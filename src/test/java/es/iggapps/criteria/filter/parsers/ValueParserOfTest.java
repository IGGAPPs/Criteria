package es.iggapps.criteria.filter.parsers;

import static org.assertj.core.api.Assertions.assertThat;

import es.iggapps.criteria.filter.Field;
import es.iggapps.criteria.filter.Filter;
import es.iggapps.criteria.filter.FilterConfig;
import es.iggapps.criteria.filter.Operator;
import es.iggapps.criteria.filter.Type;
import es.iggapps.criteria.plain.PlainFilter;
import es.iggapps.criteria.exception.CriteriaValidationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValueParserOfTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  public record Email(String value) {
    public Email {
      if (value == null || !value.contains("@")) {
        throw new IllegalArgumentException("Debe contener un '@'");
      }
      if (!value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
        throw new IllegalArgumentException("Formato de email inválido");
      }
    }
  }

  private static final ValueParser<Email> EMAIL_PARSER =
      ValueParser.of(value -> new Email(value));

  @Test
  void givenValidEmail_whenParseWithVO_thenReturnsEmail() {
    Filter<Email> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "user@example.com", Type.STRING,
            EMAIL_PARSER))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ).get()).isEqualTo(new Email("user@example.com"));
  }

  @Test
  void givenEmailWithoutAt_whenParseWithVO_thenThrowsWithVOMessage() {
    CriteriaValidationException ex = org.assertj.core.api.Assertions.catchThrowableOfType(
        () -> new Filter<>(
            FIELD,
            List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "userexample.com", Type.STRING,
                EMAIL_PARSER))),
        CriteriaValidationException.class);

    assertThat(ex.getMessage()).contains("Debe contener un '@'");
  }

  @Test
  void givenInvalidEmail_whenParseWithVO_thenThrowsWithVOMessage() {
    CriteriaValidationException ex = org.assertj.core.api.Assertions.catchThrowableOfType(
        () -> new Filter<>(
            FIELD,
            List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "user@", Type.STRING,
                EMAIL_PARSER))),
        CriteriaValidationException.class);

    assertThat(ex.getMessage()).contains("Formato de email inválido");
  }

  @Test
  void givenFilterConfigWithCustomParser_whenCreated_thenHasCustomParser() {
    FilterConfig config = new FilterConfig(
        Type.STRING, Set.of(Operator.EQ), EMAIL_PARSER);

    assertThat(config.customParser()).isNotNull();
    assertThat(config.pattern()).isNull();
  }
}
