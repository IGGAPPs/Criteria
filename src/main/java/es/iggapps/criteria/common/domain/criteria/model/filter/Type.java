package es.iggapps.criteria.common.domain.criteria.model.filter;

import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.IntegerParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.StringParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.UUIDParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.ValueParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {
  STRING(new StringParser()),
  INTEGER(new IntegerParser()),
  UUID(new UUIDParser());

  private final ValueParser<?> parser;
}
