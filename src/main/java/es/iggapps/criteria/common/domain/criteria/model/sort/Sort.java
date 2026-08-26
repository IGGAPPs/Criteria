package es.iggapps.criteria.common.domain.criteria.model.sort;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.plain.PlainSort;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class Sort {

  private final Field field;
  private final Order order;

  public static Sort of(final Field field, final PlainSort plainSort) {
    return new Sort(field, plainSort.getOrder());
  }

  public boolean isAscending() {
    return order == Order.ASC;
  }

  public boolean isDescending() {
    return order == Order.DESC;
  }
}