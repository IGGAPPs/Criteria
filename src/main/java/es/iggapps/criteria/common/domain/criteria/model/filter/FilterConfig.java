package es.iggapps.criteria.common.domain.criteria.model.filter;

import java.util.Set;

public record FilterConfig(Type type, Set<Operator> operators, boolean isList) {

  private static final String MESSAGE_TYPE_CANNOT_BE_NULL = "El tipo no puede ser nulo.";
  private static final String MESSAGE_OPERATORS_CANNOT_BE_NULL_OR_EMPTY =
      "Los operadores no pueden ser nulos o vacíos.";

  public FilterConfig {
    if (type == null) {
      throw new IllegalArgumentException(MESSAGE_TYPE_CANNOT_BE_NULL);
    }
    if (operators == null || operators.isEmpty()) {
      throw new IllegalArgumentException(MESSAGE_OPERATORS_CANNOT_BE_NULL_OR_EMPTY);
    }
  }

  public FilterConfig(final Type type, final Set<Operator> operators) {
    this(type, operators, false);
  }

  public static FilterConfig listOf(final Type elementType, final Set<Operator> operators) {
    return new FilterConfig(elementType, operators, true);
  }
}
