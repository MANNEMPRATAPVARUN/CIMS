package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import ca.cihi.cims.data.mapper.IncompleteReportMapper;
import ca.cihi.cims.model.changerequest.IncompleteProperty;
import ca.cihi.cims.util.PropertyManager;

public class IncompleteReportServiceImpl implements IncompleteReportService {

	private IncompleteReportMapper incompleteReportMapper;

	private PropertyManager propertyManager;

	@Override
	public List<IncompleteProperty> checkIndexConcept(final Long contextId, final Long conceptId, final String indexDesc) {

		String incompleteString = incompleteReportMapper.checkIndexConcept(contextId, conceptId);

		List<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		if (!StringUtils.isEmpty(incompleteString)) {
			incompleteProperties = processIncompleteString(incompleteString, indexDesc);
		}
		return incompleteProperties;
	}

	@Override
	public List<IncompleteProperty> checkSupplementConcept(final Long contextId, final Long conceptId,
			final String supplementDesc) {

		String incompleteString = incompleteReportMapper.checkSupplementConcept(contextId, conceptId);

		List<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		if (!StringUtils.isEmpty(incompleteString)) {
			incompleteProperties = processIncompleteString(incompleteString, supplementDesc);
		}
		return incompleteProperties;
	}

	@Override
	public List<IncompleteProperty> checkTabularConcept(final Long contextId, final Long conceptId,
			final boolean isVersionYear, final String conceptCode) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("conceptId", conceptId);
		parameters.put("isVersionYear", isVersionYear ? 'Y' : 'N');

		String incompleteString = incompleteReportMapper.checkTabularConcept(parameters);

		List<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		if (!StringUtils.isEmpty(incompleteString)) {
			incompleteProperties = processIncompleteString(incompleteString, conceptCode);
		}
		return incompleteProperties;
	}

	public IncompleteReportMapper getIncompleteReportMapper() {
		return incompleteReportMapper;
	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	private ArrayList<IncompleteProperty> processIncompleteString(final String incompleteString,
			final String conceptCode) {
		ArrayList<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();
		if (!StringUtils.isEmpty(incompleteString)) {
			String[] rules = incompleteString.split(",");

			for (int i = 0; i < rules.length; i++) {
				String rule = rules[i].trim();
				if (rule.length() > 0) {
					IncompleteProperty incompleteProperty = new IncompleteProperty();
					incompleteProperty.setCodeValue(conceptCode);
					incompleteProperty.setIncompleteRatoinale(propertyManager.getMessage(rule));

					incompleteProperties.add(incompleteProperty);
				}
			}
		}

		return incompleteProperties;
	}

	public void setIncompleteReportMapper(IncompleteReportMapper incompleteReportMapper) {
		this.incompleteReportMapper = incompleteReportMapper;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

}
