package es.iggapps.criteria.filter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Operator {

  private static final Map<String, Operator> REGISTRY = new ConcurrentHashMap<>();

  public static final Operator EQ = register("EQ");
  public static final Operator NEQ = register("NEQ");
  public static final Operator GT = register("GT");
  public static final Operator GTE = register("GTE");
  public static final Operator LT = register("LT");
  public static final Operator LTE = register("LTE");
  public static final Operator BETWEEN = register("BETWEEN");
  public static final Operator CONTAINS = register("CONTAINS");
  public static final Operator STARTS_WITH = register("STARTS_WITH");
  public static final Operator ENDS_WITH = register("ENDS_WITH");
  public static final Operator CONTAINS_ALL = register("CONTAINS_ALL");
  public static final Operator CONTAINS_ANY = register("CONTAINS_ANY");

  private final String name;

  private Operator(final String name) {
    this.name = name;
  }

  private static Operator register(final String name) {
    final Operator op = new Operator(name);
    REGISTRY.put(name.toUpperCase(), op);
    return op;
  }

  public static Operator of(final String name) {
    Objects.requireNonNull(name, "El nombre del operador no puede ser nulo");
    final String upper = name.toUpperCase();
    return REGISTRY.computeIfAbsent(upper, Operator::new);
  }

  public static Operator fromString(final String operator) {
    Objects.requireNonNull(operator, "El operador no puede ser nulo");
    final Operator op = REGISTRY.get(operator.toUpperCase());
    if (op == null) {
      throw new IllegalArgumentException(
          "Operador no reconocido: '%s'. Operadores válidos: %s"
              .formatted(operator, REGISTRY.keySet()));
    }
    return op;
  }

  public String name() {
    return name;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof Operator other)) return false;
    return name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
