package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.Arrays;
import java.util.List;

public final class ContainsAllStringsFilter extends Filter<List<String>> {

  public static final Schema TYPE = Schema.CONTAINS_ALL_STRINGS;

  private static final String MESSAGE_INVALID_FORMAT =
      "El valor del filtro '%s' debe tener formato '[valor1,valor2,...]'.";
  private static final String MESSAGE_EMPTY_LIST =
      "La lista de valores del filtro '%s' no puede estar vacía.";

  private ContainsAllStringsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<List<String>> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new ContainsAllStringsFilter(field, plainFilterList);
  }

  @Override
  protected List<String> configValueParsing(final String value) {
    final String trimmed = value.strip();
    if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
      throw new CriteriaException(MESSAGE_INVALID_FORMAT.formatted(field.getField()));
    }
    final String inner = trimmed.substring(1, trimmed.length() - 1).strip();
    if (inner.isBlank()) {
      throw new CriteriaException(MESSAGE_EMPTY_LIST.formatted(field.getField()));
    }
    return Arrays.stream(inner.split(","))
        .map(String::strip)
        .toList();
  }
}
