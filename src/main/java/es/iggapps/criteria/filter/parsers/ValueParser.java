package es.iggapps.criteria.filter.parsers;

import es.iggapps.criteria.exception.CriteriaValidationException;
import java.util.function.Function;

@FunctionalInterface
public interface ValueParser<E> {

  E parse(String value, String fieldName);

  static <E> ValueParser<E> of(final Function<String, E> constructor) {
    return (value, fieldName) -> {
      try {
        return constructor.apply(value);
      } catch (final Exception ex) {
        final String message = ex.getMessage() != null
            ? ex.getMessage()
            : "El filtro '%s' no pudo ser transformado.".formatted(fieldName);
        throw new CriteriaValidationException(message, ex);
      }
    };
  }
}
