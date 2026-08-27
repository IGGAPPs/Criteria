package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;
import es.iggapps.criteria.common.domain.exception.DomainException;
import es.iggapps.criteria.common.domain.valueobject.FechaPeninsular;

public class FechaPeninsularParser implements ValueParser<FechaPeninsular> {

  private static final String MESSAGE_VALUE_NOT_A_DATE =
      "El valor indicado en el filtro '%s' no tiene un formato de fecha correcto (yyyy-mm-dd) "
          + "o no es una fecha existente";

  @Override
  public FechaPeninsular parse(final String value, final String fieldName) {
    try {
      return FechaPeninsular.fromString(value);
    } catch (DomainException ex) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_A_DATE.formatted(fieldName), ex);
    }
  }
}
