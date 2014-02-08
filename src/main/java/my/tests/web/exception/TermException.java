package my.tests.web.exception;

/**
 * Created by Chris Sekaran on 2/8/14.
 */
public class TermException extends Exception {


    public TermException() {
    }

    public TermException(String message) {
        super(message);
    }

    public TermException(String message, Throwable cause) {
        super(message, cause);
    }
}
