package top.dogtcc.core.jms.exception;

public class CallNotExsitException extends Exception {
    public CallNotExsitException() {
    }

    public CallNotExsitException(String message) {
        super(message);
    }

    public CallNotExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallNotExsitException(Throwable cause) {
        super(cause);
    }
}
