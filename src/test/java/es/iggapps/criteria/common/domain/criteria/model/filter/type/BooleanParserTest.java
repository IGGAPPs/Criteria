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

class BooleanParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @SuppressWarnings("unchecked")
  private Boolean parse(String value) {
    return ((es.iggapps.criteria.common.domain.criteria.model.filter.parsers.ValueParser<Boolean>)
        Type.BOOLEAN.getParser()).parse(value, FIELD_NAME);
  }

  @Test
  void givenTrue_whenParseBoolean_thenReturnsTrue() {
    assertThat(parse("true")).isTrue();
  }

  @Test
  void givenFalse_whenParseBoolean_thenReturnsFalse() {
    assertThat(parse("false")).isFalse();
  }

  @Test
  void givenUpperCase_whenParseBoolean_thenReturnsValue() {
    assertThat(parse("TRUE")).isTrue();
    assertThat(parse("FALSE")).isFalse();
  }

  @Test
  void givenMixedCase_whenParseBoolean_thenReturnsValue() {
    assertThat(parse("True")).isTrue();
    assertThat(parse("False")).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "si", "1", "yes", "on", ""})
  void givenInvalidBoolean_whenParseBoolean_thenThrows(String value) {
    assertThatThrownBy(() -> parse(value))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenBooleanFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Boolean> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "true", Type.BOOLEAN))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(true);
  }
}
