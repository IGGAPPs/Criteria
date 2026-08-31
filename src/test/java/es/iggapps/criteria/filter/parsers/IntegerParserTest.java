package es.iggapps.criteria.filter.parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.filter.Field;
import es.iggapps.criteria.filter.Filter;
import es.iggapps.criteria.filter.Operator;
import es.iggapps.criteria.filter.Type;
import es.iggapps.criteria.plain.PlainFilter;
import es.iggapps.criteria.exception.CriteriaValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseInteger_thenReturnsInteger() {
    Filter<Integer> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "42", Type.INTEGER))
    );

    assertThat(filter.withOperator(Operator.EQ)).contains(42);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "12.5", "12L", ""})
  void givenInvalidValue_whenParseInteger_thenThrows(String value) {
    assertThatThrownBy(() -> new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, value, Type.INTEGER))
    )).isInstanceOf(CriteriaValidationException.class);
  }
}
