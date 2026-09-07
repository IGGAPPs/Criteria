package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.Arrays;
import java.util.List;

public final class ContainsAnyStringsFilter extends Filter<List<String>> {

  public static final Schema TYPE = Schema.CONTAINS_ANY_STRINGS;

  private static final String MESSAGE_VALUE_EMPTY =
      "La lista de valores del filtro '%s' contiene algún valor vacío.";

  private ContainsAnyStringsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<List<String>> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new ContainsAnyStringsFilter(field, plainFilterList);
  }

  @Override
  protected List<String> configValueParsing(final String value) {
    final var elements = Arrays.stream(value.split(",", -1))
        .map(String::strip)
        .toList();
    if (elements.stream().anyMatch(String::isEmpty)) {
      throw new CriteriaException(MESSAGE_VALUE_EMPTY.formatted(field.getField()));
    }
    return elements;
  }
}
