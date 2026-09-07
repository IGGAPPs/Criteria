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
  void givenValidStringValue_whenMake_thenFilterCreated() {
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
  void givenValueWithSpaces_whenMake_thenSpacesArePreserved() {
    Criteria criteria = factory.make(
        Optional.of("name:EQ:John Doe"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<String> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo("John Doe");
  }

  @Test
  void givenExtraSegmentsInValue_whenMake_thenValueContainsColons() {
    Criteria criteria = factory.make(
        Optional.of("name:EQ:John:extra"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<String> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo("John:extra");
  }

  @Test
  void givenFormatWithTooFewSegments_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:EQ"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El formato del parámetro 'filters' es incorrecto. Cada filtro debe seguir el formato 'campo:operador:valor'.");
  }

  @Test
  void givenEmptyField_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of(":EQ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El formato del parámetro 'filters' es incorrecto."
            + " Se debe indicar el nombre del campo de filtrado."
            + " Cada filtro debe seguir el formato 'campo:operador:valor.'.");
  }

  @Test
  void givenEmptyOperator_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name::John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El formato del parámetro 'filters' es incorrecto."
            + " Se debe indicar el operador de filtrado."
            + " Cada filtro debe seguir el formato 'campo:operador:valor'.");
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:EQ:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El formato del parámetro 'filters' es incorrecto."
            + " Se debe indicar el valor de filtrado."
            + " Cada filtro debe seguir el formato 'campo:operador:valor'.");
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("unknown:EQ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El campo 'unknown' no está permitido para el filtrado. La lista de campos permitidos es ['name'].");
  }

  @Test
  void givenOperatorNotAllowedForSchema_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("name:FZ:John"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'name'. La lista de operadores válidos es ['EQ']");
  }

  @Test
  void givenBlankFilterString_whenMake_thenNoFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of(""),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.existsFilterBy(FIELD)).isFalse();
  }
}
