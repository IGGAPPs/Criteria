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

class StringEqualsFilterTest {

  private static final String FIELD = "name";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.STRING_EQUALS);
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
  void shouldCreateFilterWithValidStringValue() {
    Criteria criteria = factory.make(
        Optional.of("name:EQ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<String> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo("John");
  }

  @Test
  void shouldThrowExceptionWhenFormatHasTooFewSegments() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:EQ"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFormatHasTooManySegments() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:EQ:John:extra"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFieldIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of(":EQ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenOperatorIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name::John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenValueIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:EQ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFieldNotInWhitelist() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:EQ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenOperatorIsNotAllowedForSchema() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:FZ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldNotCreateFilterWhenFilterStringIsBlank() {
    Criteria criteria = factory.make(
        Optional.of(""),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.existsFilterBy(FIELD)).isFalse();
  }
}
