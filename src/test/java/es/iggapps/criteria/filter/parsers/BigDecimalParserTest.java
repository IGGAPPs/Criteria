package es.iggapps.criteria.filter.parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.filter.Field;
import es.iggapps.criteria.filter.Filter;
import es.iggapps.criteria.filter.Operator;
import es.iggapps.criteria.filter.Type;
import es.iggapps.criteria.plain.PlainFilter;
import es.iggapps.criteria.exception.CriteriaValidationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BigDecimalParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseBigDecimal_thenReturnsBigDecimal() {
    Filter<BigDecimal> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "123.456", Type.BIG_DECIMAL))
    );

    assertThat(filter.withOperator(Operator.EQ).get())
        .isEqualByComparingTo(new BigDecimal("123.456"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5.5"})
  void givenInvalidValue_whenParseBigDecimal_thenThrows(String value) {
    assertThatThrownBy(() -> new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, value, Type.BIG_DECIMAL))
    )).isInstanceOf(CriteriaValidationException.class);
  }
}
