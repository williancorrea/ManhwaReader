package dev.williancorrea.manhwa.reader.system;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("system/configuration")
public class SystemConfigurationResource {

  private final SystemConfigurationService systemConfigurationService;

  public SystemConfigurationResource(@Lazy SystemConfigurationService systemConfigurationService) {
    this.systemConfigurationService = systemConfigurationService;
  }

  @GetMapping()
  public ResponseEntity<List<SystemConfigurationOutput>> findAll() {
    var items = systemConfigurationService.findAll()
        .stream().map(SystemConfigurationOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SystemConfigurationOutput> findById(@PathVariable UUID id) {
    var item = systemConfigurationService.findById(id)
        .map(SystemConfigurationOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @GetMapping("/group")
  public ResponseEntity<List<SystemConfigurationOutput>> findAllByGroup(@RequestParam UUID groupId) {
    var group = new SystemConfigurationGroup();
    group.setId(groupId);
    var items = systemConfigurationService.findAllByGroup(group)
        .stream().map(SystemConfigurationOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<SystemConfigurationOutput> create(@RequestBody @Valid SystemConfigurationInput input) {
    var entity = toEntity(input);
    var saved = systemConfigurationService.save(entity);
    return ResponseEntity.ok(new SystemConfigurationOutput(saved));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SystemConfigurationOutput> update(@PathVariable UUID id, @RequestBody @Valid SystemConfigurationInput input) {
    if (!systemConfigurationService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = systemConfigurationService.save(entity);
    return ResponseEntity.ok(new SystemConfigurationOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!systemConfigurationService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    systemConfigurationService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private SystemConfiguration toEntity(SystemConfigurationInput input) {
    var entity = new SystemConfiguration();
    if (input.getGroupId() != null) {
      var group = new SystemConfigurationGroup();
      group.setId(input.getGroupId());
      entity.setGroup(group);
    }
    entity.setDescription(input.getDescription());
    entity.setReference(input.getReference());
    entity.setValue(input.getValue());
    entity.setActive(input.isActive());
    return entity;
  }
}

