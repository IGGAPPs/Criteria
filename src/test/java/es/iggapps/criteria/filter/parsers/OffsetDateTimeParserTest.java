package es.iggapps.criteria.filter.parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.filter.Field;
import es.iggapps.criteria.filter.Filter;
import es.iggapps.criteria.filter.Operator;
import es.iggapps.criteria.filter.Type;
import es.iggapps.criteria.plain.PlainFilter;
import es.iggapps.criteria.exception.CriteriaValidationException;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OffsetDateTimeParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseDateTime_thenReturnsOffsetDateTime() {
    Filter<OffsetDateTime> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "2024-01-15T10:30:00+01:00", Type.DATE_TIME))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
  }

  @ParameterizedTest
  @ValueSource(strings = {"2024-01-15T10:30:00", "2024-01-15", "abc"})
  void givenInvalidValue_whenParseDateTime_thenThrows(String value) {
    assertThatThrownBy(() -> new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, value, Type.DATE_TIME))
    )).isInstanceOf(CriteriaValidationException.class);
  }
}
