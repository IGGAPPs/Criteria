package es.iggapps.criteria.sort;

import es.iggapps.criteria.filter.Field;
import es.iggapps.criteria.plain.PlainSort;
import es.iggapps.criteria.exception.CriteriaValidationException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Sorts {

  private static final String MESSAGE_SORT_DUPLICATED =
      "No se puede ordenar por el mismo campo más de una vez.";

  private final Map<Field, Sort> sorts;

  private Sorts(final List<PlainSort> plainSortList) {
    final Map<Field, Sort> sorts1 = new LinkedHashMap<>();
    plainSortList.forEach(plainSort -> {
      final Field field = plainSort.getField();
      if (sorts1.containsKey(field)) {
        throw new CriteriaValidationException(MESSAGE_SORT_DUPLICATED);
      }
      sorts1.put(field, Sort.of(field, plainSort));
    });
    this.sorts = Map.copyOf(sorts1);
  }

  public static Sorts of(final List<PlainSort> plainSortList) {
    return new Sorts(plainSortList);
  }

  public static Sorts empty() {
    return new Sorts(Collections.emptyList());
  }

  public Optional<Sort> findBy(final Field field) {
    return Optional.ofNullable(sorts.get(field));
  }
}
