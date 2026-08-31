package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;

public class IntegerParser implements ValueParser<Integer> {

  private static final String MESSAGE_VALUE_NOT_AN_INTEGER =
      "El filtro '%s' contiene algún valor que no es un número entero válido.";

  @Override
  public Integer parse(final String value, final String fieldName) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_AN_INTEGER.formatted(fieldName), ex);
    }
  }
}
