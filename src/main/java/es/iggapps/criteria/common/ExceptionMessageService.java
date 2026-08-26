package es.iggapps.criteria.common;

public class ExceptionMessageService {
    public String getMessage(Exception ex) {
        return ex.getLocalizedMessage();
    }
}
