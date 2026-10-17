package com.purelyinfo.ingestion;

import java.util.List;

public interface DataSourceAdapter {
  String id();
  boolean isEnabled();
  List<SourceFact> fetch();
  boolean validate(Object response);
  List<SourceFact> mapToEntities(Object response);
  default String sourceUrl() { return ""; }
  default boolean requiresKey() { return true; }
}
