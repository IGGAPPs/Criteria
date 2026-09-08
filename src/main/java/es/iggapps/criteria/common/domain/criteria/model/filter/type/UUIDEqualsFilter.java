package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.List;
import java.util.UUID;

public final class UUIDEqualsFilter extends Filter<UUID> {

  public static final Schema TYPE = Schema.STRING_EQUALS;
  private static final String MESSAGGE_VALUE_IN_NOT_AN_UUID = "El filtro '%s' contiene un valor que no es de tipo UUID";

  private UUIDEqualsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<UUID> of(final Field field, final List<PlainFilter> plainFilterList) {
    return new UUIDEqualsFilter(field, plainFilterList);
  }

  @Override
  protected UUID configValueParsing(final Object value) {
    final String strValue = (String) value;
    UUID uuid;
    try {
      uuid = UUID.fromString(strValue);
    } catch (IllegalArgumentException ex) {
      throw new CriteriaException(MESSAGGE_VALUE_IN_NOT_AN_UUID.formatted(field.getField()), ex);
    }

    return uuid;
  }
}
