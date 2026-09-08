package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import java.util.List;

public final class FuzzyStringEqualsFilter extends Filter<String> {

  public static final Schema TYPE = Schema.FUZZY_STRING_EQUALS;

  private FuzzyStringEqualsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<String> of(final Field field, final List<PlainFilter> plainFilterList) {
    return new FuzzyStringEqualsFilter(field, plainFilterList);
  }

  @Override
  protected String configValueParsing(final Object value) {
    return (String) value;
  }
}
