package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.AgentGroupDTO;
import ca.cihi.cims.model.sgsc.CCIRubric;
import ca.cihi.cims.model.sgsc.CodeDescription;

public interface SGSCMapper {

	List<AgentGroupDTO> findAgentATCCodes(Map<String, Object> params);

	List<CodeDescription> findCCIDisabledCodes(Map<String, Object> paramMap);

	List<CCIReferenceAttribute> findCCIDisabledMandatoryReferenceCodes(Map<String, Object> params);

	List<CodeDescription> findCCINewCodes(Map<String, Object> paramMap);

	List<CCIReferenceAttribute> findCCINewMandatoryReferenceCodes(Map<String, Object> params);

	List<CCIRubric> findCCIRubric(Map<String, Object> paramMap);

	List<CodeDescription> findICDDisabledCodes(Map<String, Object> paramMap);

	List<CodeDescription> findICDNewCodes(Map<String, Object> paramMap);
}
