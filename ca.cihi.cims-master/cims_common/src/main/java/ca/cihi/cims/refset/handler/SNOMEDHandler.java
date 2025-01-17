package ca.cihi.cims.refset.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;
import ca.cihi.cims.refset.mapper.SNOMEDCommonMapper;

@Component
public class SNOMEDHandler {

	@Autowired
	private SNOMEDCommonMapper snomedCommonMapper;

	public List<SCTDescriptionChangeDTO> findChangedSynonym(String fromVersionCode, String toVersionCode,
			Long refsetContextId, String baseClassificationName) {

		return snomedCommonMapper.findChangedSynonym(fromVersionCode, toVersionCode, refsetContextId,
				baseClassificationName);
	}

	public List<Long> findExpiredConcepts(String fromVersionCode, String toVersionCode, Long refsetContextId,
			String baseClassificationName) {

		return snomedCommonMapper.findExpiredConcepts(fromVersionCode, toVersionCode, refsetContextId,
				baseClassificationName);
	}

	public List<SCTDescriptionChangeDTO> findChangedFSNs(String fromVersionCode, String toVersionCode,
			Long refsetContextId, String baseClassificationName) {

		return snomedCommonMapper.findChangedFSNs(fromVersionCode, toVersionCode, refsetContextId,
				baseClassificationName);
	}

	public List<SCTDescriptionChangeDTO> findChangedPreferreds(String fromVersionCode, String toVersionCode,
			Long refsetContextId, String baseClassificationName) {
		return snomedCommonMapper.findChangedPreferreds(fromVersionCode, toVersionCode, refsetContextId,
				baseClassificationName);
	}
}
