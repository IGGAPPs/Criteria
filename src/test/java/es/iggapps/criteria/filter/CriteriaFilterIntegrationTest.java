package es.iggapps.criteria.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.CriteriaFactory;
import es.iggapps.criteria.exception.CriteriaValidationException;
import es.iggapps.criteria.Criteria;
import es.iggapps.criteria.plain.PlainSort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CriteriaFilterIntegrationTest {

  private CriteriaFactory factory;

  private static final Optional<String> EMPTY_STR = Optional.empty();
  private static final Optional<Integer> EMPTY_INT = Optional.empty();

  @BeforeEach
  void setUp() {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, FilterConfig> configFilterWhiteList() {
        return Map.of(
            "name", new FilterConfig(Type.STRING, Set.of(Operator.EQ)),
            "description", new FilterConfig(Type.STRING, Set.of(Operator.CONTAINS)),
            "quantity", new FilterConfig(Type.INTEGER, Set.of(Operator.EQ)),
            "id", new FilterConfig(Type.UUID, Set.of(Operator.EQ)),
            "tags", FilterConfig.listOf(Type.STRING, Set.of(Operator.CONTAINS_ALL)),
            "ids", FilterConfig.listOf(Type.INTEGER, Set.of(Operator.CONTAINS_ALL)),
            "uuids", FilterConfig.listOf(Type.UUID, Set.of(Operator.CONTAINS_ALL))
        );
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
  }

  @Nested
  class StringEquals {

    @Test
    void givenValidString_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("name:EQ:John"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<String> filter = criteria.getFilterOrElseThrow("name");
      assertThat(filter.withOperator(Operator.EQ).get()).contains("John");
    }

    @ParameterizedTest
    @ValueSource(strings = {"name:EQ", ":EQ:John", "name::John", "name:EQ:"})
    void givenInvalidFormat_whenMake_thenBadRequest(String filter) {
      Optional<String> filters = Optional.of(filter);
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }

    @Test
    void givenExtraSegments_whenMake_thenValueContainsColons() {
      Criteria criteria = factory.make(
          Optional.of("name:EQ:John:extra"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<String> filter = criteria.getFilterOrElseThrow("name");
      assertThat(filter.withOperator(Operator.EQ).get()).contains("John:extra");
    }

    @Test
    void givenFieldNotInWhitelist_whenMake_thenBadRequest() {
      Optional<String> filters = Optional.of("unknown:EQ:John");
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }

    @Test
    void givenInvalidOperator_whenMake_thenBadRequest() {
      Optional<String> filters = Optional.of("name:INVALID:John");
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }

    @Test
    void givenBlankFilter_whenMake_thenNoFilter() {
      Criteria criteria = factory.make(
          Optional.of(""), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      assertThat(criteria.existsFilterBy("name")).isFalse();
    }
  }

  @Nested
  class ContainsString {

    @Test
    void givenValidContainsString_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("description:CONTAINS:laptop gamer"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<String> filter = criteria.getFilterOrElseThrow("description");
      assertThat(filter.withOperator(Operator.CONTAINS).get()).contains("laptop gamer");
    }

    @Test
    void givenInvalidOperator_whenMake_thenBadRequest() {
      Optional<String> filters = Optional.of("description:EQ:laptop");
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }
  }

  @Nested
  class NaturalNumberEquals {

    @ParameterizedTest
    @ValueSource(strings = {"quantity:EQ:42", "quantity:EQ:1", "quantity:EQ:0", "quantity:EQ:-5"})
    void givenValidNumber_whenMake_thenFilterCreated(String filter) {
      Criteria criteria = factory.make(
          Optional.of(filter), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<Integer> result = criteria.getFilterOrElseThrow("quantity");
      assertThat(result.withOperator(Operator.EQ)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {"quantity:EQ:notanumber", "quantity:EQ:12.5"})
    void givenInvalidNumber_whenMake_thenBadRequest(String filter) {
      Optional<String> filters = Optional.of(filter);
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }
  }

  @Nested
  class UUIDEquals {

    @Test
    void givenValidUuid_whenMake_thenFilterCreated() {
      String uuid = "550e8400-e29b-41d4-a716-446655440000";
      Criteria criteria = factory.make(
          Optional.of("id:EQ:" + uuid), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<UUID> filter = criteria.getFilterOrElseThrow("id");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(UUID.fromString(uuid));
    }

    @Test
    void givenInvalidUuid_whenMake_thenBadRequest() {
      Optional<String> filters = Optional.of("id:EQ:not-a-uuid");
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }
  }

  @Nested
  class ContainsAllTests {

    @Test
    void givenValidStringList_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("tags:CONTAINS_ALL:[java,python]"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<List<String>> filter = criteria.getFilterOrElseThrow("tags");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly("java", "python");
    }

    @Test
    void givenValidNumberList_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("ids:CONTAINS_ALL:[1,2,3]"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<List<Integer>> filter = criteria.getFilterOrElseThrow("ids");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly(1, 2, 3);
    }

    @Test
    void givenValidUuidList_whenMake_thenFilterCreated() {
      String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
      String uuid2 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
      Criteria criteria = factory.make(
          Optional.of("uuids:CONTAINS_ALL:[" + uuid1 + "," + uuid2 + "]"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<List<UUID>> filter = criteria.getFilterOrElseThrow("uuids");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly(UUID.fromString(uuid1), UUID.fromString(uuid2));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "tags:CONTAINS_ALL:java,python",
        "tags:CONTAINS_ALL:[]",
        "ids:CONTAINS_ALL:[1,abc,3]"
    })
    void givenInvalidList_whenMake_thenBadRequest(String filter) {
      Optional<String> filters = Optional.of(filter);
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }

    @Test
    void givenNonUuidInUuidList_whenMake_thenBadRequest() {
      String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
      Optional<String> filters = Optional.of("uuids:CONTAINS_ALL:[" + uuid1 + ",not-a-uuid]");
      assertThatThrownBy(() -> factory.make(filters, EMPTY_STR, EMPTY_INT, EMPTY_INT))
          .isInstanceOf(CriteriaValidationException.class);
    }

    @Test
    void givenListWithWhitespace_whenMake_thenElementsTrimmed() {
      Criteria criteria = factory.make(
          Optional.of("tags:CONTAINS_ALL:[ java , python ]"), EMPTY_STR, EMPTY_INT, EMPTY_INT);

      Filter<List<String>> filter = criteria.getFilterOrElseThrow("tags");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly("java", "python");
    }
  }
}
