package es.iggapps.criteria.filter;

public enum Operator {

  EQ,
  FZ,
  GTE,
  LTE,
  CONTAINS_ALL;


  public static Operator fromString(final String operator) {
    for (Operator operator1 : Operator.values()) {
      if (operator1.name().equalsIgnoreCase(operator)) {
        return operator1;
      }
    }
    throw new IllegalArgumentException();
  }
}
