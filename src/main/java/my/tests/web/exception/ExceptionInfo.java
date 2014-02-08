package my.tests.web.exception;

/**
 * Created by Chris Sekaran on 2/8/14.
 */
public class ExceptionInfo {

    private String requestUrl;

    private String status;

    private String message;

    public ExceptionInfo(String requestUrl, String status, String message) {
        this.requestUrl = requestUrl;
        this.status = status;
        this.message = message;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
