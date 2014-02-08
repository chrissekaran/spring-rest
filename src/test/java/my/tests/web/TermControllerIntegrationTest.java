package my.tests.web;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import my.tests.ControllerUnitTestConfig;
import my.tests.entity.Term;
import my.tests.persistence.TermRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Chris Sekaran on 2/6/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ControllerUnitTestConfig.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@Transactional
public class TermControllerIntegrationTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private TermRepository termRepository;

    @Resource
    private DataSource dataSource;

    @Autowired
    private WebApplicationContext webApplicationContext;


    private IDatabaseConnection getConnection() throws Exception{
        // get connection
        Connection con = dataSource.getConnection();
        DatabaseMetaData databaseMetaData = con.getMetaData();
        IDatabaseConnection connection = new DatabaseConnection(con,"PUBLIC");
        return connection;
    }

    private IDataSet getDataSet() throws Exception  {
        // get insert data
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        IDataSet dataSet = builder.build(new File("src/test/resources/my/tests/web/termSampleData.xml"));
        builder.setColumnSensing(true);
        return dataSet;

    }


    @Before
    public void setUp() throws Exception   {
        //reset mock between tests or stubbing and verified behavior would "leak" from one test to another.

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
    }

    @Test
    public void testControllerTermById() throws Exception {

        Term term = new Term();
        term.setTermId("101");
        term.setTermText("The British Broadcasting Corporation is a British public service broadcasting");

        mockMvc.perform(get("/terms/" + term.getTermId())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("json"))
                .andExpect(jsonPath("$.json.termId", is(term.getTermId())))
                .andExpect(jsonPath("$.json.termText", is(term.getTermText())));

    }

    @Test
    public void testControllerTermByIdFailsOnWrongMapping() throws Exception {
        //The British Broadcasting Corporation is a British public service broadcasting
        Term term = new Term();
        term.setTermId("101");
        term.setTermText("The British Broadcasting Corporation is a British public service broadcasting");

        //Not setting the {termId} pathVariable
        mockMvc.perform(get("/terms/termById/" + "105")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }


    @Test
    public void testControllerListTerms() throws Exception {

        Term term = new Term();
        term.setTermId("103");
        term.setTermText("Mary has a little lamb");

        mockMvc.perform(get("/terms/")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(term))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].termId", is("101")))
                .andExpect(jsonPath("$[0].termText", is("The British Broadcasting Corporation is a British public service broadcasting")))
                .andExpect(jsonPath("$[1].termId", is("102")));

    }


    @Test
    public void testControllerCreateTerm() throws Exception {

        Term term = new Term();
        term.setTermId("103");
        term.setTermText("Mary has a little lamb");

        mockMvc.perform(post("/terms/")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(term))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.termId", is(term.getTermId())))
                .andExpect(jsonPath("$.termText", is(term.getTermText())));
    }

    @Test
    public void testControllerUpdateTerm() throws Exception {
        //The British Broadcasting Corporation is a British public service broadcasting
        Term term = new Term();
        term.setTermId("101");
        term.setTermText("Mary has a little lamb");

        mockMvc.perform(put("/terms/"+term.getTermId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(term))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termId", is(term.getTermId())))
                .andExpect(jsonPath("$.termText", is(term.getTermText())));

    }

    @Test
    public void testControllerUpdateTermAdviceOnFail() throws Exception {
        //The British Broadcasting Corporation is a British public service broadcasting
        Term term = new Term();
        term.setTermId("202");
        term.setTermText("Mary has a little lamb");

        mockMvc.perform(put("/terms/"+term.getTermId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(term))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestUrl", is("/terms/202")))
                .andExpect(jsonPath("$.status", is("Failed ")))
                .andExpect(jsonPath("$.message", is("Error updating term. Possibly not found in repository")));

    }


    @Test
    public void testControllerRemoveTerm() throws Exception {
        //The British Broadcasting Corporation is a British public service broadcasting
        Term term = new Term();
        term.setTermId("101");
        term.setTermText("Mary has a little lamb");

        mockMvc.perform(delete("/terms/" + term.getTermId())
                .contentType(APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Utility methods
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
