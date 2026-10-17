package com.purelyinfo.admin;
import com.purelyinfo.ingestion.SourceRegistry;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
@RestController
public class SampleController { private final SourceRegistry sources; public SampleController(SourceRegistry sources){this.sources=sources;} @GetMapping("/api/sample/companies") public Object sample(){return Map.of("label","SAMPLE DATA — fictional examples for development only","companies",sources.sampleData());} }
