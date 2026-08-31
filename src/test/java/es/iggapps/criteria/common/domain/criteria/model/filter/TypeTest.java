package es.iggapps.criteria.common.domain.criteria.model.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import es.iggapps.criteria.common.domain.valueobject.FechaPeninsular;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.IntegerParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.ListParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.StringParser;
import es.iggapps.criteria.common.domain.criteria.model.filter.parsers.UUIDParser;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TypeTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenStringValue_whenParseString_thenReturnsSameValue() {
    assertThat(Type.STRING.getParser().parse("hello", FIELD_NAME)).isEqualTo("hello");
  }

  @Test
  void givenNaturalNumber_whenParseInteger_thenReturnsInteger() {
    assertThat(new IntegerParser().parse("42", FIELD_NAME)).isEqualTo(42);
  }

  @Test
  void givenValueOne_whenParseInteger_thenReturnsOne() {
    assertThat(new IntegerParser().parse("1", FIELD_NAME)).isEqualTo(1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "0", "-5"})
  void givenInvalidInteger_whenParseInteger_thenThrows(String value) {
    IntegerParser parser = new IntegerParser();
    assertThatThrownBy(() -> parser.parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenValidUuid_whenParseUuid_thenReturnsUuid() {
    String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
    assertThat(new UUIDParser().parse(uuidStr, FIELD_NAME))
        .isEqualTo(UUID.fromString(uuidStr));
  }

  @Test
  void givenInvalidUuid_whenParseUuid_thenThrows() {
    UUIDParser parser = new UUIDParser();
    assertThatThrownBy(() -> parser.parse("not-a-uuid", FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenValidDate_whenParseFechaPeninsular_thenReturnsFecha() {
    assertThat(Type.FECHA_PENINSULAR.getParser().parse("2024-01-15", FIELD_NAME))
        .isEqualTo(FechaPeninsular.fromString("2024-01-15"));
  }

  @Test
  void givenInvalidDate_whenParseFechaPeninsular_thenThrows() {
    var parser = Type.FECHA_PENINSULAR.getParser();
    assertThatThrownBy(() -> parser.parse("15-01-2024", FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenStringList_whenListParser_thenReturnsList() {
    assertThat(new ListParser<>(new StringParser()).parse("java,python,javascript", FIELD_NAME))
        .containsExactly("java", "python", "javascript");
  }

  @Test
  void givenStringListWithWhitespace_whenListParser_thenTrimmed() {
    assertThat(new ListParser<>(new StringParser()).parse(" java , python ", FIELD_NAME))
        .containsExactly("java", "python");
  }

  @Test
  void givenSingleElement_whenListParser_thenReturnsSingleElementList() {
    assertThat(new ListParser<>(new StringParser()).parse("java", FIELD_NAME))
        .containsExactly("java");
  }

  @Test
  void givenIntegerList_whenListParser_thenReturnsList() {
    assertThat(new ListParser<>(new IntegerParser()).parse("1,2,3", FIELD_NAME))
        .containsExactly(1, 2, 3);
  }

  @Test
  void givenIntegerListWithNonNumeric_whenListParser_thenThrows() {
    ListParser<Integer> parser = new ListParser<>(new IntegerParser());
    assertThatThrownBy(() -> parser.parse("1,abc,3", FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenUuidList_whenListParser_thenReturnsList() {
    String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
    String uuid2 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
    assertThat(new ListParser<>(new UUIDParser()).parse(uuid1 + "," + uuid2, FIELD_NAME))
        .containsExactly(UUID.fromString(uuid1), UUID.fromString(uuid2));
  }

  @Test
  void givenUuidListWithInvalidUuid_whenListParser_thenThrows() {
    String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
    ListParser<UUID> parser = new ListParser<>(new UUIDParser());
    assertThatThrownBy(() -> parser.parse(uuid1 + ",not-a-uuid", FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
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

  @Test
  void givenNaturalNumberFilter_whenCreateFilter_thenFilterWorks() {
    Filter<Integer> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "42", Type.INTEGER))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(42);
  }

  @Test
  void givenUuidFilter_whenCreateFilter_thenFilterWorks() {
    String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
    Filter<UUID> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, uuidStr, Type.UUID))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(UUID.fromString(uuidStr));
  }
}
