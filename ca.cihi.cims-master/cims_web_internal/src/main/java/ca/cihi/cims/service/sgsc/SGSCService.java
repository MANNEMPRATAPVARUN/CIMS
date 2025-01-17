package ca.cihi.cims.service.sgsc;

public interface SGSCService {

	String replaceSystemGeneratedSupplementContent(String htmlString, Long currentContextId, Long priorContextId,
			Boolean folio);
}
