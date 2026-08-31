package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;

public class IntegerParser implements ValueParser<Integer> {

  private static final String MESSAGE_VALUE_NOT_AN_INTEGER =
      "El filtro '%s' contiene algún valor que no es un número entero válido.";

  @Override
  public Integer parse(final String value, final String fieldName) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_AN_INTEGER.formatted(fieldName), ex);
    }
  }
}
