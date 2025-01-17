package ca.cihi.cims.service.refset;


import java.util.List;

import ca.cihi.cims.model.refset.BaseOutputContent;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;


/**
 * 
 * @author lzhu
 *
 */
public interface EvolutionService {
	
	boolean verifyPicklistOutputConfig(Long refsetContextId, Long elementId, Long elementVersionId, Long picklistElementId);
	List<PicklistColumnEvolutionResultDTO> getPicklistColumnEvolutionList(PicklistColumnEvolutionRequestDTO request);
	BaseOutputContent getPicklistColumnEvolutionContent(PicklistColumnEvolutionRequestDTO request);
	List<PicklistColumnConfigEvolutionDTO> getPicklistColumnConfigEvolutionList(PicklistColumnEvolutionRequestDTO request);
}
