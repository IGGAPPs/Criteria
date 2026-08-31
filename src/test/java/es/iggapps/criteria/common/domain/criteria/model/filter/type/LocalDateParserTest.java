package es.iggapps.criteria.common.domain.criteria.model.filter.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filter;
import es.iggapps.criteria.common.domain.criteria.model.filter.Operator;
import es.iggapps.criteria.common.domain.criteria.model.filter.Type;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LocalDateParserTest {

  private static final String FIELD_NAME = "testField";
  private static final Field FIELD = Field.of(FIELD_NAME);

  @Test
  void givenValidDate_whenParseDate_thenReturnsLocalDate() {
    assertThat(Type.DATE.getParser().parse("2024-01-15", FIELD_NAME))
        .isEqualTo(LocalDate.of(2024, 1, 15));
  }

  @Test
  void givenLeapYearDate_whenParseDate_thenReturnsLocalDate() {
    assertThat(Type.DATE.getParser().parse("2024-02-29", FIELD_NAME))
        .isEqualTo(LocalDate.of(2024, 2, 29));
  }

  @ParameterizedTest
  @ValueSource(strings = {"15-01-2024", "2024/01/15", "abc", "2024-13-01", "2024-01-32"})
  void givenInvalidDate_whenParseDate_thenThrows(String value) {
    assertThatThrownBy(() -> Type.DATE.getParser().parse(value, FIELD_NAME))
        .isInstanceOf(CriteriaException.class);
  }

  @Test
  void givenDateFilter_whenCreateFilter_thenFilterWorks() {
    Filter<LocalDate> filter = new Filter<>(
        FIELD,
        List.of(PlainFilter.of(FIELD_NAME, Operator.EQ, "2024-01-15", Type.DATE))
    );

    assertThat(filter.withOperator(Operator.EQ)).isPresent();
    assertThat(filter.withOperator(Operator.EQ)).contains(LocalDate.of(2024, 1, 15));
  }
}
