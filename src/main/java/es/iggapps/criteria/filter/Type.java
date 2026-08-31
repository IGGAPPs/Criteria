package es.iggapps.criteria.filter;

import es.iggapps.criteria.filter.parsers.BigDecimalParser;
import es.iggapps.criteria.filter.parsers.BooleanParser;
import es.iggapps.criteria.filter.parsers.DoubleParser;
import es.iggapps.criteria.filter.parsers.FloatParser;
import es.iggapps.criteria.filter.parsers.IntegerParser;
import es.iggapps.criteria.filter.parsers.LocalDateParser;
import es.iggapps.criteria.filter.parsers.LongParser;
import es.iggapps.criteria.filter.parsers.OffsetDateTimeParser;
import es.iggapps.criteria.filter.parsers.StringParser;
import es.iggapps.criteria.filter.parsers.UUIDParser;
import es.iggapps.criteria.filter.parsers.ValueParser;
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
