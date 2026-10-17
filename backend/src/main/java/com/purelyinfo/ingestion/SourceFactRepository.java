package com.purelyinfo.ingestion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SourceFactRepository extends JpaRepository<SourceFactEntity, UUID> {}
