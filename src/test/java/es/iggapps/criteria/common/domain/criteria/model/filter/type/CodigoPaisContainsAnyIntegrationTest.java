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
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodigoPaisContainsAnyIntegrationTest {

  private static final String FIELD = "paises";
  private static final Pattern CODIGO_PAIS_PATTERN = Pattern.compile("^\\d{3}$");

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

  private void validateCodigoPais(final List<String> codigos) {
    codigos.forEach(codigo -> {
      if (!CODIGO_PAIS_PATTERN.matcher(codigo).matches()) {
        throw new BadRequestException(
            "Código de país inválido: '%s'. Debe ser numérico de 3 dígitos.".formatted(codigo));
      }
    });
  }

  @Test
  void givenValid3DigitCodes_whenValidate_thenNoException() {
    Criteria criteria = factory.make(
        Optional.of("paises:containsAny:(123,456,789)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    List<String> values = filter.withOperator(Operator.CONTAINSANY).get();

    assertThat(values).containsExactly("123", "456", "789");
    validateCodigoPais(values);
  }

  @Test
  void givenCodesWithWrongLength_whenValidate_thenBadRequestException() {
    Criteria criteria = factory.make(
        Optional.of("paises:containsAny:(12,1234,123)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    List<String> values = filter.withOperator(Operator.CONTAINSANY).get();

    assertThatThrownBy(() -> validateCodigoPais(values))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("12");
  }

  @Test
  void givenCodeWithLetters_whenValidate_thenBadRequestException() {
    Criteria criteria = factory.make(
        Optional.of("paises:containsAny:(12a,456)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    List<String> values = filter.withOperator(Operator.CONTAINSANY).get();

    assertThatThrownBy(() -> validateCodigoPais(values))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("12a");
  }

  @Test
  void givenSingleValidCode_whenValidate_thenNoException() {
    Criteria criteria = factory.make(
        Optional.of("paises:containsAny:(007)"),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    Filter<List<String>> filter = criteria.getFilterOrElseThrow(FIELD);
    List<String> values = filter.withOperator(Operator.CONTAINSANY).get();

    assertThat(values).containsExactly("007");
    validateCodigoPais(values);
  }
}
