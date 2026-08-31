package es.iggapps.criteria.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class OperatorTest {

  @Test
  void givenCoreOperators_whenAccessed_thenAllExist() {
    assertThat(Operator.EQ).isNotNull();
    assertThat(Operator.NEQ).isNotNull();
    assertThat(Operator.GT).isNotNull();
    assertThat(Operator.GTE).isNotNull();
    assertThat(Operator.LT).isNotNull();
    assertThat(Operator.LTE).isNotNull();
    assertThat(Operator.CONTAINS).isNotNull();
    assertThat(Operator.STARTS_WITH).isNotNull();
    assertThat(Operator.ENDS_WITH).isNotNull();
    assertThat(Operator.CONTAINS_ALL).isNotNull();
    assertThat(Operator.CONTAINS_ANY).isNotNull();
  }

  @Test
  void givenCoreOperators_whenNameCalled_thenReturnCorrectName() {
    assertThat(Operator.EQ.name()).isEqualTo("EQ");
    assertThat(Operator.NEQ.name()).isEqualTo("NEQ");
    assertThat(Operator.GT.name()).isEqualTo("GT");
    assertThat(Operator.GTE.name()).isEqualTo("GTE");
    assertThat(Operator.LT.name()).isEqualTo("LT");
    assertThat(Operator.LTE.name()).isEqualTo("LTE");
    assertThat(Operator.CONTAINS.name()).isEqualTo("CONTAINS");
    assertThat(Operator.STARTS_WITH.name()).isEqualTo("STARTS_WITH");
    assertThat(Operator.ENDS_WITH.name()).isEqualTo("ENDS_WITH");
    assertThat(Operator.CONTAINS_ALL.name()).isEqualTo("CONTAINS_ALL");
    assertThat(Operator.CONTAINS_ANY.name()).isEqualTo("CONTAINS_ANY");
  }

  @Test
  void givenValidOperatorName_whenFromString_thenReturnOperator() {
    assertThat(Operator.fromString("EQ")).isEqualTo(Operator.EQ);
    assertThat(Operator.fromString("eq")).isEqualTo(Operator.EQ);
    assertThat(Operator.fromString("NEQ")).isEqualTo(Operator.NEQ);
    assertThat(Operator.fromString("GT")).isEqualTo(Operator.GT);
    assertThat(Operator.fromString("CONTAINS_ALL")).isEqualTo(Operator.CONTAINS_ALL);
    assertThat(Operator.fromString("STARTS_WITH")).isEqualTo(Operator.STARTS_WITH);
    assertThat(Operator.fromString("CONTAINS_ANY")).isEqualTo(Operator.CONTAINS_ANY);
  }

  @Test
  void givenInvalidOperatorName_whenFromString_thenThrow() {
    assertThatThrownBy(() -> Operator.fromString("INVALID"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Operador no reconocido");
  }

  @Test
  void givenNullOperatorName_whenFromString_thenThrow() {
    assertThatThrownBy(() -> Operator.fromString(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void givenCustomOperator_whenOf_thenReturnCustomOperator() {
    Operator custom = Operator.of("FZ");

    assertThat(custom.name()).isEqualTo("FZ");
    assertThat(custom).isNotEqualTo(Operator.EQ);
  }

  @Test
  void givenSameCustomOperator_whenOfTwice_thenReturnSameInstance() {
    Operator first = Operator.of("FZ");
    Operator second = Operator.of("FZ");

    assertThat(first).isSameAs(second);
  }

  @Test
  void givenCoreOperator_whenOf_thenReturnCoreOperator() {
    assertThat(Operator.of("EQ")).isEqualTo(Operator.EQ);
    assertThat(Operator.of("eq")).isEqualTo(Operator.EQ);
  }

  @Test
  void givenOperators_whenEquals_thenSameNameAreEqual() {
    Operator a = Operator.of("CUSTOM");
    Operator b = Operator.of("CUSTOM");

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void givenOperators_whenToString_thenReturnName() {
    assertThat(Operator.EQ.toString()).isEqualTo("EQ");
    assertThat(Operator.of("FZ").toString()).isEqualTo("FZ");
  }
}
