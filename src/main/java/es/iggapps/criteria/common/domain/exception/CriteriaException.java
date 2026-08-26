package es.iggapps.criteria.common.domain.exception;


public class CriteriaException extends RuntimeException {
    public CriteriaException(String message, Throwable e) {
        super(message, e);
    }

    public CriteriaException(String message) {
        super(message);
    }
}
