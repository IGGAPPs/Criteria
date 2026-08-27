package es.iggapps.criteria.common.domain.criteria.model.filter.parsers;

@FunctionalInterface
public interface ValueParser<E> {

  E parse(String value, String fieldName);
}
