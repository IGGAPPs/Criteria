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
  void shouldCreateFilterWithValidDate() {
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
  void shouldThrowExceptionWhenDateFormatIsInvalid() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:15-01-2024"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenDateIsNotARealDate() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:2024-13-01"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenValueIsEmpty() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:GTE:"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenOperatorIsNotAllowedForSchema() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("fecha:EQ:2024-01-15"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }

  @Test
  void shouldThrowExceptionWhenFieldNotInWhitelist() {
    assertThatThrownBy(() -> factory.make(
        Optional.of("other:GTE:2024-01-15"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    )).isInstanceOf(BadRequestException.class);
  }
}
