package com.purelyinfo.ingestion;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SourceRegistry {
  private final Adapters adapters;
  private final MockAdapter mock;
  @Autowired(required=false) private SourceFactRepository factRepository;
  private final Map<String, RunState> runs = new ConcurrentHashMap<>();
  public SourceRegistry(Adapters adapters, MockAdapter mock) { this.adapters=adapters;this.mock=mock; }
  public java.util.List<DataSourceAdapter> all() { var all = new java.util.ArrayList<>(adapters.all()); all.add(mock); return all; }
  public java.util.List<Map<String,Object>> statuses() { return all().stream().map(a -> { RunState r=runs.get(a.id()); String status = a.id().equals("mock") ? "Healthy" : !a.isEnabled() ? keyMissing(a) ? "Key missing" : "Disabled" : r==null ? "Healthy" : r.error==null ? "Healthy" : "Error"; return Map.<String,Object>of("id",a.id(),"status",status,"enabled",a.isEnabled(),"lastRun",r==null?"":r.at.toString(),"lastError",r==null||r.error==null?"":r.error); }).toList(); }
  private boolean keyMissing(DataSourceAdapter a) { String key=envKey(a.id()); return a.requiresKey() && !key.isBlank() && System.getenv().getOrDefault(key, "").isBlank(); }
  private String envKey(String id) { return switch(id){case "finnhub"->"FINNHUB_API_KEY";case "alpha-vantage"->"ALPHAVANTAGE_API_KEY";case "twelve-data"->"TWELVEDATA_API_KEY";case "fmp"->"FMP_API_KEY";case "openfigi"->"OPENFIGI_API_KEY";case "opensanctions"->"OPENSANCTIONS_API_KEY";case "opencorporates"->"OPENCORPORATES_API_TOKEN";case "comtrade"->"COMTRADE_API_KEY";default->"";}; }
  public void run(String id) { DataSourceAdapter adapter=all().stream().filter(a->a.id().equals(id)).findFirst().orElseThrow(()->new IllegalArgumentException("Unknown source")); Instant now=Instant.now(); try { var facts=adapter.fetch(); if(factRepository!=null) factRepository.saveAll(facts.stream().map(SourceFactEntity::from).toList()); runs.put(id,new RunState(now,null)); } catch(Exception e) { runs.put(id,new RunState(now,e.getMessage()));throw e; } }
  public java.util.List<SourceFact> sampleData() { return mock.fetch(); }
  @Scheduled(fixedDelayString = "${sources.schedule-delay-ms:3600000}", initialDelayString = "${sources.initial-delay-ms:30000}")
  public void scheduledRefresh() { for (DataSourceAdapter adapter : all()) if (adapter.isEnabled() && !adapter.id().equals("mock")) { try { run(adapter.id()); } catch (RuntimeException ignored) { /* status retains a sanitized error for the admin view */ } } }
  private record RunState(Instant at,String error){}
}
