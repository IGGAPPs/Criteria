package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;
import java.math.BigDecimal;

public class BigDecimalParser implements ValueParser<BigDecimal> {

  private static final String MESSAGE_VALUE_NOT_A_BIG_DECIMAL =
      "El filtro '%s' contiene algún valor que no es un número decimal (BigDecimal) válido.";

  @Override
  public BigDecimal parse(final String value, final String fieldName) {
    try {
      return new BigDecimal(value);
    } catch (NumberFormatException ex) {
      throw new CriteriaValidationException(MESSAGE_VALUE_NOT_A_BIG_DECIMAL.formatted(fieldName), ex);
    }
  }
}
