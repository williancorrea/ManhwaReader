package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping()
  public ResponseEntity<SystemConfigurationGroupOutput> create(@RequestBody SystemConfigurationGroupInput input) {
    var entity = toEntity(input);
    var saved = service.save(entity);
    return ResponseEntity.ok(new SystemConfigurationGroupOutput(saved));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SystemConfigurationGroupOutput> update(@PathVariable UUID id, @RequestBody SystemConfigurationGroupInput input) {
    if (!service.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = service.save(entity);
    return ResponseEntity.ok(new SystemConfigurationGroupOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!service.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    service.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private SystemConfigurationGroup toEntity(SystemConfigurationGroupInput input) {
    var entity = new SystemConfigurationGroup();
    entity.setName(input.getName());
    entity.setDescription(input.getDescription());
    entity.setActive(input.isActive());
    return entity;
  }
}
