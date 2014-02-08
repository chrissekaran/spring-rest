package my.tests.web;

import my.tests.entity.Term;
import my.tests.persistence.TermRepository;
import my.tests.web.exception.TermException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.List;


import static org.slf4j.LoggerFactory.getLogger;

/**
 * Term controller for CRUD operations on a Term object
 *
 * Controllers are not normally @Transactional, but for the purposes of a demo.
 *
 */
@Controller
@Transactional
public class TermController {

    @Autowired
    private View jsonView;

    //Injecting the Dao directly for demo
    @Resource
    private TermRepository termRepository;

    private static final String DATA_FIELD = "json";

    private static final String ERROR_FIELD = "error";

    private static final Logger LOG = getLogger(TermController.class);

    /**
     * find Term by Id
     *
     * @param termId
     * @return
     */
    @RequestMapping(value = "/terms/{termId}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView termById(@PathVariable("termId") String termId) throws TermException {

        Term term = null;
        /* validate term Id parameter */
        if (termId == null) {
            String sMessage = "Error invoking getTerm - Invalid term Id parameter";

        }

        try {
            term = termRepository.findOne(termId);
        } catch (Exception e) {
            String sMessage = "Error invoking findTerm. [%1$s]";

        }
        if(term != null)    {
            LOG.debug("Found term: " + term.toString());
            return new ModelAndView(jsonView, DATA_FIELD, term);
        }
        //Exception handling  Spring 3.2+ using the @ControllerAdvice
        throw new TermException("Did not find the requested term");
    }


    /**
     * Create a Term
     *
     * @param term
     * @param httpResponse
     * @param request
     * @return
     */
    @RequestMapping(value = {"/terms/"}, method = {RequestMethod.POST})
    public @ResponseBody Term createTerm(@RequestBody Term term,
                                   HttpServletResponse httpResponse, WebRequest request) throws TermException {

        Term createdTerm = null;
        LOG.debug("Creating Term: " + term.toString());

        try {
            //Create the term in Service
            createdTerm = termRepository.saveAndFlush(term);

        } catch (Exception e) {
            String sMessage = "Error creating new Term. [%1$s]";
            throw new TermException(sMessage, e);
        }

        /* set HTTP response code */
        httpResponse.setStatus(HttpStatus.CREATED.value());

        /* set location of created resource */
        httpResponse.setHeader("Location", request.getContextPath() + "/terms/" + term.getTermId());

        if(createdTerm != null) {
            return createdTerm;
        }
        throw new TermException("Could not create Term");
    }


    /**
     * List all terms
     *
     * @return  The list of all the terms
     */
    @RequestMapping(value = "/terms/", method = RequestMethod.GET)
    public @ResponseBody List<Term> listTerms() throws TermException {
        List<Term> terms = null;

        try {

            terms = termRepository.findAll();

        } catch (Exception e) {
            String sMessage = "Error getting all terms from Repository";
            throw new TermException(sMessage, e);
        }

        if(terms.isEmpty()) {
            LOG.info("No terms are created yet");
        }
        LOG.info("Returning Term: " + terms.toString());

        return terms;
    }

    /**
     * Updates an existing term
     *
     * @param term
     * @param termId
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/terms/{termId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Term updateTerm(@RequestBody Term term, @PathVariable("termId") String termId,
                                   HttpServletResponse httpResponse, ModelMap modelMap) throws TermException {

        LOG.debug("Updating Term: " + term.toString());

        /* validate term Id parameter */
        if (termId == null) {
            String sMessage = "Error updating term - Invalid term Id parameter";
            LOG.error(sMessage);
            throw new TermException(sMessage);
        }
        Term term1 = null;
        try {
            term1 =  termRepository.findOne(termId);
            term1.setTermId(term.getTermId());
            term1.setTermText(term.getTermText());
        } catch (Exception e) {
            String sMessage = "Error updating term. Possibly not found in repository";
            throw new TermException(sMessage, e);
        }
        httpResponse.setStatus(HttpStatus.OK.value());
        if(term1 != null)   {
            return  term;
        }
        throw new TermException(String.format("Could not update Term with id: %d", termId));
    }


    @RequestMapping(value = "/terms/{termId}", method = RequestMethod.DELETE)
    public ModelAndView removeTerm(@PathVariable("termId") String termId,
                                   HttpServletResponse httpResponse) throws TermException {

        LOG.debug("Deleting Term Id: " + termId.toString());

        /* validate term Id parameter */
        if (termId == null) {
            String sMessage = "Error deleting term - Invalid term Id parameter";
            throw new TermException(sMessage);
        }

        try {
            termRepository.delete(termId);
        } catch (Exception e) {
            String sMessage = "Error invoking delete on repository. ";
            throw new TermException(String.format(sMessage, e.toString()));
        }
        httpResponse.setStatus(HttpStatus.OK.value());
        return new ModelAndView(jsonView, DATA_FIELD, null);
    }

    /**
     * Injector methods.
     *
     * @param termRepository the new term service
     */
    public void setTermRepository(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    /**
     * Injector methods.
     *
     * @param view the new json view
     */
    public void setJsonView(View view) {
        jsonView = view;
    }


    /**
     * Create an error REST response.
     *
     * @param sMessage the s message
     * @return the model and view
     */
    private ModelAndView createErrorResponse(String sMessage) {
        return new ModelAndView(jsonView, ERROR_FIELD, sMessage);
    }


}
