package es.iggapps.criteria;

import es.iggapps.criteria.common.BadRequestException;
import es.iggapps.criteria.common.domain.criteria.Criteria;
import es.iggapps.criteria.common.domain.criteria.model.filter.FilterConfig;
import es.iggapps.criteria.common.domain.criteria.model.filter.Filters;
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


public abstract class CriteriaFactory {

  /* Filter error messages */
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
  private static final String MESSAGE_FILTER_LIST_FORMAT_INCORRECT =
      "El valor del filtro '%s' debe tener formato '[valor1,valor2,...]'.";
  private static final String MESSAGE_FILTER_FIELD_NOT_ALLOWED
      = "El campo '%s' no está permitido para el filtrado. La lista de campos permitidos es [%s].";

  /* Sort error messages */
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

  private static final String EXTERNAL_FILTER_AND_SORT_SEPARATOR = ",";
  private static final String INTERNAL_FILTER_AND_SORT_SEPARATOR = ":";
  private static final Integer MAX_NUMBER_OF_FILTER_SEGMENTS = 3;
  private static final Integer NUMBER_OF_SORT_SEGMENTS = 2;
  private static final Integer DEFAULT_PAGE_SIZE = 25;

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
      } catch (CriteriaException e) {
        throw new BadRequestException(e.getMessage(), e);
      }
    }
    List<PlainSort> plainSortList = configDefaultSort();
    if (sorts.isPresent() && !sorts.get().isBlank()) {
      final String sortsValue = sorts.get();
      this.validateSortStringFormat(sortsValue);
      this.validateAllSortsAreInWhiteList(sortsValue);
      try {
        plainSortList = this.makeSortList(sortsValue);
      } catch (CriteriaException e) {
        throw new BadRequestException(e.getMessage(), e);
      }
    }
    try {
      return Criteria.of(
          Filters.of(plainFilterList),
          Sorts.of(plainSortList),
          PageNumber.of(pageNumber.orElse(0)),
          PageSize.of(pageSize.orElse(configDefaultPageSize()))
      );
    } catch (CriteriaException e) {
      throw new BadRequestException(e.getMessage(), e);
    }
  }

  private void validateFilterStringFormat(final String filters) {
    splitRespectingBrackets(filters).forEach(filter -> {
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
      final FilterConfig config = configFilterWhiteList().get(filterSegments[0]);
      if (config != null && config.isList() && !isValidListFormat(filterSegments[2])) {
        throw new BadRequestException(MESSAGE_FILTER_LIST_FORMAT_INCORRECT.formatted(filterSegments[0]));
      }
    });
  }

  private boolean isValidListFormat(final String value) {
    final String trimmed = value.strip();
    if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
      return false;
    }
    final String inner = trimmed.substring(1, trimmed.length() - 1).strip();
    return !inner.isBlank();
  }

  private void validateAllFiltersAreInWhiteList(final String filters) {
    splitRespectingBrackets(filters).forEach(filter -> {
      final String field = filter.split(INTERNAL_FILTER_AND_SORT_SEPARATOR, -1)[0];
      if (!filterWhiteList().contains(field)) {
        throw new BadRequestException(MESSAGE_FILTER_FIELD_NOT_ALLOWED
            .formatted(field, "'" + String.join("','", filterWhiteList()) + "'"));
      }
    });
  }

  protected abstract Map<String, FilterConfig> configFilterWhiteList();

  private Set<String> filterWhiteList() {
    return configFilterWhiteList().keySet();
  }

  private List<PlainFilter> makeFilterList(final String filters) {
    return splitRespectingBrackets(filters)
        .stream()
        .map(filter -> {
          final String[] filterSegments = filter.split(INTERNAL_FILTER_AND_SORT_SEPARATOR,
              MAX_NUMBER_OF_FILTER_SEGMENTS);
          final FilterConfig config = configFilterWhiteList().get(filterSegments[0]);
          final String value = config.isList()
              ? stripBrackets(filterSegments[2])
              : filterSegments[2];
          return PlainFilter.of(
              filterSegments[0],
              filterSegments[1],
              value,
              config.type(),
              config.isList(),
              config.operators()
          );
        }).toList();
  }

  private String stripBrackets(final String value) {
    return value.strip().substring(1, value.strip().length() - 1).strip();
  }

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

  protected abstract Set<String> configSortWhiteList();

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

  protected Integer configDefaultPageSize() {
    return DEFAULT_PAGE_SIZE;
  }

  protected abstract List<PlainSort> configDefaultSort();

  private List<String> splitRespectingBrackets(final String value) {
    final List<String> result = new ArrayList<>();
    final StringBuilder current = new StringBuilder();
    int bracketDepth = 0;
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      if (c == '[') {
        bracketDepth++;
        current.append(c);
      } else if (c == ']') {
        bracketDepth--;
        current.append(c);
      } else if (c == ',' && bracketDepth == 0) {
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
