package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;

public class DoubleParser implements ValueParser<Double> {

  private static final String MESSAGE_VALUE_NOT_A_DOUBLE =
      "El filtro '%s' contiene algún valor que no es un número decimal (double) válido.";

  @Override
  public Double parse(final String value, final String fieldName) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_A_DOUBLE.formatted(fieldName), ex);
    }
  }
}
