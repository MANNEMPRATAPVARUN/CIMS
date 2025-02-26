package ca.cihi.cims.sct.web.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class WelcomeController {

    private static final Log LOGGER = LogFactory.getLog(WelcomeController.class);
    private static final String INDEX_VIEW = "index";    
    
    
    @RequestMapping(value = "/index.htm", method = RequestMethod.GET) 
    public String setupForm(final Model model, final HttpSession session) {
        LOGGER.debug("setupForm"); 
        
        return INDEX_VIEW;
    }    
}