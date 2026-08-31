package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;

public class FloatParser implements ValueParser<Float> {

  private static final String MESSAGE_VALUE_NOT_A_FLOAT =
      "El filtro '%s' contiene algún valor que no es un número decimal (float) válido.";

  @Override
  public Float parse(final String value, final String fieldName) {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_A_FLOAT.formatted(fieldName), ex);
    }
  }
}
