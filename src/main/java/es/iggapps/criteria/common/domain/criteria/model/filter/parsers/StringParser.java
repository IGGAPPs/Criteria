package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

public class StringParser implements ValueParser<String> {

  @Override
  public String parse(final String value, final String fieldName) {
    return value;
  }
}
