package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;

public class BooleanParser implements ValueParser<Boolean> {

  private static final String MESSAGE_VALUE_NOT_A_BOOLEAN =
      "El filtro '%s' contiene algún valor que no es un booleano válido. Se esperaba 'true' o 'false'.";

  @Override
  public Boolean parse(final String value, final String fieldName) {
    return switch (value.toLowerCase()) {
      case "true" -> true;
      case "false" -> false;
      default -> throw new CriteriaException(MESSAGE_VALUE_NOT_A_BOOLEAN.formatted(fieldName));
    };
  }
}
