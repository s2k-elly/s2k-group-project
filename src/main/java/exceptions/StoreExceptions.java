package exceptions;

/**
 * Common store runtime exceptions as static inner classes for convenience.
 */
public final class StoreExceptions {
    private StoreExceptions() {}

    public static class PermissionException extends RuntimeException {
        public PermissionException(String msg) { super(msg); }
    }

    public static class OutOfStockException extends RuntimeException {
        public OutOfStockException(String msg) { super(msg); }
    }

    public static class UserException extends RuntimeException { // CHANGED NAME FROM UML TO BETTER FIT SCOPE OF EXCEPTIONS
        public UserException(String msg) { super(msg); }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String msg) { super(msg); }
    }
}