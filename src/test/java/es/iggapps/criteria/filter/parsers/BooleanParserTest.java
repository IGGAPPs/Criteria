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

class BooleanParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseBoolean_thenReturnsBoolean() {
    Filter<Boolean> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "true", Type.BOOLEAN))
    );

    assertThat(filter.withOperator(Operator.EQ)).contains(true);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "si", "1", "yes", "on", ""})
  void givenInvalidValue_whenParseBoolean_thenThrows(String value) {
    assertThatThrownBy(() -> new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, value, Type.BOOLEAN))
    )).isInstanceOf(CriteriaValidationException.class);
  }
}
