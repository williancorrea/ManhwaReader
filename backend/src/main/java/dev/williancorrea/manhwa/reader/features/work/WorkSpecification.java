package dev.williancorrea.manhwa.reader.features.work;

import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaExpression;
import org.springframework.data.jpa.domain.Specification;

public class WorkSpecification {

  public static Specification<Work> withTitle(String title) {
    return (root, query, cb) -> {
      if (title == null || title.isBlank()) return null;
      Subquery<Long> sub = query.subquery(Long.class);
      var titleRoot = sub.from(WorkTitle.class);
      HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) cb;
      @SuppressWarnings("unchecked")
      JpaExpression<String> titleExpr = (JpaExpression<String>) titleRoot.<String>get("title");
      sub.select(cb.literal(1L))
          .where(
              cb.equal(titleRoot.get("work"), root),
              cb.like(cb.upper(hcb.cast(titleExpr, String.class)), "%" + title.toUpperCase() + "%")
          );
      return cb.exists(sub);
    };
  }

  public static Specification<Work> withType(WorkType type) {
    return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
  }

  public static Specification<Work> withPublicationDemographic(WorkPublicationDemographic demographic) {
    return (root, query, cb) -> demographic == null ? null : cb.equal(root.get("publicationDemographic"), demographic);
  }

  public static Specification<Work> withStatus(WorkStatus status) {
    return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
  }

  public static Specification<Work> withoutSynchronizationOrigin(SynchronizationOriginType origin) {
    return (root, query, cb) -> {
      if (origin == null) return null;
      Subquery<Long> subquery = query.subquery(Long.class);
      var syncRoot = subquery.from(WorkSynchronization.class);
      subquery.select(cb.literal(1L))
          .where(
              cb.equal(syncRoot.get("work"), root),
              cb.equal(syncRoot.get("origin"), origin)
          );
      return cb.not(cb.exists(subquery));
    };
  }

  public static Specification<Work> withSynchronizationOrigin(SynchronizationOriginType origin) {
    return (root, query, cb) -> {
      if (origin == null) return null;
      Subquery<Long> subquery = query.subquery(Long.class);
      var syncRoot = subquery.from(WorkSynchronization.class);
      subquery.select(cb.literal(1L))
          .where(
              cb.equal(syncRoot.get("work"), root),
              cb.equal(syncRoot.get("origin"), origin)
          );
      return cb.exists(subquery);
    };
  }

  public static Specification<Work> fromFilter(WorkCatalogFilter filter) {
    return Specification.allOf(
        withTitle(filter.title()),
        withType(filter.type()),
        withPublicationDemographic(filter.publicationDemographic()),
        withStatus(filter.status())
    );
  }
}
