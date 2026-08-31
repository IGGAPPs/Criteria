package es.iggapps.criteria.exception;

public class CriteriaValidationException extends CriteriaException {

  public CriteriaValidationException(final String message) {
    super(message);
  }

  public CriteriaValidationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
