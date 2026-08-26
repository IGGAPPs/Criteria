package es.iggapps.criteria.common.domain.exception;


public class DomainException extends RuntimeException {
    public DomainException(String message, Throwable e) {
        super(message, e);
    }

    public DomainException(String message) {
        super(message);
    }
}
