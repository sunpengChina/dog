package top.dogtcc.core.jms.exception;

public class LockExsitException extends Exception {
    public LockExsitException() {
    }

    public LockExsitException(String message) {
        super(message);
    }

    public LockExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockExsitException(Throwable cause) {
        super(cause);
    }
}
