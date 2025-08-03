package app.Errors;

public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }

}
