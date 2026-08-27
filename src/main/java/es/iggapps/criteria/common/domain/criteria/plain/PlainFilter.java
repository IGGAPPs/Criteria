package es.iggapps.criteria.common.domain.criteria.plain;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlainFilter {

  private static final String MESSAGE_OPERATOR_INVALID =
      "El operador no se reconoce como válido para el filtro '%s'. "
          + "La lista de operadores válidos es [%s]";

  private final Field field;
  private final Operator operator;
  private final Object value;
  private final Type type;
  private final boolean isList;

  public static PlainFilter of(final String field, final String operator, final Object value,
      final Type type, final boolean isList, final Set<Operator> allowedOperators) {
    final Operator operator1;
    final Set<String> operatorNameWhiteList = allowedOperators.stream()
        .map(Operator::name)
        .collect(Collectors.toUnmodifiableSet());
    final String formattedOperatorNameWhiteList = operatorNameWhiteList.stream()
        .map(operatorName -> "'" + operatorName + "'")
        .collect(Collectors.joining(", "));
    final String operatorInvalidMessage = MESSAGE_OPERATOR_INVALID
        .formatted(field, formattedOperatorNameWhiteList);

    try {
      operator1 = Operator.fromString(operator);
    } catch (IllegalArgumentException ex) {
      throw new CriteriaException(operatorInvalidMessage, ex);
    }
    if (!allowedOperators.contains(operator1)) {
      throw new CriteriaException(operatorInvalidMessage);
    }
    return new PlainFilter(Field.of(field), operator1, value, type, isList);
  }

  public static PlainFilter of(final String field, final Operator operator, final Object value,
      final Type type, final boolean isList) {
    return new PlainFilter(Field.of(field), operator, value, type, isList);
  }

  public static PlainFilter of(final String field, final Operator operator, final Object value,
      final Type type) {
    return new PlainFilter(Field.of(field), operator, value, type, false);
  }
}
