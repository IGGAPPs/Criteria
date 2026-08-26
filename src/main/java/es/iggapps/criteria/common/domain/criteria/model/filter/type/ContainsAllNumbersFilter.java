package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.Arrays;
import java.util.List;

public final class ContainsAllNumbersFilter extends Filter<List<Integer>> {

  public static final Schema TYPE = Schema.CONTAINS_ALL_NUMBERS;

  private static final String MESSAGE_INVALID_FORMAT =
      "El valor del filtro '%s' debe tener formato '[valor1,valor2,...]'.";
  private static final String MESSAGE_EMPTY_LIST =
      "La lista de valores del filtro '%s' no puede estar vacía.";
  private static final String MESSAGE_VALUE_NOT_A_NUMBER =
      "El filtro '%s' contiene algún valor no numérico.";

  private ContainsAllNumbersFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<List<Integer>> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new ContainsAllNumbersFilter(field, plainFilterList);
  }

  @Override
  protected List<Integer> configValueParsing(final String value) {
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
        .map(s -> {
          try {
            return Integer.parseInt(s);
          } catch (NumberFormatException ex) {
            throw new CriteriaException(
                MESSAGE_VALUE_NOT_A_NUMBER.formatted(field.getField()), ex);
          }
        })
        .toList();
  }
}
