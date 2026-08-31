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

class FloatParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenDecimal_whenParseFloat_thenReturnsFloat() {
    assertThat(Type.FLOAT.getParser().parse("3.14", FIELD_NAME)).isEqualTo(3.14f);
  }

  @Test
  void givenNegativeDecimal_whenParseFloat_thenReturnsNegative() {
    assertThat(Type.FLOAT.getParser().parse("-2.5", FIELD_NAME)).isEqualTo(-2.5f);
  }

  @Test
  void givenInteger_whenParseFloat_thenReturnsFloat() {
    assertThat(Type.FLOAT.getParser().parse("42", FIELD_NAME)).isEqualTo(42.0f);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5.5"})
  void givenInvalidFloat_whenParseFloat_thenThrows(String value) {
    assertThatThrownBy(() -> Type.FLOAT.getParser().parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenFloatFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Float> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "3.14", Type.FLOAT))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(3.14f);
  }
}
