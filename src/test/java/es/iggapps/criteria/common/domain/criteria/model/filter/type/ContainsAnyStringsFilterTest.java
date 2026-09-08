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
  void givenListWithSpaces_whenMake_thenSpacesAreTrimmed() {
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
  void givenDuplicateValues_whenMake_thenAllKept() {
    Criteria criteria = factory.make(
        Optional.of("tags:containsAny:(123,123)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.CONTAINSANY).get())
        .containsExactly("123", "123");
  }

  @Test
  void givenEmptyList_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:()"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("La lista de valores del filtro 'tags' no puede estar vacía.");
  }

  @Test
  void givenValueWithoutParentheses_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:java"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El valor del filtro 'tags' debe tener formato '(valor1,valor2,...)'.");
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El valor del filtro 'tags' debe tener formato '(valor1,valor2,...)'.");
  }

  @Test
  void givenTrailingComma_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:(123,)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("La lista de valores del filtro 'tags' contiene algún valor vacío.");
  }

  @Test
  void givenDoubleComma_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:(java,,python)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("La lista de valores del filtro 'tags' contiene algún valor vacío.");
  }

  @Test
  void givenOnlyComma_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:(,)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("La lista de valores del filtro 'tags' contiene algún valor vacío.");
  }

  @Test
  void givenOnlySpacesAndCommas_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:containsAny:( , )"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("La lista de valores del filtro 'tags' contiene algún valor vacío.");
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:containsAny:(java)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El campo 'unknown' no está permitido para el filtrado. La lista de campos permitidos es ['tags'].");
  }

  @Test
  void givenOperatorNotAllowedForSchema_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:(java)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'tags'. La lista de operadores válidos es ['CONTAINSANY']");
  }

  @Test
  void givenEqOperatorWithEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'tags'. La lista de operadores válidos es ['CONTAINSANY']");
  }

  @Test
  void givenEqOperatorWithEmptyParentheses_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:()"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'tags'. La lista de operadores válidos es ['CONTAINSANY']");
  }

  @Test
  void givenEqOperatorWithOnlyComma_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:(,)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'tags'. La lista de operadores válidos es ['CONTAINSANY']");
  }

  @Test
  void givenEqOperatorWithSimpleValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("tags:EQ:276"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'tags'. La lista de operadores válidos es ['CONTAINSANY']");
  }
}
