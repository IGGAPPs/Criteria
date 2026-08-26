package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.CriteriaFactory;
import es.iggapps.criteria.common.BadRequestException;
import es.iggapps.criteria.common.ExceptionMessageService;
import es.iggapps.criteria.common.domain.criteria.Criteria;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.plain.PlainSort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UUIDEqualsFilterTest {

  private static final String FIELD = "id";
  private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.UUID_EQUALS);
      }

      @Override
      protected Set<String> configSortWhiteList() {
        return Set.of();
      }

      @Override
      protected List<PlainSort> configDefaultSort() {
        return List.of();
      }
    };

    var field = CriteriaFactory.class.getDeclaredField("exceptionMessageService");
    field.setAccessible(true);
    field.set(factory, new ExceptionMessageService());
  }

  @Test
  void shouldCreateFilterWithValidUUID() {
    Criteria criteria = factory.make(
        Optional.of("id:EQ:" + VALID_UUID),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<UUID> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(UUID.fromString(VALID_UUID));
  }

  @Test
  void shouldThrowExceptionWhenValueIsNotUUID() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("id:EQ:not-a-uuid"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenValueIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("id:EQ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenOperatorIsNotAllowedForSchema() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("id:FZ:" + VALID_UUID),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFieldNotInWhitelist() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:EQ:" + VALID_UUID),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }
}
