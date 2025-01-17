package ca.cihi.cims.data.mapper.sct;

import java.util.List;

import ca.cihi.cims.model.sct.SCTVersion;

public interface SnomedSCTMapper {

	List<SCTVersion> getVersionsByStatus(String statusCode);

	String getVersionDescByCode(String code);
}
