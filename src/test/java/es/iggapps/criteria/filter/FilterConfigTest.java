package es.iggapps.criteria.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.filter.parsers.ValueParser;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FilterConfigTest {

  private static final String DATE_PATTERN = "\\d{2}-\\d{2}-\\d{4}";

  @Test
  void givenPattern_whenCreated_thenPatternIsStored() {
    FilterConfig config = FilterConfig.withPattern(
        Type.STRING, Set.of(Operator.EQ), DATE_PATTERN);

    assertThat(config.pattern()).isEqualTo(DATE_PATTERN);
    assertThat(config.customParser()).isNull();
  }

  @Test
  void givenCustomParser_whenCreated_thenParserIsStored() {
    FilterConfig config = new FilterConfig(
        Type.STRING, Set.of(Operator.EQ), ValueParser.of(v -> v.toUpperCase()));

    assertThat(config.customParser()).isNotNull();
    assertThat(config.pattern()).isNull();
  }

  @Test
  void givenPatternOnNonStringType_whenCreated_thenThrows() {
    assertThatThrownBy(() -> FilterConfig.withPattern(
        Type.INTEGER, Set.of(Operator.EQ), DATE_PATTERN))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("pattern solo está permitido para Type.STRING");
  }

  @Test
  void givenCustomParserOnNonStringType_whenCreated_thenThrows() {
    assertThatThrownBy(() -> new FilterConfig(
        Type.INTEGER, Set.of(Operator.EQ), ValueParser.of(v -> 123)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("customParser solo está permitido para Type.STRING");
  }

  @Test
  void givenPatternAndCustomParserTogether_whenCreated_thenThrows() {
    assertThatThrownBy(() -> new FilterConfig(
        Type.STRING, Set.of(Operator.EQ), false, DATE_PATTERN, ValueParser.of(v -> v)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se pueden usar pattern y customParser juntos");
  }

  @Test
  void givenListWithPattern_whenCreated_thenIsListWithPattern() {
    FilterConfig config = FilterConfig.listOfPattern(
        Type.STRING, Set.of(Operator.CONTAINS_ALL), DATE_PATTERN);

    assertThat(config.isList()).isTrue();
    assertThat(config.pattern()).isEqualTo(DATE_PATTERN);
  }

  @Test
  void givenListWithCustomParser_whenCreated_thenIsListWithParser() {
    FilterConfig config = FilterConfig.listOf(
        Type.STRING, Set.of(Operator.CONTAINS_ALL), ValueParser.of(v -> v.toUpperCase()));

    assertThat(config.isList()).isTrue();
    assertThat(config.customParser()).isNotNull();
  }
}
