package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class OffsetDateTimeParser implements ValueParser<OffsetDateTime> {

  private static final String MESSAGE_VALUE_NOT_A_DATE_TIME =
      "El filtro '%s' contiene algún valor que no es una fecha-hora válida."
          + " Se esperaba formato ISO con zona horaria (ej: 2024-01-15T10:30:00+01:00).";

  @Override
  public OffsetDateTime parse(final String value, final String fieldName) {
    try {
      return OffsetDateTime.parse(value);
    } catch (DateTimeParseException ex) {
      throw new CriteriaException(MESSAGE_VALUE_NOT_A_DATE_TIME.formatted(fieldName), ex);
    }
  }
}
