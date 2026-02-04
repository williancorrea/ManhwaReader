package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/group/")
  public ResponseEntity<List<SystemConfigurationOutput>> findAllByGroup(@RequestParam SystemConfigurationGroup group) {
    var items = systemConfigurationService.findAllByGroup(group)
        .stream().map(SystemConfigurationOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}