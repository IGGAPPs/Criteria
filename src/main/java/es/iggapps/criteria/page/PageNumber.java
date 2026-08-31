package es.iggapps.criteria.page;

import es.iggapps.criteria.exception.CriteriaValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageNumber {

  private static final String MESSAGE_PAGE_NUMBER_CANNOT_BE_LOWER_THAN_ZERO
      = "El número de página no puede ser menor a 0.";

  private final Integer pageNumber;

  public static PageNumber of(final Integer pageNumber) {
    if (pageNumber < 0) {
      throw new CriteriaValidationException(MESSAGE_PAGE_NUMBER_CANNOT_BE_LOWER_THAN_ZERO);
    }
    return new PageNumber(pageNumber);
  }
}
