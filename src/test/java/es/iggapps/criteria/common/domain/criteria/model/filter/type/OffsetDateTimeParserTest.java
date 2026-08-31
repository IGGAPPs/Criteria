package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OffsetDateTimeParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @SuppressWarnings("unchecked")
  private OffsetDateTime parse(String value) {
    return ((es.iggapps.criteria.common.domain.criteria.model.filter.parsers.ValueParser<OffsetDateTime>)
        Type.DATE_TIME.getParser()).parse(value, FIELD_NAME);
  }

  @Test
  void givenValidDateTimeWithOffset_whenParseDateTime_thenReturnsOffsetDateTime() {
    OffsetDateTime result = parse("2024-01-15T10:30:00+01:00");
    assertThat(result.getYear()).isEqualTo(2024);
    assertThat(result.getMonthValue()).isEqualTo(1);
    assertThat(result.getDayOfMonth()).isEqualTo(15);
    assertThat(result.getHour()).isEqualTo(10);
    assertThat(result.getMinute()).isEqualTo(30);
    assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(1));
  }

  @Test
  void givenValidDateTimeUTC_whenParseDateTime_thenReturnsOffsetDateTime() {
    OffsetDateTime result = parse("2024-01-15T10:30:00Z");
    assertThat(result.getOffset()).isEqualTo(ZoneOffset.UTC);
  }

  @Test
  void givenValidDateTimeNegativeOffset_whenParseDateTime_thenReturnsOffsetDateTime() {
    OffsetDateTime result = parse("2024-01-15T10:30:00-05:00");
    assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
  }

  @ParameterizedTest
  @ValueSource(strings = {"2024-01-15T10:30:00", "2024-01-15", "abc", "2024-13-01T10:30:00+01:00"})
  void givenInvalidDateTime_whenParseDateTime_thenThrows(String value) {
    assertThatThrownBy(() -> parse(value))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenDateTimeFilter_whenCreateFilter_thenFilterWorks() {
    Filter<OffsetDateTime> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "2024-01-15T10:30:00+01:00", Type.DATE_TIME))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
  }
}
