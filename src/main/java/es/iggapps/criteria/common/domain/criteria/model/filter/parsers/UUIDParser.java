package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.UUID;

public class UUIDParser implements ValueParser<UUID> {

  private static final String MESSAGE_VALUE_NOT_UUID =
      "El filtro '%s' contiene un valor que no es de tipo UUID.";

  @Override
  public UUID parse(final String value, final String fieldName) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_UUID.formatted(fieldName), ex);
    }
  }
}
