package es.iggapps.criteria.common.domain.criteria.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Schema {
  STRING_EQUALS(List.of(Operator.EQ)),
  NATURAL_NUMBER_EQUALS(List.of(Operator.EQ)),
  UUID_EQUALS(List.of(Operator.EQ)),
  FUZZY_STRING_EQUALS(List.of(Operator.FZ)),
  FECHA_PENINSULAR_GTE(List.of(Operator.GTE)),
  FECHA_PENINSULAR_LTE(List.of(Operator.LTE)),
  CONTAINS_ALL_STRINGS(List.of(Operator.CONTAINSALL)),
  CONTAINS_ALL_NUMBERS(List.of(Operator.CONTAINSALL)),
  CONTAINS_ALL_UUIDS(List.of(Operator.CONTAINSALL));

  private final List<Operator> operatorWhiteList;
}