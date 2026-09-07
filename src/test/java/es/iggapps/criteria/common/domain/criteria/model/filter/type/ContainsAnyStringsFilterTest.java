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

class ContainsAnyStringsFilterTest {

  private static final String FIELD = "tags";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.CONTAINS_ANY_STRINGS);
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
  void givenValidStringList_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("tags:containsAny:(java,python,javascript)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINSANY)).isPresent();
    assertThat(filter.withOperator(Operator.CONTAINSANY).get())
        .containsExactly("java", "python", "javascript");
  }

  @Test
  void givenSingleElementList_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("tags:containsAny:(java)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINSANY).get())
        .containsExactly("java");
  }

  @Test
  void givenListWithWhitespace_whenMake_thenElementsAreTrimmed() {
    Criteria criteria = factory.make(
        Optional.of("tags:containsAny:( java , python )"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINSANY).get())
        .containsExactly("java", "python");
  }

  @Test
  void givenFormatWithoutParentheses_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:java,python"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenEmptyList_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:()"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:containsAny:(java)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void givenOperatorNotAllowedForSchema_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:(java)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }
}
