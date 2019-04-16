package top.dogtcc.core.jms.exception;

public class CallExsitException extends Exception {
    public CallExsitException() {
    }

    public CallExsitException(String message) {
        super(message);
    }

    public CallExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallExsitException(Throwable cause) {
        super(cause);
    }
}
