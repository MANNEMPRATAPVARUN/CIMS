package ca.cihi.cims.web.controller.refset;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.model.refset.BaseOutputContent;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.service.refset.EvolutionService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetExportUtils;

@Controller
@RequestMapping(value = "/refset/picklist")
public class EvolutionController {
	
    private static final Log LOGGER = LogFactory.getLog(EvolutionController.class);
    
	@Autowired
	private RefsetService refsetService;
	
	@Autowired
	private EvolutionService evolutionService;
	
	public RefsetService getRefsetService() {
		return refsetService;
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	public EvolutionService getEvolutionService() {
		return evolutionService;
	}

	public void setEvolutionService(EvolutionService evolutionService) {
		this.evolutionService = evolutionService;
	}
	
	@RequestMapping(value = "/generateEvolution.htm", method = RequestMethod.GET)
	public void generateEvolution(@RequestParam("contextId") Long contextId, @RequestParam("elementId") Long elementId,
			                              @RequestParam("elementVersionId") Long elementVersionId, @RequestParam("picklistElementId") Long picklistElementId,
			                              @RequestParam("picklistOutputId") Long picklistOutputId, Model model, HttpServletResponse response) {
		RefsetImpl refset = (RefsetImpl)refsetService.getRefset(contextId, elementId, elementVersionId);
		String versionCode = refset.getVersionCode();
		Long baseContextId = refset.getContext().getBaseContextId();
		String baseVersionCode = refsetService.getVersionCode(baseContextId);
		Long cciContextId = refset.getCCIContextId();
		Long icd10caContextId = refset.getICD10CAContextId();
		PicklistColumnEvolutionRequestDTO evolutionRequst = new PicklistColumnEvolutionRequestDTO();
		evolutionRequst.setBaseRefsetContextId(baseContextId);
		evolutionRequst.setCciContextId(cciContextId);
		evolutionRequst.setIcd10caContextId(icd10caContextId);
		evolutionRequst.setPicklistElementId(picklistElementId);
		evolutionRequst.setRefsetContextId(contextId);
		evolutionRequst.setPicklistOutputId(picklistOutputId);
		evolutionRequst.setVersionCode(versionCode);
		evolutionRequst.setBaseVersionCode(baseVersionCode);
		BaseOutputContent evolutionContent = evolutionService.getPicklistColumnEvolutionContent(evolutionRequst);
		try {
			RefsetExportUtils.outputExcel(evolutionContent,response);
        } catch (IOException ex) {
            LOGGER.error("Error writing file to output stream. Filename" + ex);
        }
    }
}
