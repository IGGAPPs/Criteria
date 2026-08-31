package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BigDecimalParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @SuppressWarnings("unchecked")
  private BigDecimal parse(String value) {
    return ((es.iggapps.criteria.common.domain.criteria.model.filter.parsers.ValueParser<BigDecimal>)
        Type.BIG_DECIMAL.getParser()).parse(value, FIELD_NAME);
  }

  @Test
  void givenDecimal_whenParseBigDecimal_thenReturnsBigDecimal() {
    assertThat(parse("123.456")).isEqualByComparingTo(new BigDecimal("123.456"));
  }

  @Test
  void givenNegativeDecimal_whenParseBigDecimal_thenReturnsNegative() {
    assertThat(parse("-0.001")).isEqualByComparingTo(new BigDecimal("-0.001"));
  }

  @Test
  void givenInteger_whenParseBigDecimal_thenReturnsBigDecimal() {
    assertThat(parse("42")).isEqualByComparingTo(new BigDecimal("42"));
  }

  @Test
  void givenHighPrecision_whenParseBigDecimal_thenReturnsBigDecimal() {
    assertThat(parse("0.000001")).isEqualByComparingTo(new BigDecimal("0.000001"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5.5"})
  void givenInvalidBigDecimal_whenParseBigDecimal_thenThrows(String value) {
    assertThatThrownBy(() -> parse(value))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenBigDecimalFilter_whenCreateFilter_thenFilterWorks() {
    Filter<BigDecimal> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "123.456", Type.BIG_DECIMAL))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ).get())
        .isEqualByComparingTo(new BigDecimal("123.456"));
  }
}
