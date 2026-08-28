package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import java.util.Arrays;
import java.util.List;

public final class ContainsAllStringsFilter extends Filter<List<String>> {

  public static final Schema TYPE = Schema.CONTAINS_ALL_STRINGS;

  private ContainsAllStringsFilter(final Field field, final List<PlainFilter> plainFilterList) {
    super(field, plainFilterList, TYPE);
  }

  public static Filter<List<String>> of(final Field field,
      final List<PlainFilter> plainFilterList) {
    return new ContainsAllStringsFilter(field, plainFilterList);
  }

  @Override
  protected List<String> configValueParsing(final String value) {
    return Arrays.stream(value.split(","))
        .map(String::strip)
        .toList();
  }
}
