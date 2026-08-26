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

class ContainsAllUuidsFilterTest {

  private static final String FIELD = "uuids";
  private static final String UUID1 = "550e8400-e29b-41d4-a716-446655440000";
  private static final String UUID2 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.CONTAINS_ALL_UUIDS);
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
  void givenValidUuidList_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("uuids:CONTAINS_ALL:[" + UUID1 + "," + UUID2 + "]"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<List<UUID>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINS_ALL)).isPresent();
    assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
        .containsExactly(UUID.fromString(UUID1), UUID.fromString(UUID2));
  }

  @Test
  void givenSingleElementList_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("uuids:CONTAINS_ALL:[" + UUID1 + "]"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<UUID>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
        .containsExactly(UUID.fromString(UUID1));
  }

  @Test
  void givenNonUuidElement_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("uuids:CONTAINS_ALL:[" + UUID1 + ",not-a-uuid]"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenFormatWithoutBrackets_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("uuids:CONTAINS_ALL:" + UUID1),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenEmptyList_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("uuids:CONTAINS_ALL:[]"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("uuids:CONTAINS_ALL:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:CONTAINS_ALL:[" + UUID1 + "]"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }
}
