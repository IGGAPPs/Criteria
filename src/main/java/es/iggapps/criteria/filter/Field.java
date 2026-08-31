package es.iggapps.criteria.filter;

import es.iggapps.criteria.exception.CriteriaException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class Field {

  private static final String MESSAGE_EMPTY_FIELD = "Campo con nombre vacío.";

  private final String field;

  public static Field of(final String field) {
    if (field.isBlank()) {
      throw new CriteriaException(MESSAGE_EMPTY_FIELD);
    }
    return new Field(field);
  }
}
