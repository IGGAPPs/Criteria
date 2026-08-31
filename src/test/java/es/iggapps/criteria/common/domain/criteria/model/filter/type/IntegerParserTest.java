package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenPositiveNumber_whenParseInteger_thenReturnsInteger() {
    assertThat(Type.INTEGER.getParser().parse("42", FIELD_NAME)).isEqualTo(42);
  }

  @Test
  void givenValueOne_whenParseInteger_thenReturnsOne() {
    assertThat(Type.INTEGER.getParser().parse("1", FIELD_NAME)).isEqualTo(1);
  }

  @Test
  void givenNegativeNumber_whenParseInteger_thenReturnsNegative() {
    assertThat(Type.INTEGER.getParser().parse("-5", FIELD_NAME)).isEqualTo(-5);
  }

  @Test
  void givenZero_whenParseInteger_thenReturnsZero() {
    assertThat(Type.INTEGER.getParser().parse("0", FIELD_NAME)).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5", "12L"})
  void givenInvalidInteger_whenParseInteger_thenThrows(String value) {
    assertThatThrownBy(() -> Type.INTEGER.getParser().parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenIntegerFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Integer> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "42", Type.INTEGER))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(42);
  }

  @Test
  void givenNegativeIntegerFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Integer> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "-10", Type.INTEGER))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(-10);
  }
}
