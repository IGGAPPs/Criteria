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
import es.iggapps.criteria.common.domain.valueobject.FechaPeninsular;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FechaPeninsularGteFilterTest {

  private static final String FIELD = "fecha";

  private CriteriaFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    factory = new CriteriaFactory() {
      @Override
      protected Map<String, Schema> configFilterWhiteListAndSchemas() {
        return Map.of(FIELD, Schema.FECHA_PENINSULAR_GTE);
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
  void givenValidDate_whenMake_thenFilterCreated() {
    Criteria criteria = factory.make(
        Optional.of("fecha:GTE:2024-01-15"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    assertThat(criteria.findFilterBy(FIELD)).isPresent();
    Filter<FechaPeninsular> filter = criteria.getFilterOrElseThrow(FIELD);
    assertThat(filter.withOperator(Operator.GTE)).isPresent();
    assertThat(filter.withOperator(Operator.GTE).get())
        .isEqualTo(FechaPeninsular.fromString("2024-01-15"));
  }

  @Test
  void givenInvalidDateFormat_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:15-01-2024"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El valor indicado en el filtro 'fecha' no tiene un formato de fecha correcto (yyyy-mm-dd) "
            + "o no es una fecha existente");
  }

  @Test
  void givenNonExistingDate_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:2024-13-01"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El valor indicado en el filtro 'fecha' no tiene un formato de fecha correcto (yyyy-mm-dd) "
            + "o no es una fecha existente");
  }

  @Test
  void givenEmptyValue_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El formato del parámetro 'filters' es incorrecto."
            + " Se debe indicar el valor de filtrado."
            + " Cada filtro debe seguir el formato 'campo:operador:valor'.");
  }

  @Test
  void givenOperatorNotAllowedForSchema_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:EQ:2024-01-15"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El operador no se reconoce como válido para el filtro 'fecha'. La lista de operadores válidos es ['GTE']");
  }

  @Test
  void givenFieldNotInWhitelist_whenMake_thenBadRequestException() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("other:GTE:2024-01-15"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class)
        .hasMessage("El campo 'other' no está permitido para el filtrado. La lista de campos permitidos es ['fecha'].");
  }
}
