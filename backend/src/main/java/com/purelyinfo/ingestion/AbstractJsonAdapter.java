package com.purelyinfo.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractJsonAdapter implements DataSourceAdapter {
  private static final Map<String, Cached> CACHE = new ConcurrentHashMap<>();
  protected final ObjectMapper mapper;
  protected final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  protected AbstractJsonAdapter(ObjectMapper mapper) { this.mapper = mapper; }
  protected abstract String endpoint();
  protected abstract String host();
  protected String apiKey() { return ""; }
  protected String keyHeader() { return "Authorization"; }
  protected String userAgent() { return "PurelyInfo/1.0 (public-data-ingestion)"; }
  @Override public boolean isEnabled() { return Boolean.parseBoolean(System.getenv("SOURCE_" + id().toUpperCase().replaceAll("[^A-Z0-9]", "_") + "_ENABLED")) && (!requiresKey() || !apiKey().isBlank()); }
  @Override public boolean validate(Object response) { return response instanceof JsonNode n && !n.isMissingNode() && !n.isNull(); }
  @Override public List<SourceFact> fetch() {
    if (!isEnabled()) return List.of();
    Cached cached = CACHE.get(id());
    if (cached != null && cached.expiresAt > System.currentTimeMillis()) return cached.facts;
    try {
      URI uri = URI.create(endpoint());
      if (!"https".equalsIgnoreCase(uri.getScheme()) || !host().equalsIgnoreCase(uri.getHost())) throw new IllegalArgumentException("Outbound host is not allow-listed");
      HttpRequest.Builder request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).header("Accept", "application/json").header("User-Agent", userAgent()).GET();
      if (!apiKey().isBlank()) request.header(keyHeader(), apiKey());
      HttpResponse<String> response = null;
      for (int attempt = 0; attempt < 3; attempt++) {
        response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 500 && response.statusCode() != 429) break;
        if (attempt < 2) Thread.sleep(200L * (1L << attempt));
      }
      if (response.statusCode() < 200 || response.statusCode() >= 300 || response.body().length() > 2_000_000) throw new IllegalStateException("Source returned HTTP " + response.statusCode());
      JsonNode body = mapper.readTree(response.body());
      if (!validate(body)) throw new IllegalArgumentException("Response schema validation failed");
      List<SourceFact> facts = mapToEntities(body);
      CACHE.put(id(), new Cached(facts, System.currentTimeMillis() + 3_600_000));
      return facts;
    } catch (Exception e) { throw new IllegalStateException("Fetch failed for " + id(), e); }
  }
  private record Cached(List<SourceFact> facts, long expiresAt) {}
  @Override public List<SourceFact> mapToEntities(Object response) {
    return List.of(new SourceFact(id(), sourceUrl(), Instant.now(), "unverified", "", "raw_source_response", Map.of("response", response.toString()), false));
  }
}
