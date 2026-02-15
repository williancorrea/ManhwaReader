package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("system/configuration-group")
public class SystemConfigurationGroupResource {

  private final SystemConfigurationGroupService service;

  public SystemConfigurationGroupResource(@Lazy SystemConfigurationGroupService service) {
    this.service = service;
  }

  @GetMapping()
  public ResponseEntity<List<SystemConfigurationGroupOutput>> findAll() {
    var items = service.findAll().stream().map(SystemConfigurationGroupOutput::new).toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SystemConfigurationGroupOutput> findById(@PathVariable UUID id) {
    var item = service.findById(id).map(SystemConfigurationGroupOutput::new).orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
