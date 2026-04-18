package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.time.OffsetDateTime;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.data.jpa.domain.Specification;

public class ScanlatorSynchronizationErrorSpecification {

  private ScanlatorSynchronizationErrorSpecification() {
  }

  public static Specification<ScanlatorSynchronizationError> withSynchronization(SynchronizationOriginType origin) {
    return (root, query, cb) ->
        origin == null ? null : cb.equal(root.get("scanlator").get("synchronization"), origin);
  }

  public static Specification<ScanlatorSynchronizationError> withExternalWorkNameLike(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank()) return null;
      return cb.like(cb.lower(root.get("externalWorkName")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<ScanlatorSynchronizationError> withExternalWorkId(String externalWorkId) {
    return (root, query, cb) -> {
      if (externalWorkId == null || externalWorkId.isBlank()) return null;
      return cb.equal(root.get("externalWorkId"), externalWorkId);
    };
  }

  public static Specification<ScanlatorSynchronizationError> withErrorMessageLike(String message) {
    return (root, query, cb) -> {
      if (message == null || message.isBlank()) return null;
      return cb.like(cb.lower(root.get("errorMessage")), "%" + message.toLowerCase() + "%");
    };
  }

  public static Specification<ScanlatorSynchronizationError> withCreatedAtFrom(OffsetDateTime from) {
    return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
  }

  public static Specification<ScanlatorSynchronizationError> withCreatedAtTo(OffsetDateTime to) {
    return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
  }

  public static Specification<ScanlatorSynchronizationError> onlyOrphans(Boolean orphansOnly) {
    return (root, query, cb) ->
        Boolean.TRUE.equals(orphansOnly) ? cb.isNull(root.get("workId")) : null;
  }

  public static Specification<ScanlatorSynchronizationError> fromFilter(SyncErrorFilter filter) {
    return Specification.allOf(
        withSynchronization(filter.synchronization()),
        withExternalWorkNameLike(filter.externalWorkName()),
        withExternalWorkId(filter.externalWorkId()),
        withErrorMessageLike(filter.errorMessage()),
        withCreatedAtFrom(filter.from()),
        withCreatedAtTo(filter.to()),
        onlyOrphans(filter.orphansOnly())
    );
  }
}
