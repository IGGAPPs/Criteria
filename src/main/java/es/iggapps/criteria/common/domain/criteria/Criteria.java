package es.iggapps.criteria.common.domain.criteria;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filters;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.page.PageNumber;
import es.iggapps.criteria.common.domain.criteria.model.page.PageSize;
import es.iggapps.criteria.common.domain.criteria.model.sort.Sort;
import es.iggapps.criteria.common.domain.criteria.model.sort.Sorts;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Criteria {

  private final Filters filters;
  private final Sorts sorts;
  @Getter
  private final PageNumber pageNumber;
  @Getter
  private final PageSize pageSize;

  public static Criteria of(final Filters filters, final Sorts sorts, final PageNumber pageNumber,
      final PageSize pageSize) {
    return new Criteria(filters, sorts, pageNumber, pageSize);
  }

  public <T> Optional<Filter<T>> findFilterBy(final String field) {
    return filters.findBy(Field.of(field));
  }

  public <T> Optional<Sort> findSortBy(final String field) {
    return sorts.findBy(Field.of(field));
  }

  public Optional<PlainFilter> findPlainFilterBy(final String field, final Operator operator) {
    return findFilterBy(field).flatMap(filter -> filter
        .withOperator(operator)
        .map(value -> PlainFilter.of(
            filter.getField().getField(),
            operator,
            value,
            filter.getType()
        )));
  }

  public boolean existsFilterBy(final String field) {
    return findFilterBy(field).isPresent();
  }

  public <T> Filter<T> getFilterOrElseThrow(final String field) {
    return (Filter<T>) findFilterBy(field).orElseThrow();
  }

  public Criteria appendFilter(final PlainFilter plainFilter) {
    return of(
        filters.appendFilter(plainFilter),
        sorts,
        pageNumber,
        pageSize
    );
  }

  public Criteria removeFilter(final String field) {
    return of(
        filters.removeFilter(Field.of(field)),
        sorts,
        pageNumber,
        pageSize
    );
  }
}
