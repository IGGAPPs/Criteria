package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;

public class IntegerParser implements ValueParser<Integer> {

  private static final String MESSAGE_VALUE_NOT_A_NUMBER =
      "El filtro '%s' contiene algún valor no numérico.";
  private static final String MESSAGE_VALUE_NOT_NATURAL =
      "El filtro '%s' contiene algún valor igual o inferior a 0.";

  @Override
  public Integer parse(final String value, final String fieldName) {
    final int iValue;
    try {
      iValue = Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_A_NUMBER.formatted(fieldName), ex);
    }
    if (iValue <= 0) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_NATURAL.formatted(fieldName));
    }
    return iValue;
  }
}
