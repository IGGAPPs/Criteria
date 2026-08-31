package es.iggapps.criteria.filter;

import es.iggapps.criteria.filter.parsers.ValueParser;
import java.util.Set;

public record FilterConfig(Type type, Set<Operator> operators, boolean isList,
                           String pattern, ValueParser<?> customParser) {

  private static final String MESSAGE_TYPE_CANNOT_BE_NULL = "El tipo no puede ser nulo.";
  private static final String MESSAGE_OPERATORS_CANNOT_BE_NULL_OR_EMPTY =
      "Los operadores no pueden ser nulos o vacíos.";
  private static final String MESSAGE_PATTERN_ONLY_FOR_STRING =
      "El pattern solo está permitido para Type.STRING.";
  private static final String MESSAGE_CUSTOM_PARSER_ONLY_FOR_STRING =
      "El customParser solo está permitido para Type.STRING.";
  private static final String MESSAGE_PATTERN_AND_CUSTOM_PARSER_EXCLUSIVE =
      "No se pueden usar pattern y customParser juntos.";

  public FilterConfig {
    if (type == null) {
      throw new IllegalArgumentException(MESSAGE_TYPE_CANNOT_BE_NULL);
    }
    if (operators == null || operators.isEmpty()) {
      throw new IllegalArgumentException(MESSAGE_OPERATORS_CANNOT_BE_NULL_OR_EMPTY);
    }
    if (pattern != null && type != Type.STRING) {
      throw new IllegalArgumentException(MESSAGE_PATTERN_ONLY_FOR_STRING);
    }
    if (customParser != null && type != Type.STRING) {
      throw new IllegalArgumentException(MESSAGE_CUSTOM_PARSER_ONLY_FOR_STRING);
    }
    if (pattern != null && customParser != null) {
      throw new IllegalArgumentException(MESSAGE_PATTERN_AND_CUSTOM_PARSER_EXCLUSIVE);
    }
  }

  public FilterConfig(final Type type, final Set<Operator> operators) {
    this(type, operators, false, null, null);
  }

  public FilterConfig(final Type type, final Set<Operator> operators, final boolean isList) {
    this(type, operators, isList, null, null);
  }

  public FilterConfig(final Type type, final Set<Operator> operators,
      final ValueParser<?> customParser) {
    this(type, operators, false, null, customParser);
  }

  public static FilterConfig withPattern(final Type type, final Set<Operator> operators,
      final String pattern) {
    return new FilterConfig(type, operators, false, pattern, null);
  }

  public static FilterConfig listOfPattern(final Type type, final Set<Operator> operators,
      final String pattern) {
    return new FilterConfig(type, operators, true, pattern, null);
  }

  public static FilterConfig listOf(final Type elementType, final Set<Operator> operators) {
    return new FilterConfig(elementType, operators, true, null, null);
  }

  public static FilterConfig listOf(final Type elementType, final Set<Operator> operators,
      final ValueParser<?> customParser) {
    return new FilterConfig(elementType, operators, true, null, customParser);
  }
}
