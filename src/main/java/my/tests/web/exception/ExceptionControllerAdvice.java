package my.tests.web.exception;


import my.tests.web.TermController;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Chris Sekaran on 2/8/14.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOG = getLogger(TermController.class);

    @ExceptionHandler(TermException.class)
    @ResponseBody
    public ExceptionInfo handleException(HttpServletRequest req, TermException e) {
        LOG.info("Exception occurred at URL: "+req.getRequestURL());
        return new ExceptionInfo(req.getRequestURI(), "Failed ", e.getMessage());
    }
}