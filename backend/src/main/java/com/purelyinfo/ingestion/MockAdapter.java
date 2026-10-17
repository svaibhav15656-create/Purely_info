package com.purelyinfo.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MockAdapter implements DataSourceAdapter {
  private final List<SourceFact> samples = List.of(
      fact("Acme Solar Systems", "ACME", "Renewable energy equipment", "India"),
      fact("Northstar Robotics", "NSTAR", "Industrial robotics", "United States"));
  public MockAdapter(ObjectMapper ignored) {}
  private SourceFact fact(String name, String ticker, String description, String country) {
    return new SourceFact(id(), "", Instant.EPOCH, "sample data", name, "company", Map.of("ticker", ticker, "description", description, "country", country, "label", "SAMPLE DATA"), true);
  }
  @Override public String id() { return "mock"; }
  @Override public boolean isEnabled() { return true; }
  @Override public boolean requiresKey() { return false; }
  @Override public List<SourceFact> fetch() { return samples; }
  @Override public boolean validate(Object response) { return true; }
  @Override public List<SourceFact> mapToEntities(Object response) { return samples; }
  @Override public String sourceUrl() { return ""; }
}
