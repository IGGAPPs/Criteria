package es.iggapps.criteria.page;

import es.iggapps.criteria.exception.CriteriaValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageSize {

  public static final Integer MAX_PAGE_SIZE = 50;
  private static final String MESSAGE_PAGE_SIZE_CANNOT_BE_LOWER_THAN_ONE
      = "El tamaño de página no puede ser menor a 1.";
  private static final String MESSAGE_PAGE_SIZE_CANNOT_BE_LOWER_THAN_MAXIMUM
      = "El tamaño de la página excede el máximo permitido '%s'.";

  private final Integer pageSize;

  public static PageSize of(final Integer pageSize) {
    if (pageSize < 1) {
      throw new CriteriaValidationException(MESSAGE_PAGE_SIZE_CANNOT_BE_LOWER_THAN_ONE);
    }
    if (pageSize > MAX_PAGE_SIZE) {
      throw new CriteriaValidationException(
          MESSAGE_PAGE_SIZE_CANNOT_BE_LOWER_THAN_MAXIMUM.formatted(MAX_PAGE_SIZE));
    }
    return new PageSize(pageSize);
  }
}
