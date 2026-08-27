package es.iggapps.criteria.common.domain.criteria.model.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.CriteriaFactory;
import es.iggapps.criteria.common.BadRequestException;
import es.iggapps.criteria.common.ExceptionMessageService;
import es.iggapps.criteria.common.domain.criteria.Criteria;
import es.iggapps.criteria.common.domain.criteria.plain.PlainSort;
import es.iggapps.criteria.common.domain.valueobject.FechaPeninsular;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CriteriaFilterIntegrationTest {

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, FilterConfig> configFilterWhiteList() {
        return Map.of(
            "name", new FilterConfig(Type.STRING, Set.of(Operator.EQ)),
            "description", new FilterConfig(Type.STRING, Set.of(Operator.FZ)),
            "quantity", new FilterConfig(Type.INTEGER, Set.of(Operator.EQ)),
            "id", new FilterConfig(Type.UUID, Set.of(Operator.EQ)),
            "fecha", new FilterConfig(Type.FECHA_PENINSULAR, Set.of(Operator.GTE)),
            "fechaLte", new FilterConfig(Type.FECHA_PENINSULAR, Set.of(Operator.LTE)),
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

    var field = CriteriaFactory.class.getDeclaredField("exceptionMessageService");
    field.setAccessible(true);
    field.set(factory, new ExceptionMessageService());
  }

  @Nested
  class StringEquals {

    @Test
    void givenValidString_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("name:EQ:John"), Optional.empty(), Optional.empty(), Optional.empty());

      Filter<String> filter = criteria.getFilterOrElseThrow("name");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo("John");
    }

    @Test
    void givenTooFewSegments_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("name:EQ"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenExtraSegments_whenMake_thenValueContainsColons() {
      Criteria criteria = factory.make(
          Optional.of("name:EQ:John:extra"), Optional.empty(), Optional.empty(), Optional.empty());

      Filter<String> filter = criteria.getFilterOrElseThrow("name");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo("John:extra");
    }

    @Test
    void givenEmptyField_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of(":EQ:John"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenEmptyOperator_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("name::John"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenEmptyValue_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("name:EQ:"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenFieldNotInWhitelist_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("unknown:EQ:John"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenInvalidOperator_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("name:FZ:John"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenBlankFilter_whenMake_thenNoFilter() {
      Criteria criteria = factory.make(
          Optional.of(""), Optional.empty(), Optional.empty(), Optional.empty());

      assertThat(criteria.existsFilterBy("name")).isFalse();
    }
  }

  @Nested
  class FuzzyStringEquals {

    @Test
    void givenValidFuzzyString_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("description:FZ:laptop gamer"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<String> filter = criteria.getFilterOrElseThrow("description");
      assertThat(filter.withOperator(Operator.FZ).get()).isEqualTo("laptop gamer");
    }

    @Test
    void givenInvalidOperator_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("description:EQ:laptop"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }
  }

  @Nested
  class NaturalNumberEquals {

    @Test
    void givenValidNumber_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("quantity:EQ:42"), Optional.empty(), Optional.empty(), Optional.empty());

      Filter<Integer> filter = criteria.getFilterOrElseThrow("quantity");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(42);
    }

    @Test
    void givenValueOne_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("quantity:EQ:1"), Optional.empty(), Optional.empty(), Optional.empty());

      Filter<Integer> filter = criteria.getFilterOrElseThrow("quantity");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(1);
    }

    @Test
    void givenNonNumeric_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("quantity:EQ:notanumber"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenZero_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("quantity:EQ:0"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenNegative_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("quantity:EQ:-5"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }
  }

  @Nested
  class UUIDEquals {

    @Test
    void givenValidUuid_whenMake_thenFilterCreated() {
      String uuid = "550e8400-e29b-41d4-a716-446655440000";
      Criteria criteria = factory.make(
          Optional.of("id:EQ:" + uuid), Optional.empty(), Optional.empty(), Optional.empty());

      Filter<UUID> filter = criteria.getFilterOrElseThrow("id");
      assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(UUID.fromString(uuid));
    }

    @Test
    void givenInvalidUuid_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("id:EQ:not-a-uuid"), Optional.empty(), Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }
  }

  @Nested
  class FechaPeninsularTests {

    @Test
    void givenValidDate_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("fecha:GTE:2024-01-15"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<FechaPeninsular> filter = criteria.getFilterOrElseThrow("fecha");
      assertThat(filter.withOperator(Operator.GTE).get())
          .isEqualTo(FechaPeninsular.fromString("2024-01-15"));
    }

    @Test
    void givenInvalidDateFormat_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("fecha:GTE:15-01-2024"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenNonExistingDate_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("fecha:GTE:2024-13-01"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenLte_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("fechaLte:LTE:2024-12-31"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<FechaPeninsular> filter = criteria.getFilterOrElseThrow("fechaLte");
      assertThat(filter.withOperator(Operator.LTE).get())
          .isEqualTo(FechaPeninsular.fromString("2024-12-31"));
    }
  }

  @Nested
  class ContainsAllTests {

    @Test
    void givenValidStringList_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("tags:CONTAINS_ALL:[java,python]"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<List<String>> filter = criteria.getFilterOrElseThrow("tags");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly("java", "python");
    }

    @Test
    void givenValidNumberList_whenMake_thenFilterCreated() {
      Criteria criteria = factory.make(
          Optional.of("ids:CONTAINS_ALL:[1,2,3]"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<List<Integer>> filter = criteria.getFilterOrElseThrow("ids");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly(1, 2, 3);
    }

    @Test
    void givenValidUuidList_whenMake_thenFilterCreated() {
      String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
      String uuid2 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
      Criteria criteria = factory.make(
          Optional.of("uuids:CONTAINS_ALL:[" + uuid1 + "," + uuid2 + "]"), Optional.empty(),
          Optional.empty(), Optional.empty());

      Filter<List<UUID>> filter = criteria.getFilterOrElseThrow("uuids");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly(UUID.fromString(uuid1), UUID.fromString(uuid2));
    }

    @Test
    void givenListWithoutBrackets_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("tags:CONTAINS_ALL:java,python"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenEmptyList_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("tags:CONTAINS_ALL:[]"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenNonNumericInNumberList_whenMake_thenBadRequest() {
      assertThatThrownBy(() -> factory.make(
          Optional.of("ids:CONTAINS_ALL:[1,abc,3]"), Optional.empty(), Optional.empty(),
          Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenNonUuidInUuidList_whenMake_thenBadRequest() {
      String uuid1 = "550e8400-e29b-41d4-a716-446655440000";
      assertThatThrownBy(() -> factory.make(
          Optional.of("uuids:CONTAINS_ALL:[" + uuid1 + ",not-a-uuid]"), Optional.empty(),
          Optional.empty(), Optional.empty()))
          .isInstanceOf(BadRequestException.class);
    }

    @Test
    void givenListWithWhitespace_whenMake_thenElementsTrimmed() {
      Criteria criteria = factory.make(
          Optional.of("tags:CONTAINS_ALL:[ java , python ]"), Optional.empty(), Optional.empty(),
          Optional.empty());

      Filter<List<String>> filter = criteria.getFilterOrElseThrow("tags");
      assertThat(filter.withOperator(Operator.CONTAINS_ALL).get())
          .containsExactly("java", "python");
    }
  }
}
