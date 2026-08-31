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

class LongParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenPositiveNumber_whenParseLong_thenReturnsLong() {
    assertThat(Type.LONG.getParser().parse("123", FIELD_NAME)).isEqualTo(123L);
  }

  @Test
  void givenNegativeNumber_whenParseLong_thenReturnsNegative() {
    assertThat(Type.LONG.getParser().parse("-5", FIELD_NAME)).isEqualTo(-5L);
  }

  @Test
  void givenZero_whenParseLong_thenReturnsZero() {
    assertThat(Type.LONG.getParser().parse("0", FIELD_NAME)).isEqualTo(0L);
  }

  @Test
  void givenLargeNumber_whenParseLong_thenReturnsLong() {
    assertThat(Type.LONG.getParser().parse("9999999999", FIELD_NAME)).isEqualTo(9999999999L);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5", "99999999999999999999"})
  void givenInvalidLong_whenParseLong_thenThrows(String value) {
    assertThatThrownBy(() -> Type.LONG.getParser().parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenLongFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Long> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "123", Type.LONG))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(123L);
  }
}
