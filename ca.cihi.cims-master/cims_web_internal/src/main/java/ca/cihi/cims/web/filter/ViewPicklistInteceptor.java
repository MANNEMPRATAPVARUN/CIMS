package ca.cihi.cims.web.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.PickListTableViewBean;
import ca.cihi.cims.web.bean.refset.RefsetBaseBean;

public class ViewPicklistInteceptor extends HandlerInterceptorAdapter {
    /**
     * Reference to logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ViewPicklistInteceptor.class);

    /**
     * Reference to refset service.
     */
    @Autowired
    private RefsetService refsetService;

    /**
     * Map contains all the available context retrievers.
     */
    private static final Map<String, ContextRetriever> CONTEXT_RETRIEVER_MAP = new HashMap<String, ContextRetriever>();

    public ViewPicklistInteceptor() {
        ContextRetriever ICD10CAContextRetriever = new ICD10CAContextRetriever();
        ContextRetriever CCIContextRetriever = new CCIContextRetriever();

        synchronized (CONTEXT_RETRIEVER_MAP) {
            CONTEXT_RETRIEVER_MAP.put("ICD-10-CA", ICD10CAContextRetriever);
            CONTEXT_RETRIEVER_MAP.put("CCI", CCIContextRetriever);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        PickListTableViewBean pickListTableViewBean = (PickListTableViewBean) modelAndView.getModel().get("picklist");
        RefsetBaseBean refsetBaseBean = (RefsetBaseBean) modelAndView.getModel().get("viewBean");

        try {
            long contextId = refsetBaseBean.getContextId();
            long elementId = refsetBaseBean.getElementId();
            long elementVersionId = refsetBaseBean.getElementVersionId();

            Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);

            ContextRetriever contextRetriever = getRetriever(pickListTableViewBean.getClassificationStandard());

            if (contextRetriever == null) {
                LOGGER.error("invalid classification standard: " + pickListTableViewBean.getClassificationStandard());

                return;
            }

            Long classificationContextId = contextRetriever.getContextId(refset);
            request.setAttribute("classificationContextId", classificationContextId);
            request.setAttribute("sctVersionCode", refset.getSCTVersionCode());
        } catch (Exception e) {
            LOGGER.error("Exception: " + e);
        }
    }

    private ContextRetriever getRetriever(String classificationStandard) {
        return CONTEXT_RETRIEVER_MAP.get(classificationStandard);
    }

    public interface ContextRetriever {
        /**
         * Get the right context Id based on classification.
         * 
         * @param refset
         *            the refset.
         * @return the context Id based on classification.
         */
        public Long getContextId(Refset refset);
    }

    public class ICD10CAContextRetriever implements ContextRetriever {
        public Long getContextId(Refset refset) {
            try {
                return refset.getICD10CAContextId();
            } catch (PropertyKeyNotFoundException e) {
                LOGGER.error("Exception: " + e);
            }

            return null;
        }
    }

    public class CCIContextRetriever implements ContextRetriever {
        public Long getContextId(Refset refset) {
            try {
                return refset.getCCIContextId();
            } catch (PropertyKeyNotFoundException e) {
                LOGGER.error("Exception: " + e);
            }

            return null;
        }
    }
}
