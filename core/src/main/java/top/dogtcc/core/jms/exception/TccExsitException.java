package top.dogtcc.core.jms.exception;

public class TccExsitException extends Exception {
    public TccExsitException() {
    }

    public TccExsitException(String message) {
        super(message);
    }

    public TccExsitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TccExsitException(Throwable cause) {
        super(cause);
    }
}
