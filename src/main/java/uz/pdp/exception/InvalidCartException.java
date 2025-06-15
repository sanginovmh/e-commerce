package uz.pdp.exception;

public class InvalidCartException extends RuntimeException {
    public InvalidCartException(String message) {
        super(message);
    }

    public InvalidCartException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCartException(Throwable cause) {
        super(cause);
    }
}
