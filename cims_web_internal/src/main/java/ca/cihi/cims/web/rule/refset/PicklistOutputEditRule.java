package ca.cihi.cims.web.rule.refset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.service.refset.EvolutionService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.PickListOutputConfigBean;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.bean.refset.RefsetLightBean;

/**
 * 
 * @author lzhu
 *
 */
@Component
public class PicklistOutputEditRule implements PermissionRule {
	
	private final static String ATTR_PICKLIST_COLUMNS_EVOLUTION_PERMISSION = "picklistColumnsEvolutionPermission";
	
   
    private RefsetService refsetService;
    
    private EvolutionService evolutionService;
    
    public RefsetService getRefsetService() {
		return refsetService;
	}

    @Autowired
	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	public EvolutionService getEvolutionService() {
		return evolutionService;
	}

    @Autowired
	public void setEvolutionService(EvolutionService evolutionService) {
		this.evolutionService = evolutionService;
	}

	@Override
	public void applyRule(HttpServletRequest request, RefsetLightBean refsetLightBean) {
		Long contextId = refsetLightBean.getContextId();
		PickListOutputConfigBean viewBean = (PickListOutputConfigBean)refsetLightBean;		
		if (request.getAttribute(ATTR_REFSET_PERMISSION).equals(REFSET_ACCESS_PERMISSION_WRITE) ||
				request.getAttribute(ATTR_WRITE_FOR_LASTEST_CLOSED_VERSION).equals(REFSET_ACCESS_PERMISSION_Y)){
			if (!refsetService.isInceptionVersion(viewBean.getContextId()) 
					&& (refsetService.isOpenRefsetVersion(contextId) || refsetService.isLatestClosedRefsetVersion(contextId)) 
					&& evolutionService.verifyPicklistOutputConfig(viewBean.getContextId(),viewBean.getElementId(),viewBean.getElementVersionId(),viewBean.getPicklistElementId())) {
				request.setAttribute(ATTR_PICKLIST_COLUMNS_EVOLUTION_PERMISSION, REFSET_ACCESS_PERMISSION_WRITE);
			}else{
				request.setAttribute(ATTR_PICKLIST_COLUMNS_EVOLUTION_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
			}
		}else {
			request.setAttribute(ATTR_PICKLIST_COLUMNS_EVOLUTION_PERMISSION, REFSET_ACCESS_PERMISSION_READ);
		}
	}

}
