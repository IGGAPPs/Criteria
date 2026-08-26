package es.iggapps.criteria.common.domain.criteria.model.filter;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.FechaPeninsularGteFilter;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.FechaPeninsularLteFilter;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.FuzzyStringEqualsFilter;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.NaturalNumberEqualsFilter;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.StringEqualsFilter;
import es.iggapps.criteria.common.domain.criteria.model.filter.type.UUIDEqualsFilter;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.DomainException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Filters {

  private static final String MESSAGE_FILTER_TYPE_UNKNOWN =
      "No se reconoce el tipo de filtro dentro del conjunto de tipos establecido.";

  private final Map<Field, Filter<?>> filters;

  private Filters(final List<PlainFilter> plainFilterList) {

    final Map<Field, List<PlainFilter>> plainFilterListByField = new HashMap<>();
    plainFilterList.forEach(plainFilter -> plainFilterListByField
        .computeIfAbsent(plainFilter.getField(), k -> new ArrayList<>())
        .add(plainFilter));

    final Map<Field, Filter<?>> filters1 = new HashMap<>();
    plainFilterListByField.forEach((field, plainFilterList1) -> filters1.put(
        field,
        convertToTypedFilter(
            field,
            plainFilterList1.getFirst().getSchema(),
            plainFilterList1
        )
    ));

    this.filters = Map.copyOf(filters1);
  }

  public static Filters of(final List<PlainFilter> plainFilterList) {
    return new Filters(plainFilterList);
  }

  private Filter<?> convertToTypedFilter(
      final Field field,
      final Schema schema,
      final List<PlainFilter> plainFilterList
  ) {
    return switch (schema) {
      case Schema.STRING_EQUALS -> StringEqualsFilter.of(field, plainFilterList);
      case Schema.UUID_EQUALS -> UUIDEqualsFilter.of(field, plainFilterList);
      case Schema.NATURAL_NUMBER_EQUALS -> NaturalNumberEqualsFilter.of(field, plainFilterList);
      case Schema.FECHA_PENINSULAR_GTE -> FechaPeninsularGteFilter.of(field, plainFilterList);
      case Schema.FECHA_PENINSULAR_LTE -> FechaPeninsularLteFilter.of(field, plainFilterList);
      case Schema.FUZZY_STRING_EQUALS -> FuzzyStringEqualsFilter.of(field, plainFilterList);
      default -> throw new DomainException(MESSAGE_FILTER_TYPE_UNKNOWN);
    };
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
                filter.getType()
            ))
            .toList()
        )
        .flatMap(List<PlainFilter>::stream)
        .toList();
  }
}
