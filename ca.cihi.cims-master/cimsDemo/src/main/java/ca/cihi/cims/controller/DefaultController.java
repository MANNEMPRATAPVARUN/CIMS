package ca.cihi.cims.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DefaultController {

    private static final Log LOGGER = LogFactory.getLog(DefaultController.class);

    @RequestMapping("/index.htm")
    public String viewHome() {
        return "index";
    }

    @RequestMapping("/accessDenied.htm")
    public String viewAccessDenied() {
        return "accessDenied";
    }

    @RequestMapping("/error.htm")
    public String viewError(final HttpServletRequest request, final HttpServletResponse response) {
        if (request.getAttribute("javax.servlet.error.exception") != null) {
            String error = request.getAttribute("javax.servlet.error.exception").toString();
            LOGGER.error(error);
            if (error.contains("HttpSessionRequiredException")) {
                return "sessionExpired";
            }
        }
        return "error";
    }

}
