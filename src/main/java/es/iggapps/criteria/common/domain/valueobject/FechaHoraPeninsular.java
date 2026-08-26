package es.iggapps.criteria.common.domain.valueobject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class FechaHoraPeninsular implements Comparable<FechaHoraPeninsular> {

  private static final String ZONA_HORARIA_PENINSULAR = "Europe/Madrid";
  private final OffsetDateTime fechaHoraPeninsular;

  private FechaHoraPeninsular(final OffsetDateTime fechaHoraPeninsular) {
    this.fechaHoraPeninsular = fechaHoraPeninsular.truncatedTo(ChronoUnit.SECONDS);
  }

  public static FechaHoraPeninsular of(final Instant instant) {

    return new FechaHoraPeninsular(
        instant.atZone(ZoneId.of(ZONA_HORARIA_PENINSULAR)).toOffsetDateTime());
  }

  public static FechaHoraPeninsular of(final LocalDateTime localDateTime) {
    return new FechaHoraPeninsular(
        localDateTime.atZone(ZoneId.of(ZONA_HORARIA_PENINSULAR)).toOffsetDateTime());
  }

  public static FechaHoraPeninsular now() {
    return of(Instant.now());
  }

  public LocalDateTime toLocalDateTime() {
    return this.fechaHoraPeninsular.toLocalDateTime();
  }

  public Instant toInstant() {
    return this.fechaHoraPeninsular.toInstant();
  }

  @Override
  public int compareTo(final FechaHoraPeninsular fechaHoraPeninsular1) {
    return fechaHoraPeninsular.compareTo(fechaHoraPeninsular1.fechaHoraPeninsular);
  }
}
