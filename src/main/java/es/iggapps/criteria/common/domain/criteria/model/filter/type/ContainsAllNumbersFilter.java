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
    return Arrays.stream(value.split(","))
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
