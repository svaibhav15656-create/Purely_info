package com.purelyinfo.admin;

import com.purelyinfo.ingestion.SourceRegistry;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/data-sources")
public class DataSourcesController {
  private final SourceRegistry registry;
  public DataSourcesController(SourceRegistry registry){this.registry=registry;}
  @GetMapping public Object list(){return Map.of("sources",registry.statuses());}
  @PostMapping("/{id}/run") public Object run(@PathVariable String id){try{registry.run(id);return Map.of("status","Healthy","id",id);}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Source not found");}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Source refresh failed");}}
}
