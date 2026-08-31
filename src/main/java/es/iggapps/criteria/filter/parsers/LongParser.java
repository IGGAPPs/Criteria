package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;

public class LongParser implements ValueParser<Long> {

  private static final String MESSAGE_VALUE_NOT_A_LONG =
      "El filtro '%s' contiene algún valor que no es un número long válido.";

  @Override
  public Long parse(final String value, final String fieldName) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_A_LONG.formatted(fieldName), ex);
    }
  }
}
