package my.tests.web.exception;

import my.tests.ControllerUnitTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Chris Sekaran on 2/8/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ControllerUnitTestConfig.class)
public class TestExceptionControllerAdvice {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;


    @Before
    public void setup() {
        mockMvc = webAppContextSetup(wac).build();
    }

    /**
     * Add a controller to the context so that the exception handling is @ControllerAdvice 'd
     */
    @Controller
    @RequestMapping("/tests")
    public static class ExceptionAdvisedController {

        @RequestMapping(value = "/exception", method = RequestMethod.GET)
        public @ResponseBody
        String find() throws TermException {
            throw new TermException("Exception is intercepted");
        }
    }

    @Test
    public void testSomething() throws Exception {
        mockMvc.perform(get("/tests/exception/")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestUrl", is("/tests/exception/")))
                .andExpect(jsonPath("$.status", is("Failed ")))
                .andExpect(jsonPath("$.message", is("Exception is intercepted")));

    }
}
