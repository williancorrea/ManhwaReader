package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.scanlator.error.dto.DeleteSyncErrorsInput;
import dev.williancorrea.manhwa.reader.features.scanlator.error.dto.SyncErrorOutput;
import dev.williancorrea.manhwa.reader.features.scanlator.error.dto.SyncErrorSummaryOutput;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sync-errors")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
@RequiredArgsConstructor
public class ScanlatorSynchronizationErrorResource {

  private final ScanlatorSynchronizationErrorService service;

  @GetMapping
  public ResponseEntity<Page<SyncErrorOutput>> list(
      @RequestParam(required = false) SynchronizationOriginType synchronization,
      @RequestParam(required = false) String externalWorkName,
      @RequestParam(required = false) String externalWorkId,
      @RequestParam(required = false) String errorMessage,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
      @RequestParam(required = false) Boolean orphansOnly,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt,desc") String sort) {

    size = Math.min(Math.max(size, 1), 50);
    var filter = new SyncErrorFilter(
        synchronization, externalWorkName, externalWorkId, errorMessage, from, to, orphansOnly);
    return ResponseEntity.ok(service.list(filter, PageRequest.of(page, size, parseSort(sort))));
  }

  @GetMapping("/summary")
  public ResponseEntity<SyncErrorSummaryOutput> summary() {
    return ResponseEntity.ok(service.summary());
  }

  @GetMapping("/{id}")
  public ResponseEntity<SyncErrorOutput> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(service.findById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteMany(@RequestBody @Valid DeleteSyncErrorsInput input) {
    service.deleteMany(input.ids());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/older-than")
  public ResponseEntity<Void> purge(@RequestParam(defaultValue = "30") int days) {
    service.deleteOlderThan(days);
    return ResponseEntity.noContent().build();
  }

  private Sort parseSort(String sort) {
    if (sort == null || sort.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
    var parts = sort.split(",");
    var property = parts[0].trim();
    var direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
        ? Sort.Direction.ASC : Sort.Direction.DESC;
    if (!isAllowedSortProperty(property)) {
      property = "createdAt";
    }
    return Sort.by(direction, property);
  }

  private boolean isAllowedSortProperty(String property) {
    return "createdAt".equals(property)
        || "externalWorkName".equals(property)
        || "scanlator.name".equals(property);
  }
}
