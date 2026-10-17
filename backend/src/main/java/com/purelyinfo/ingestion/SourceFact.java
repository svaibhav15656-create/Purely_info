package com.purelyinfo.ingestion;

import java.time.Instant;
import java.util.Map;

public record SourceFact(String sourceId, String sourceUrl, Instant retrievedAt, String confidence,
                        String entityName, String factType, Map<String, Object> data, boolean sample) {}
