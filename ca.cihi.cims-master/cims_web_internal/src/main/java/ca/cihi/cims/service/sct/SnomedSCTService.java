package ca.cihi.cims.service.sct;

import java.util.List;

import ca.cihi.cims.model.sct.SCTVersion;

public interface SnomedSCTService {

	List<SCTVersion> getVersionsByStatus(String statusCode);

	String getVersionDescByCode(String code);

}
