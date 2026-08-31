package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseString_thenReturnsSameValue() {
    assertThat(Type.STRING.getParser().parse("hello", FIELD_NAME)).isEqualTo("hello");
  }

  @Test
  void givenEmptyString_whenParseString_thenReturnsEmptyString() {
    assertThat(Type.STRING.getParser().parse("", FIELD_NAME)).isEqualTo("");
  }

  @Test
  void givenStringFilter_whenCreateFilter_thenFilterWorks() {
    Filter<String> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "John", Type.STRING))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains("John");
  }
}
