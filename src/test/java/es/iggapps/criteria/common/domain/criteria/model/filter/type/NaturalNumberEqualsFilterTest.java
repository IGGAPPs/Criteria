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

class NaturalNumberEqualsFilterTest {

  private static final String FIELD = "quantity";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.NATURAL_NUMBER_EQUALS);
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
  void givenValidNaturalNumber_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("quantity:EQ:42"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<Integer> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(42);
  }

  @Test
  void givenValueOne_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("quantity:EQ:1"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<Integer> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(1);
  }

  @Test
  void givenNonNumericValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("quantity:EQ:notanumber"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenZeroValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("quantity:EQ:0"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenNegativeValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("quantity:EQ:-5"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("quantity:EQ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenOperatorNotAllowedForSchema_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("quantity:FZ:42"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:EQ:42"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }
}
