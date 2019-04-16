package top.dogtcc.core.jms.exception;

public class LockNotExsitException extends Exception {
    public LockNotExsitException() {
    }

    public LockNotExsitException(String message) {
        super(message);
    }

    public LockNotExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockNotExsitException(Throwable cause) {
        super(cause);
    }
}
