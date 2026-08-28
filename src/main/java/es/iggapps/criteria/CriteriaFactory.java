package es.iggapps.criteria;

import es.iggapps.criteria.common.BadRequestException;
import es.iggapps.criteria.common.ExceptionMessageService;
import es.iggapps.criteria.common.domain.criteria.Criteria;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filters;
import es.iggapps.criteria.common.domain.criteria.model.filter.Schema;
import es.iggapps.criteria.common.domain.criteria.model.page.PageNumber;
import es.iggapps.criteria.common.domain.criteria.model.page.PageSize;
import es.iggapps.criteria.common.domain.criteria.model.sort.Sorts;
import es.iggapps.criteria.common.domain.criteria.plain.PlainFilter;
import es.iggapps.criteria.common.domain.criteria.plain.PlainSort;
import es.iggapps.criteria.common.domain.exception.CriteriaException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class CriteriaFactory {

  @Autowired
  private ExceptionMessageService exceptionMessageService;

  private static final String EXTERNAL_FILTER_AND_SORT_SEPARATOR = ",";
  private static final String INTERNAL_FILTER_AND_SORT_SEPARATOR = ":";
  private static final Integer MAX_NUMBER_OF_FILTER_SEGMENTS = 3;
  private static final Integer NUMBER_OF_SORT_SEGMENTS = 2;
  private static final Integer DEFAULT_PAGE_SIZE = 25;

  private static final String MESSAGE_FILTERS_FORMAT_INCORRECT
      = "El formato del parámetro 'filters' es incorrecto. Cada filtro debe seguir el formato 'campo:operador:valor'.";
  private static final String MESSAGE_FILTER_FIELD_CANNOT_BE_EMPTY =
      "El formato del parámetro 'filters' es incorrecto."
          + " Se debe indicar el nombre del campo de filtrado."
          + " Cada filtro debe seguir el formato 'campo:operador:valor.'.";
  private static final String MESSAGE_FILTER_OPERATOR_CANNOT_BE_EMPTY =
      "El formato del parámetro 'filters' es incorrecto."
          + " Se debe indicar el operador de filtrado. Cada filtro debe seguir el formato 'campo:operador:valor'.";
  private static final String MESSAGE_FILTER_VALUE_CANNOT_BE_EMPTY =
      "El formato del parámetro 'filters' es incorrecto."
          + " Se debe indicar el valor de filtrado. Cada filtro debe seguir el formato 'campo:operador:valor'.";
  private static final String MESSAGE_FILTER_FIELD_NOT_ALLOWED
      = "El campo '%s' no está permitido para el filtrado. La lista de campos permitidos es [%s].";
  private static final String MESSAGE_FILTER_VALUE_INVALID_LIST_FORMAT =
      "El valor del filtro '%s' debe tener formato '(valor1,valor2,...)'.";
  private static final String MESSAGE_FILTER_VALUE_EMPTY_LIST =
      "La lista de valores del filtro '%s' no puede estar vacía.";
  private static final String MESSAGE_SORTS_FORMAT_INCORRECT
      = "El formato del parámetro 'sorts' es incorrecto. Cada ordenación debe seguir el formato 'campo:orden'.";
  private static final String MESSAGE_SORT_FIELD_CANNOT_BE_EMPTY =
      "El formato del parámetro 'sorts' es incorrecto."
          + " Se debe indicar el campo de ordenación. Cada ordenación debe seguir el formato 'campo:orden'.";
  private static final String MESSAGE_SORT_ORDER_CANNOT_BE_EMPTY =
      "El formato del parámetro 'sorts' es incorrecto."
          + " Se debe indicar el sentido de la ordenación. Cada ordenación debe seguir el formato 'campo:orden'.";
  private static final String MESSAGE_SORT_FIELD_NOT_ALLOWED
      = "El campo '%s' no está permitido para la ordenación. La lista de campos permitidos es [%s].";

  // ──────────────────────────────────────────────
  // 1. Public API
  // ──────────────────────────────────────────────

  public final Criteria make(
      final Optional<String> filters,
      final Optional<String> sorts,
      final Optional<Integer> pageNumber,
      final Optional<Integer> pageSize
  ) {
    List<PlainFilter> plainFilterList = new ArrayList<>();
    if (filters.isPresent() && !filters.get().isBlank()) {
      final String filtersValue = filters.get();
      this.validateFilterStringFormat(filtersValue);
      this.validateAllFiltersAreInWhiteList(filtersValue);
      try {
        plainFilterList = this.makeFilterList(filtersValue);
      } catch (CriteriaException ex) {
        throw new BadRequestException(exceptionMessageService.getMessage(ex), ex);
      }
    }
    List<PlainSort> plainSortList = configDefaultSort();
    if (sorts.isPresent() && !sorts.get().isBlank()) {
      final String sortsValue = sorts.get();
      this.validateSortStringFormat(sortsValue);
      this.validateAllSortsAreInWhiteList(sortsValue);
      try {
        plainSortList = this.makeSortList(sortsValue);
      } catch (CriteriaException ex) {
        throw new BadRequestException(exceptionMessageService.getMessage(ex), ex);
      }
    }
    try {
      return Criteria.of(
          Filters.of(plainFilterList),
          Sorts.of(plainSortList),
          PageNumber.of(pageNumber.orElse(0)),
          PageSize.of(pageSize.orElse(configDefaultPageSize()))
      );
    } catch (CriteriaException ex) {
      throw new BadRequestException(exceptionMessageService.getMessage(ex), ex);
    }
  }

  // ──────────────────────────────────────────────
  // 2. Protected abstract - Configuration hooks
  // ──────────────────────────────────────────────

  protected abstract Map<String, Schema> configFilterWhiteListAndSchemas();

  protected abstract Set<String> configSortWhiteList();

  protected abstract List<PlainSort> configDefaultSort();

  // ──────────────────────────────────────────────
  // 3. Protected config - With defaults
  // ──────────────────────────────────────────────

  protected Set<Schema> configSchemasRequiringParentheses() {
    return Set.of(
        Schema.CONTAINS_ALL_STRINGS,
        Schema.CONTAINS_ALL_NUMBERS,
        Schema.CONTAINS_ALL_UUIDS
    );
  }

  protected Integer configDefaultPageSize() {
    return DEFAULT_PAGE_SIZE;
  }

  // ──────────────────────────────────────────────
  // 4. Private - Filter
  // ──────────────────────────────────────────────

  private void validateFilterStringFormat(final String filters) {
    splitRespectingParentheses(filters).forEach(filter -> {
      final String[] filterSegments = filter.split(INTERNAL_FILTER_AND_SORT_SEPARATOR,
          MAX_NUMBER_OF_FILTER_SEGMENTS);
      final int numberOfInternalSegments = filterSegments.length;
      if (numberOfInternalSegments != MAX_NUMBER_OF_FILTER_SEGMENTS) {
        throw new BadRequestException(MESSAGE_FILTERS_FORMAT_INCORRECT);
      }
      if (filterSegments[0].isBlank()) {
        throw new BadRequestException(MESSAGE_FILTER_FIELD_CANNOT_BE_EMPTY);
      }
      if (filterSegments[1].isBlank()) {
        throw new BadRequestException(MESSAGE_FILTER_OPERATOR_CANNOT_BE_EMPTY);
      }
      if (filterSegments[2].isBlank()) {
        throw new BadRequestException(MESSAGE_FILTER_VALUE_CANNOT_BE_EMPTY);
      }
    });
  }

  private void validateAllFiltersAreInWhiteList(final String filters) {
    splitRespectingParentheses(filters).forEach(filter -> {
      final String field = filter.split(INTERNAL_FILTER_AND_SORT_SEPARATOR, -1)[0];
      if (!filterWhiteList().contains(field)) {
        throw new BadRequestException(MESSAGE_FILTER_FIELD_NOT_ALLOWED
            .formatted(field, "'" + String.join("','", filterWhiteList()) + "'"));
      }
    });
  }

  private List<PlainFilter> makeFilterList(final String filters) {
    return splitRespectingParentheses(filters)
        .stream()
        .map(filter -> {
          final String[] filterSegments = filter.split(INTERNAL_FILTER_AND_SORT_SEPARATOR,
              MAX_NUMBER_OF_FILTER_SEGMENTS);
          final String field = filterSegments[0];
          final String operator = filterSegments[1];
          final String rawValue = filterSegments[2];
          final Schema schema = configFilterWhiteListAndSchemas().get(field);
          final String value = configSchemasRequiringParentheses().contains(schema)
              ? validateAndStripParentheses(rawValue, field)
              : rawValue;
          return PlainFilter.of(field, operator, value, schema);
        }).toList();
  }

  private String validateAndStripParentheses(final String value, final String field) {
    final String trimmed = value.strip();
    if (!trimmed.startsWith("(") || !trimmed.endsWith(")")) {
      throw new BadRequestException(
          MESSAGE_FILTER_VALUE_INVALID_LIST_FORMAT.formatted(field));
    }
    final String inner = trimmed.substring(1, trimmed.length() - 1).strip();
    if (inner.isBlank()) {
      throw new BadRequestException(
          MESSAGE_FILTER_VALUE_EMPTY_LIST.formatted(field));
    }
    return inner;
  }

  // ──────────────────────────────────────────────
  // 5. Private - Sort
  // ──────────────────────────────────────────────

  private void validateSortStringFormat(final String filters) {
    Arrays.stream(filters.split(EXTERNAL_FILTER_AND_SORT_SEPARATOR, -1)).forEach(sort -> {
      final String[] sortSegments = sort.split(INTERNAL_FILTER_AND_SORT_SEPARATOR, -1);
      final int numberOfInternalSegments = sortSegments.length;
      if (numberOfInternalSegments != NUMBER_OF_SORT_SEGMENTS) {
        throw new BadRequestException(MESSAGE_SORTS_FORMAT_INCORRECT);
      }
      if (sortSegments[0].isBlank()) {
        throw new BadRequestException(MESSAGE_SORT_FIELD_CANNOT_BE_EMPTY);
      }
      if (sortSegments[1].isBlank()) {
        throw new BadRequestException(MESSAGE_SORT_ORDER_CANNOT_BE_EMPTY);
      }
    });
  }

  private void validateAllSortsAreInWhiteList(final String sorts) {
    Arrays.stream(sorts.split(EXTERNAL_FILTER_AND_SORT_SEPARATOR, -1)).forEach(sort -> {
      final String field = sort.split(INTERNAL_FILTER_AND_SORT_SEPARATOR, -1)[0];
      if (!configSortWhiteList().contains(field)) {
        throw new BadRequestException(MESSAGE_SORT_FIELD_NOT_ALLOWED
            .formatted(field, "'" + String.join("','", configSortWhiteList()) + "'"));
      }
    });
  }

  private List<PlainSort> makeSortList(final String sorts) {
    return Arrays.stream(sorts.split(EXTERNAL_FILTER_AND_SORT_SEPARATOR, -1))
        .map(sort -> {
          final String[] sortSegments = sort.split(INTERNAL_FILTER_AND_SORT_SEPARATOR, -1);
          final String sortField = sortSegments[0];
          final String sortOrder = sortSegments[1];
          return PlainSort.of(
              sortField,
              sortOrder
          );
        }).toList();
  }

  // ──────────────────────────────────────────────
  // 6. Private - Utility
  // ──────────────────────────────────────────────

  private Set<String> filterWhiteList() {
    return configFilterWhiteListAndSchemas().keySet();
  }

  private List<String> splitRespectingParentheses(final String value) {
    final List<String> result = new ArrayList<>();
    final StringBuilder current = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      if (c == '(') {
        depth++;
        current.append(c);
      } else if (c == ')') {
        depth--;
        current.append(c);
      } else if (c == ',' && depth == 0) {
        result.add(current.toString());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }
    result.add(current.toString());
    return result;
  }
}
