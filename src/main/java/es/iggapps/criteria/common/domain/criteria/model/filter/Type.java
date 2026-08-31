package es.iggapps.criteria.common.domain.criteria.model.filter;

import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.BigDecimalParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.BooleanParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.DoubleParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.FloatParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.IntegerParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.LocalDateParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.LongParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.OffsetDateTimeParser;
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
  UUID(new UUIDParser()),
  BOOLEAN(new BooleanParser()),
  DATE(new LocalDateParser()),
  DATE_TIME(new OffsetDateTimeParser()),
  LONG(new LongParser()),
  FLOAT(new FloatParser()),
  DOUBLE(new DoubleParser()),
  BIG_DECIMAL(new BigDecimalParser());

  private final ValueParser<?> parser;
}
