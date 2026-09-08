package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.List;

public final class NaturalNumberEqualsFilter extends Filter<Integer> {

  public static final Schema TYPE = Schema.NATURAL_NUMBER_EQUALS;

  private static final String MESSAGE_VALUE_IN_NOT_A_NUMBER = "El filtro '%s' contiene algún valor no numérico.";
  private static final String MESSAGE_VALUE_NOT_NATURAL = "El filtro '%s' contiene algún valor igual o inferior a 0.";

  private NaturalNumberEqualsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<Integer> of(final Field field, final List<PlainFilter> plainFilterList) {
    return new NaturalNumberEqualsFilter(field, plainFilterList);
  }

  @Override
  protected Integer configValueParsing(final Object value) {
    final String strValue = (String) value;
    int iValue;
    try {
      iValue = Integer.parseInt(strValue);
    } catch (NumberFormatException ex) {
      throw new CriteriaException(MESSAGE_VALUE_IN_NOT_A_NUMBER.formatted(field.getField()), ex);
    }
    if (iValue <= 0) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_NATURAL.formatted(field.getField()));
    }
    return iValue;
  }
}
