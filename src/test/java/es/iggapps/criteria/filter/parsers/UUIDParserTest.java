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
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UUIDParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValue_whenParseUuid_thenReturnsUuid() {
    String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
    Filter<UUID> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, uuidStr, Type.UUID))
    );

    assertThat(filter.withOperator(Operator.EQ)).contains(UUID.fromString(uuidStr));
  }

  @ParameterizedTest
  @ValueSource(strings = {"not-a-uuid", "550e8400-e29b-41d4-a716", ""})
  void givenInvalidValue_whenParseUuid_thenThrows(String value) {
    assertThatThrownBy(() -> new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, value, Type.UUID))
    )).isInstanceOf(CriteriaValidationException.class);
  }
}
