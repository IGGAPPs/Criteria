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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FuzzyStringEqualsFilterTest {

  private static final String FIELD = "description";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.FUZZY_STRING_EQUALS);
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
  void shouldCreateFilterWithValidFuzzyStringValue() {
    Criteria criteria = factory.make(
        Optional.of("description:FZ:laptop gamer"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<String> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.FZ)).isPresent();
    assertThat(filter.withOperator(Operator.FZ).get()).isEqualTo("laptop gamer");
  }

  @Test
  void shouldThrowExceptionWhenFormatHasTooFewSegments() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("description:FZ"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenValueIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("description:FZ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFieldNotInWhitelist() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:FZ:laptop"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenOperatorIsNotAllowedForSchema() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("description:EQ:laptop"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldNotCreateFilterWhenFilterStringIsBlank() {
    Criteria criteria = factory.make(
        Optional.of("  "),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.existsFilterBy(FIELD)).isFalse();
  }
}
