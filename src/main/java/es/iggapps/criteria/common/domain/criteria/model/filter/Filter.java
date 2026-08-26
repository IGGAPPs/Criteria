package es.iggapps.criteria.common.domain.criteria.model.filter;

import es.iggapps.criteria.common.domain.criteria.model.Field;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import es.iggapps.criteria.common.domain.exception.DomainException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public abstract class Filter<E> {

  private static final String MESSAGE_FILTER_IN_NOT_BELONGS_TO_THIS_FILTER
      = "Alguno de los filtros 'In' no pertenece a este filtro provocando un estado inconsistente.";
  private static final String MESSAGE_FILTER_DUPLICATED =
      "No se puede filtrar por el campo '%s' y el operador '%s' más de una vez.";
  private static final String MESSAGE_VALUE_EMPTY =
      "El valor para el filtro '%s' con operador '%s' no puede estar vacío.";

  protected final Field field;
  protected final Map<Operator, E> operatorValueMap;
  protected final Schema type;

  protected Filter(final Field field, final List<PlainFilter> plainFilterList, final Schema type) {
    this.field = field;
    this.type = type;

    if (plainFilterList.stream().anyMatch(plainFilter -> !plainFilter.getField().equals(field))) {
      throw new DomainException(MESSAGE_FILTER_IN_NOT_BELONGS_TO_THIS_FILTER);
    }

    final Map<Operator, E> operatorValueMap1 = new HashMap<>();

    plainFilterList.forEach(plainFilter -> {
      final Operator operator = plainFilter.getOperator();
      if (operatorValueMap1.containsKey(operator)) {
        throw new CriteriaException(
            MESSAGE_FILTER_DUPLICATED.formatted(field.getField(), operator.name()));
      }
      if (plainFilter.getValue().toString().isBlank()) {
        throw new CriteriaException(
            MESSAGE_VALUE_EMPTY.formatted(field.getField(), operator.name()));
      }
      operatorValueMap1.put(
          operator,
          configValueParsing(plainFilter.getValue().toString())
      );
    });

    this.operatorValueMap = Map.copyOf(operatorValueMap1);
  }

  protected abstract E configValueParsing(String value);

  public Optional<E> withOperator(final Operator operator) {
    return Optional.ofNullable(operatorValueMap.get(operator));
  }

  public boolean hasOperator(final Operator operator) {
    return operatorValueMap.containsKey(operator);
  }

  public E withOperatorOrElseThrow(final Operator operator) {
    return withOperator(operator).orElseThrow();
  }
}
