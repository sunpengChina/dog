package top.dogtcc.core.jms.exception;

public class TccNotExsitException extends Exception {
    public TccNotExsitException() {
    }

    public TccNotExsitException(String message) {
        super(message);
    }

    public TccNotExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TccNotExsitException(Throwable cause) {
        super(cause);
    }
}
