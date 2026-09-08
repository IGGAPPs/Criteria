package es.iggapps.criteria.common.domain.criteria.plain;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.List;
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
  private final Schema schema;

  public static PlainFilter of(final String field, final String operator, final Object value,
      final Schema schema) {
    validateOperator(field, operator, schema);
    final Operator operator1 = Operator.fromString(operator);
    return new PlainFilter(Field.of(field), operator1, value, schema);
  }

  public static PlainFilter of(final String field, final Operator operator, final Object value,
      final Schema schema) {
    return new PlainFilter(Field.of(field), operator, value, schema);
  }

  public static void validateOperator(final String field, final String operator,
      final Schema schema) {
    final List<Operator> operatorWhiteList = schema.getOperatorWhiteList();
    final List<String> operatorNameWhiteList = operatorWhiteList.stream()
        .map(Operator::name)
        .toList();
    final String formattedOperatorNameWhiteList = operatorNameWhiteList.stream()
        .map(operatorName -> "'" + operatorName + "'")
        .collect(Collectors.joining(", "));
    final String operatorInvalidMessage = MESSAGE_OPERATOR_INVALID
        .formatted(field, formattedOperatorNameWhiteList);

    try {
      final Operator parsedOperator = Operator.fromString(operator);
      if (!operatorWhiteList.contains(parsedOperator)) {
        throw new CriteriaException(operatorInvalidMessage);
      }
    } catch (IllegalArgumentException ex) {
      throw new CriteriaException(operatorInvalidMessage, ex);
    }
  }
}
