package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import es.iggapps.criteria.common.domain.exception.DomainException;
import es.iggapps.criteria.common.domain.valueobject.FechaPeninsular;
import java.util.List;

public final class FechaPeninsularGteFilter extends Filter<FechaPeninsular> {

  public static final Schema TYPE = Schema.FECHA_PENINSULAR_GTE;

  private static final String MESSAGE_VALUE_IN_NOT_A_DATE
      = "El valor indicado en el filtro '%s' no tiene un formato de fecha correcto (yyyy-mm-dd) "
      + "o no es una fecha existente";

  private FechaPeninsularGteFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<FechaPeninsular> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new FechaPeninsularGteFilter(field, plainFilterList);
  }

  @Override
  protected FechaPeninsular configValueParsing(final Object value) {
    final String strValue = (String) value;
    try {
      return FechaPeninsular.fromString(strValue);
    } catch (DomainException ex) {
      throw new CriteriaException(MESSAGE_VALUE_IN_NOT_A_DATE.formatted(field.getField()), ex);
    }
  }
}
