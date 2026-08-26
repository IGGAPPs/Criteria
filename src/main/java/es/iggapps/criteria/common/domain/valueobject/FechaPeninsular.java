package es.iggapps.criteria.common.domain.valueobject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import es.iggapps.criteria.common.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class FechaPeninsular implements Comparable<FechaPeninsular> {

  private static final String ZONA_HORARIA_PENINSULAR = "Europe/Madrid";
  private static final String MESSAGE_FECHA_PENINSULAR_INVALID_FORMAT =
      "El valor de tipo 'FechaPeninsular' no tiene un formato correcto (yyyy-mm-dd) o es una fecha no existente.";
  private static final String MESSAGE_FECHA_PENINSULAR_INVALID_MONTHS_VALUE =
      "El parámetro 'plusMonths' requiere un valor > 0.";

  private final LocalDate localDate;

  public static FechaPeninsular of(final String date) throws DomainException {
    try {
      return new FechaPeninsular(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE));
    } catch (DateTimeParseException e) {
      throw new DomainException(MESSAGE_FECHA_PENINSULAR_INVALID_FORMAT, e);
    }
  }

  public static FechaPeninsular of(final LocalDate date) {
    return new FechaPeninsular(date);
  }

  public static FechaPeninsular of(final Instant date) {
    return new FechaPeninsular(date.atZone(ZoneId.of(ZONA_HORARIA_PENINSULAR)).toLocalDate());
  }

  public static FechaPeninsular fromString(final String date) throws DomainException {
    try {
      return new FechaPeninsular(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE));
    } catch (DateTimeParseException e) {
      throw new DomainException(MESSAGE_FECHA_PENINSULAR_INVALID_FORMAT, e);
    }
  }

  public boolean isAfter(final FechaPeninsular fechaPeninsular) {
    return this.localDate.isAfter(fechaPeninsular.localDate);
  }

  public boolean isBefore(final FechaPeninsular fechaPeninsular) {
    return this.localDate.isBefore(fechaPeninsular.localDate);
  }

  public FechaHoraPeninsular endOfDay() {
    return FechaHoraPeninsular.of(this.localDate.atTime(LocalTime.MAX));
  }

  public FechaHoraPeninsular startOfDay() {
    return FechaHoraPeninsular.of(this.localDate.atTime(LocalTime.MIN));
  }

  public FechaPeninsular plusMonths(final int plusMonths) {
    if (plusMonths <= 0) {
      throw new DomainException(MESSAGE_FECHA_PENINSULAR_INVALID_MONTHS_VALUE);
    }
    return new FechaPeninsular(this.localDate.plusMonths(plusMonths));
  }

  public FechaPeninsular minusMonths(final int minusMonths) {
    if (minusMonths <= 0) {
      throw new DomainException(MESSAGE_FECHA_PENINSULAR_INVALID_MONTHS_VALUE);
    }
    return new FechaPeninsular(this.localDate.minusMonths(minusMonths));
  }

  public static FechaPeninsular today() {
    return new FechaPeninsular(LocalDate.now());
  }

  @Override
  public String toString() {
    return this.localDate.toString();
  }

  @Override
  public int compareTo(final FechaPeninsular fechaPeninsular1) {
    return localDate.compareTo(fechaPeninsular1.localDate);
  }
}
