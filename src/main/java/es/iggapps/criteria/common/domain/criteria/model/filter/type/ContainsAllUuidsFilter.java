package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class ContainsAllUuidsFilter extends Filter<List<UUID>> {

  public static final Schema TYPE = Schema.CONTAINS_ALL_UUIDS;

  private static final String MESSAGE_VALUE_NOT_UUID =
      "El filtro '%s' contiene un valor que no es de tipo UUID.";

  private ContainsAllUuidsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<List<UUID>> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new ContainsAllUuidsFilter(field, plainFilterList);
  }

  @Override
  protected List<UUID> configValueParsing(final String value) {
    return Arrays.stream(value.split(","))
        .map(String::strip)
        .map(s -> {
          try {
            return UUID.fromString(s);
          } catch (IllegalArgumentException ex) {
            throw new CriteriaException(
                MESSAGE_VALUE_NOT_UUID.formatted(field.getField()), ex);
          }
        })
        .toList();
  }
}
