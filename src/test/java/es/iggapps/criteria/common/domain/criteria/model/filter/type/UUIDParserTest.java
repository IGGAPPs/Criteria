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
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UUIDParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);
  private static final String UUID_STR = "550e8400-e29b-41d4-a716-446655440000";

  @Test
  void givenValidUuid_whenParseUuid_thenReturnsUuid() {
    assertThat(Type.UUID.getParser().parse(UUID_STR, FIELD_NAME))
        .isEqualTo(UUID.fromString(UUID_STR));
  }

  @Test
  void givenInvalidUuid_whenParseUuid_thenThrows() {
    assertThatThrownBy(() -> Type.UUID.getParser().parse("not-a-uuid", FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenUuidFilter_whenCreateFilter_thenFilterWorks() {
    Filter<UUID> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, UUID_STR, Type.UUID))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(UUID.fromString(UUID_STR));
  }
}
