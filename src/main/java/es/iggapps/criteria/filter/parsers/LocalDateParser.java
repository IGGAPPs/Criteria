package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateParser implements ValueParser<LocalDate> {

  private static final String MESSAGE_VALUE_NOT_A_DATE =
      "El filtro '%s' contiene algún valor que no es una fecha válida. Se esperaba formato yyyy-MM-dd.";

  @Override
  public LocalDate parse(final String value, final String fieldName) {
    try {
      return LocalDate.parse(value);
    } catch (DateTimeParseException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_A_DATE.formatted(fieldName), ex);
    }
  }
}
