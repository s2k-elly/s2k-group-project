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

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String msg) { super(msg); }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String msg) { super(msg); }
    }
}