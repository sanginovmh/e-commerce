package uz.pdp.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String message) {
        super(message);
    }

    public InvalidCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCategoryException(Throwable cause) {
        super(cause);
    }
}
