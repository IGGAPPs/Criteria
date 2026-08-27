package es.iggapps.criteria.common.domain.criteria.model.filter;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Filters {

  private final Map<Field, Filter<?>> filters;

  private Filters(final List<PlainFilter> plainFilterList) {

    final Map<Field, List<PlainFilter>> plainFilterListByField = new HashMap<>();
    plainFilterList.forEach(plainFilter -> plainFilterListByField
        .computeIfAbsent(plainFilter.getField(), k -> new ArrayList<>())
        .add(plainFilter));

    final Map<Field, Filter<?>> filters1 = new HashMap<>();
    plainFilterListByField.forEach((field, plainFilterList1) -> filters1.put(
        field,
        new Filter<>(field, plainFilterList1)
    ));

    this.filters = Map.copyOf(filters1);
  }

  public static Filters of(final List<PlainFilter> plainFilterList) {
    return new Filters(plainFilterList);
  }

  public <T> Optional<Filter<T>> findBy(final Field field) {
    return Optional.ofNullable((Filter<T>) filters.get(field));
  }

  public Filters appendFilter(final PlainFilter plainFilter) {
    final List<PlainFilter> plainFilterList = new ArrayList<>(asList());
    plainFilterList.add(plainFilter);
    return of(plainFilterList);
  }

  public Filters removeFilter(final Field field) {
    final List<PlainFilter> plainFilterList = new ArrayList<>(asList());
    plainFilterList.removeIf(plainFilter -> plainFilter.getField().equals(field));
    return of(plainFilterList);
  }

  private List<PlainFilter> asList() {
    return filters
        .values()
        .stream()
        .map(filter -> filter
            .getOperatorValueMap()
            .entrySet()
            .stream()
            .map(entry -> PlainFilter.of(
                filter.getField().getField(),
                entry.getKey(),
                entry.getValue(),
                filter.getType(),
                filter.isList()
            ))
            .toList()
        )
        .flatMap(List::stream)
        .toList();
  }
}
