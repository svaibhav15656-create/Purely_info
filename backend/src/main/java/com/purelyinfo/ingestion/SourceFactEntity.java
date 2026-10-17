package com.purelyinfo.ingestion;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="source_facts")
public class SourceFactEntity {
  @Id @GeneratedValue private UUID id;
  @Column(name="source_id",nullable=false) private String sourceId;
  @Column(name="entity_name",nullable=false) private String entityName;
  @Column(name="fact_type",nullable=false) private String factType;
  @JdbcTypeCode(SqlTypes.JSON) @Column(name="payload",nullable=false,columnDefinition="jsonb") private java.util.Map<String,Object> payload;
  @Column(name="source_url") private String sourceUrl;
  @Column(name="retrieved_at",nullable=false) private Instant retrievedAt;
  @Column(nullable=false) private String confidence;
  @Column(nullable=false) private boolean sample;
  protected SourceFactEntity() {}
  static SourceFactEntity from(SourceFact fact) { var entity=new SourceFactEntity(); entity.sourceId=fact.sourceId(); entity.entityName=fact.entityName().isBlank()?"Unresolved source record":fact.entityName(); entity.factType=fact.factType(); entity.payload=fact.data(); entity.sourceUrl=fact.sourceUrl(); entity.retrievedAt=fact.retrievedAt(); entity.confidence=fact.confidence(); entity.sample=fact.sample(); return entity; }
}
