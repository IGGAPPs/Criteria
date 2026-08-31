package es.iggapps.criteria.sort;

public enum Order {

  ASC,
  DESC;

  public static Order fromString(final String order) {
    for (Order order1 : Order.values()) {
      if (order1.name().equalsIgnoreCase(order)) {
        return order1;
      }
    }
    throw new IllegalArgumentException();
  }
}
