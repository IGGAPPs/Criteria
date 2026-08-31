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

class DoubleParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenDecimal_whenParseDouble_thenReturnsDouble() {
    assertThat(Type.DOUBLE.getParser().parse("3.14", FIELD_NAME)).isEqualTo(3.14);
  }

  @Test
  void givenNegativeDecimal_whenParseDouble_thenReturnsNegative() {
    assertThat(Type.DOUBLE.getParser().parse("-2.5", FIELD_NAME)).isEqualTo(-2.5);
  }

  @Test
  void givenInteger_whenParseDouble_thenReturnsDouble() {
    assertThat(Type.DOUBLE.getParser().parse("42", FIELD_NAME)).isEqualTo(42.0);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5.5"})
  void givenInvalidDouble_whenParseDouble_thenThrows(String value) {
    assertThatThrownBy(() -> Type.DOUBLE.getParser().parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenDoubleFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Double> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "3.14", Type.DOUBLE))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(3.14);
  }
}
