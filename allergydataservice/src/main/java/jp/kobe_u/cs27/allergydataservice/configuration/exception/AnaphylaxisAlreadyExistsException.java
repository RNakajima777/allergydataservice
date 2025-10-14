package jp.kobe_u.cs27.allergydataservice.configuration.exception;

public class AnaphylaxisAlreadyExistsException extends RuntimeException {
    public AnaphylaxisAlreadyExistsException(String message) {
        super(message);
    }
}
