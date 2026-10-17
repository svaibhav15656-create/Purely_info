package com.purelyinfo.ingestion;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class MockAdapterTest {
  private WireMockServer server;
  @BeforeEach void start() { server = new WireMockServer(options().dynamicPort()); server.start(); }
  @AfterEach void stop() { if (server != null) server.stop(); }
  @Test void recordedJsonFixtureAndMockCompaniesAreAvailableWithoutKeys() throws Exception {
    server.stubFor(get(urlEqualTo("/sample.json")).willReturn(okJson("{\"ok\":true}")));
    var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(server.url("/sample.json"))).GET().build(), HttpResponse.BodyHandlers.ofString());
    var adapter = new MockAdapter(new ObjectMapper());
    assertEquals(200, response.statusCode());
    assertTrue(adapter.validate(new ObjectMapper().readTree(response.body())));
    assertTrue(adapter.isEnabled());
    assertTrue(adapter.validate(server.getOptions().httpsSettings().enabled() ? "{}" : "recorded response"));
    assertEquals(2, adapter.fetch().size());
    assertTrue(adapter.fetch().stream().allMatch(SourceFact::sample));
    assertTrue(adapter.fetch().stream().allMatch(f -> "SAMPLE DATA".equals(f.data().get("label"))));
  }
}
