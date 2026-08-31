package es.iggapps.criteria.filter;

import es.iggapps.criteria.filter.parsers.ListParser;
import es.iggapps.criteria.filter.parsers.ValueParser;
import es.iggapps.criteria.plain.PlainFilter;
import es.iggapps.criteria.exception.CriteriaValidationException;
import es.iggapps.criteria.exception.CriteriaException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class Filter<E> {

  private static final String MESSAGE_FILTER_IN_NOT_BELONGS_TO_THIS_FILTER
      = "Alguno de los filtros 'In' no pertenece a este filtro provocando un estado inconsistente.";
  private static final String MESSAGE_FILTER_DUPLICATED =
      "No se puede filtrar por el campo '%s' y el operador '%s' más de una vez.";
  private static final String MESSAGE_VALUE_EMPTY =
      "El valor para el filtro '%s' con operador '%s' no puede estar vacío.";

  private final Field field;
  private final Map<Operator, E> operatorValueMap;
  private final Type type;
  private final boolean isList;

  public Filter(final Field field, final List<PlainFilter> plainFilterList) {
    this.field = field;
    this.type = plainFilterList.getFirst().getType();
    this.isList = plainFilterList.getFirst().isList();

    if (plainFilterList.stream().anyMatch(plainFilter -> !plainFilter.getField().equals(field))) {
      throw new CriteriaException(MESSAGE_FILTER_IN_NOT_BELONGS_TO_THIS_FILTER);
    }

    final ValueParser<?> customParser = plainFilterList.getFirst().getCustomParser();
    final ValueParser<E> parser = buildParser(customParser);
    final Map<Operator, E> operatorValueMap1 = new HashMap<>();

    plainFilterList.forEach(plainFilter -> {
      final Operator operator = plainFilter.getOperator();
      if (operatorValueMap1.containsKey(operator)) {
        throw new CriteriaValidationException(
            MESSAGE_FILTER_DUPLICATED.formatted(field.getField(), operator.name()));
      }
      if (plainFilter.getValue().toString().isBlank()) {
        throw new CriteriaValidationException(
            MESSAGE_VALUE_EMPTY.formatted(field.getField(), operator.name()));
      }
      operatorValueMap1.put(
          operator,
          parser.parse(plainFilter.getValue().toString(), field.getField())
      );
    });

    this.operatorValueMap = Map.copyOf(operatorValueMap1);
  }

  @SuppressWarnings("unchecked")
  private ValueParser<E> buildParser(final ValueParser<?> customParser) {
    final ValueParser<?> baseParser = customParser != null ? customParser : type.getParser();
    if (isList) {
      return (ValueParser<E>) new ListParser<>(baseParser);
    }
    return (ValueParser<E>) baseParser;
  }

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
