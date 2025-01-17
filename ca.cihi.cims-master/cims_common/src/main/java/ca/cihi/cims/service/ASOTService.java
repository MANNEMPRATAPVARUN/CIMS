package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.dao.bean.AsotETLLog;

public interface ASOTService {

	List<String> findVersionYears();

	void generateASOT(String fiscalYear, Long releaseId, String email);

	void generateASOT(String fiscalYear, String email);

	AsotETLLog getLatestETLLog(String fiscalYear);
}
