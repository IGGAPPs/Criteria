package es.iggapps.criteria.common.domain.criteria.plain;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.sort.Order;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlainSort {

  private static final String MESSAGE_SORT_UNKNOWN_ORDER
      = "El sentido del orden del campo '%s' es desconocido. Los sentidos admitidos son 'asc' y 'desc'.";

  private final Field field;
  private final Order order;

  public static PlainSort of(final String field, final String order) {
    Order order1;
    try {
      order1 = Order.fromString(order);
    } catch (IllegalArgumentException ex) {
      throw new CriteriaException(MESSAGE_SORT_UNKNOWN_ORDER.formatted(field), ex);
    }
    return new PlainSort(Field.of(field), order1);
  }

  public static PlainSort of(final String field, final Order order) {
    return new PlainSort(Field.of(field), order);
  }
}
