package es.iggapps.criteria.exception;

public class CriteriaException extends RuntimeException {

  public CriteriaException(final String message) {
    super(message);
  }

  public CriteriaException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
