package es.iggapps.criteria.filter.parsers;

import java.util.Arrays;
import java.util.List;

public class ListParser<E> implements ValueParser<List<E>> {

  private final ValueParser<E> elementParser;

  public ListParser(final ValueParser<E> elementParser) {
    this.elementParser = elementParser;
  }

  @Override
  public List<E> parse(final String value, final String fieldName) {
    return Arrays.stream(value.split(","))
        .map(String::strip)
        .map(element -> elementParser.parse(element, fieldName))
        .toList();
  }
}
