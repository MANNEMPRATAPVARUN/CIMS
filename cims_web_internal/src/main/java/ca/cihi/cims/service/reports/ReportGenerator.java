package ca.cihi.cims.service.reports;

import static ca.cihi.cims.WebConstants.MARK;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public abstract class ReportGenerator {

	private LookupService lookupService;
	private ReportMapper reportMapper;
	private ConceptService conceptService;
	protected static final String GENDER = "gender";
	protected static final String MAXAGE = "maxAge";
	protected static final String MINAGE = "minAge";

	public static String getDateStr(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	protected String appendMark(int value) {
		return value + MARK;
	}

	protected String appendMark(String value) {
		return value + MARK;
	}

	public abstract Map<String, Object> generatReportData(ReportViewBean reportViewBean);

	public ConceptService getConceptService() {
		return conceptService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	protected List<ContextIdentifier> getOpenContextIdentifiers(String classification, String isVersionYear) {
		return lookupService.findNonClosedBaseContextIdentifiersReport(classification, isVersionYear);
	}

	public ReportMapper getReportMapper() {
		return reportMapper;
	}

	protected void markData(Map<String, Object> firstMap, Map<String, Object> secondMap, int firstNumber,
			int secondNumber, String key) {
		if (firstNumber != secondNumber) {
			firstMap.put(key, firstNumber + MARK);
			secondMap.put(key, secondNumber + MARK);
		} else {
			firstMap.put(key, firstNumber + "");
			secondMap.put(key, secondNumber + "");
		}
	}

	protected void markData(Map<String, Object> firstMap, Map<String, Object> secondMap, String firstString,
			String secondString, String key) {
		if (!firstString.equals(secondString)) {
			firstMap.put(key, appendMark(firstString));
			secondMap.put(key, appendMark(secondString));
		} else {
			firstMap.put(key, firstString);
			secondMap.put(key, secondString);
		}
	}

	protected String paddingChangeRequestIDs(List<Long> changeRequestIds) {
		StringBuilder sb = new StringBuilder();
		int j = 0;
		for (Long requestId : changeRequestIds) {
			if (j++ > 0) {
				sb.append("\n");
			}
			sb.append(requestId);
		}
		return sb.toString();
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setReportMapper(ReportMapper reportMapper) {
		this.reportMapper = reportMapper;
	}
}
