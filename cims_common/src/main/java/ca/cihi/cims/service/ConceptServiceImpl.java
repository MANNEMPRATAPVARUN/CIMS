package ca.cihi.cims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dao.mapper.ConceptMapper;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.AttributeInfo;
import ca.cihi.cims.model.AttributeType;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.Diagram;
import ca.cihi.cims.model.IdCodeDescription;

public class ConceptServiceImpl implements ConceptService {

	private static final String CONTEXT_ID = "contextId";

	private static final String ELEMENT_ID = "elementId";
	private static final Log LOGGER = LogFactory.getLog(ConceptServiceImpl.class);
	// KEYS
	private static final String YES_FLAG = "Y";

	private ConceptMapper conceptMapper;
	@Autowired
	private ContextOperations operations;

	/**
	 * Find the validation rule for Acute Care for the given rubric concept
	 *
	 * @param elementId
	 *            Long the elementId of the given rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return AttributeInfo
	 */
	@Override
	public AttributeInfo findDadDHValidation(final Long elementId, final Long contextId) {

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put(ELEMENT_ID, elementId);

		final StopWatch stopWatch = new StopWatch("call findDadDHValidation");
		stopWatch.start("call sql findDadDHValidation");
		final String attrInfoString = conceptMapper.findDadDHValidation(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);
		return getAttributeInfo(contextId, attrInfoString);
	}

	// -------------------------------------------------------------

	/**
	 * Find the validation rule for Acute Care at child levels.
	 *
	 * @param elementId
	 *            Long the elementId of the given rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return
	 */
	@Override
	public AttributeInfo findDadDHValidationAtChildLevels(final Long elementId, final Long contextId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put(ELEMENT_ID, elementId);

		final StopWatch stopWatch = new StopWatch("call findDadDHValidationAtChildLevels");
		stopWatch.start("call sql findDadDHValidationAtChildLevels");
		final String attrInfoString = conceptMapper.findDadDHValidationAtChildLevels(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);
		return getAttributeInfo(contextId, attrInfoString);
	}

	/**
	 * Get ordered active category-one concepts with asterisk.
	 *
	 * @param chaperElementId
	 *            Long the elementId of the given chapter
	 * @param contextId
	 *            Long the contextId of the given chapter belongs to
	 */
	@Override
	public List<AsteriskBlockInfo> getAsteriskList(final Long chapterElementId, final Long contextId) {

		final StopWatch stopWatch = new StopWatch();

		stopWatch.start("set parameters");
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("chapterElementId", chapterElementId);
		parameters.put(CONTEXT_ID, contextId);
		parameters.put("codeClassId", getICDClassID("TextPropertyVersion", "Code"));
		parameters.put("categoryClassId", getICDClassID("ConceptVersion", "Category"));
		parameters.put("daggerClassId", getICDClassID("ConceptPropertyVersion", "DaggerAsteriskIndicator"));
		parameters.put("domainValueCodeClassId", getICDClassID("TextPropertyVersion", "DomainValueCode"));
		stopWatch.stop();
		stopWatch.start("call sql getAsteriskList");
		final List<AsteriskBlockInfo> asteriskList = conceptMapper.getAsteriskList(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);

		return asteriskList;
	}

	private AttributeInfo getAttributeInfo(final Long contextId, final String attrRefsString) {
		final AttributeInfo attributeInfo = new AttributeInfo();

		if (attrRefsString != null) {

			final String[] attributeRefs = attrRefsString.split(",");

			if (attributeRefs.length > 0) {
				final String statusRefCode = attributeRefs[0].trim();
				attributeInfo.setStatusRef(statusRefCode);
				if (statusRefCode.length() > 0) {
					attributeInfo.setStatusRefMandatory(isRefAttributeMandatory(contextId, statusRefCode));
				}
			}
			if (attributeRefs.length > 1) {
				final String extentRefCode = attributeRefs[1].trim();
				attributeInfo.setExtentRef(extentRefCode);
				if (extentRefCode.length() > 0) {
					attributeInfo.setExtentRefMandatory(isRefAttributeMandatory(contextId, extentRefCode));
				}
			}
			if (attributeRefs.length > 2) {
				final String locationRefCode = attributeRefs[2].trim();
				attributeInfo.setLocationRef(locationRefCode);
				if (locationRefCode.length() > 0) {
					attributeInfo.setLocationRefMandatory(isRefAttributeMandatory(contextId, locationRefCode));
				}
			}
		}

		return attributeInfo;
	}

	public Long getBaseContextId(final long contextId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		return conceptMapper.getBaseContextId(parameters);
	}

	/**
	 * Get ordered active blocks in all block levels.
	 *
	 * @param classificatioon
	 *            String the given classification
	 * @param chaperElementId
	 *            Long the elementId of the given chapter
	 * @param contextId
	 *            Long the contextId of the given chapter belongs to
	 */
	@Override
	public List<AsteriskBlockInfo> getBlockList(final String classification, final Long chapterElementId,
			final Long contextId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("chapterElementId", chapterElementId);
		parameters.put(CONTEXT_ID, contextId);

		final StopWatch stopWatch = new StopWatch("getBlockList");
		stopWatch.start("Set sql parameters");
		if (CIMSConstants.ICD_10_CA.equalsIgnoreCase(classification)) {
			parameters.put("narrowerClassId", getICDClassID("ConceptPropertyVersion", "Narrower"));
			parameters.put("codeClassId", getICDClassID("TextPropertyVersion", "Code"));
			parameters.put("blockClassId", getICDClassID("ConceptVersion", "Block"));
			parameters.put("chapterClassId", getICDClassID("ConceptVersion", "Chapter"));
		} else {
			parameters.put("narrowerClassId", getCCIClassID("ConceptPropertyVersion", "Narrower"));
			parameters.put("codeClassId", getCCIClassID("TextPropertyVersion", "Code"));
			parameters.put("blockClassId", getCCIClassID("ConceptVersion", "Block"));
			parameters.put("chapterClassId", getCCIClassID("ConceptVersion", "Section"));
		}
		stopWatch.stop();

		stopWatch.start("call sql getBlockList");
		final List<AsteriskBlockInfo> blockList = conceptMapper.getBlockList(parameters);
		stopWatch.stop();

		LOGGER.info(stopWatch);

		return blockList;
	}

	@Override
	public Long getCCIClassID(final String tablename, final String classname) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tablename", tablename);
		parameters.put("classname", classname);
		return conceptMapper.getCCIClassID(parameters);
	}

	@Override
	public List<IdCodeDescription> getCciComponentsPerSection(final long sectionId, final long contextId,
			final Language language, final CciComponentType type) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		Long baseContextId = getBaseContextId(contextId);
		if (baseContextId == null) {
			baseContextId = contextId;
		}
		parameters.put("sectionId", sectionId);
		parameters.put(CONTEXT_ID, baseContextId);
		parameters.put("language", language.getCode());
		parameters.put("cpvClassName", type.name() + "ToSectionCPV");
		parameters.put("componentClassName", type.name());
		return conceptMapper.getCCIComponentsPerSection(parameters);
	}

	@Override
	public List<IdCodeDescription> getCciComponentsPerSectionLongTitle(final long sectionId, final long contextId,
			final Language language, final CciComponentType type, String orderBy, String firstLetter) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		Long baseContextId = getBaseContextId(contextId);
		if (baseContextId == null) {
			baseContextId = contextId;
		}
		parameters.put("sectionId", sectionId);
		parameters.put(CONTEXT_ID, baseContextId);
		parameters.put("language", language.getCode());
		parameters.put("cpvClassName", type.name() + "ToSectionCPV");
		parameters.put("componentClassName", type.name());
		parameters.put("orderBy", orderBy);
		parameters.put("groupCode", firstLetter);
		return conceptMapper.getCCIComponentsPerSectionLongTitle(parameters);
	}

	@Override
	public long getCCISectionIdBySectionCode(String sectionCode, long contextId) {
		return conceptMapper.getCCISectionIdBySectionCode(sectionCode, contextId);
	}

	@Override
	public Long getClassId(final String classification, final String tableName, final String className) {
		Long classId;
		if (CIMSConstants.CCI.equalsIgnoreCase(classification)) {
			classId = getCCIClassID(tableName, className);
		} else {
			classId = getICDClassID(tableName, className);
		}

		return classId;
	}

	@Override
	public Long getContextId(String classification, String versionCode) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("versionCode", versionCode);
		return conceptMapper.getContextId(parameters);
	}

	@Override
	public byte[] getDiagram(String diagramFileName, Long currentContextId) {
		Diagram diagram = conceptMapper.getDiagramByFileName(removeDigitSuffix(diagramFileName), currentContextId);

		byte[] diagramBytes;
		if (diagram == null) {
			diagramBytes = null;
		} else {
			diagramBytes = diagram.getDiagramBytes();
		}

		return diagramBytes;
	}

	@Override
	public List<Diagram> getDiagrams(Long currentContextId) {
		List<Diagram> diagrams = conceptMapper.getDiagramByContextId(currentContextId);
		return diagrams;
	}
	  

	@Override
	public Long getICDClassID(final String tablename, final String classname) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tablename", tablename);
		parameters.put("classname", classname);
		return conceptMapper.getICDClassID(parameters);
	}

	@Override
	public List<IdCodeDescription> getRefAttributePerType(final long contextId, final Language language,
			final AttributeType type) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		Long baseContextId = getBaseContextId(contextId);
		if (baseContextId == null) {
			baseContextId = contextId;
		}
		parameters.put(CONTEXT_ID, baseContextId);
		parameters.put("language", language.getCode());
		parameters.put("attributeType", type == null ? null : type.name());
		return conceptMapper.getRefAttributePerType(parameters);

	}

	/**
	 * Check if the given concept one or rubric concept has validation rules.
	 *
	 * @param elementId
	 *            Long the elementId of the given category one or rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return boolean
	 */
	@Override
	public boolean hasActiveValidationRule(final Long elementId, final Long contextId) {

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put(ELEMENT_ID, elementId);

		final StopWatch stopWatch = new StopWatch("call hasActiveValidationRule");
		stopWatch.start("call sql hasActiveValidationRule");
		final String hasActiveValidationRule = conceptMapper.hasActiveValidationRule(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);

		return YES_FLAG.equalsIgnoreCase(hasActiveValidationRule);
	}

	/**
	 * Check if any child of the given category one or rubric concept has active validation rule
	 *
	 * @param elementId
	 *            Long the elementId of the given category one or rubric concept
	 * @param contextId
	 *            Long the given context id
	 * @return boolean
	 */
	@Override
	public boolean hasChildWithActiveValidationRule(final Long elementId, final Long contextId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put(ELEMENT_ID, elementId);

		final StopWatch stopWatch = new StopWatch("call hasChildWithActiveValidationRule");
		stopWatch.start("call sql hasChildWithActiveValidationRule");
		final String hasChildWithActiveValidationRule = conceptMapper.hasChildWithActiveValidationRule(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);

		return YES_FLAG.equalsIgnoreCase(hasChildWithActiveValidationRule);
	}

	/**
	 * Check if the given reference attribute is mandatory in the given context
	 *
	 * @param contextId
	 *            Long the context id
	 * @param refAttributeCode
	 *            String the code of the given reference attribute
	 * @return
	 */
	@Override
	public boolean isRefAttributeMandatory(final Long contextId, final String refAttributeCode) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put("refAttributeCode", refAttributeCode);

		return YES_FLAG.equalsIgnoreCase(conceptMapper.isRefAttributeMandatory(parameters));
	}

	/**
	 * Check if the given concept is a leaf node.
	 *
	 * @param elementId
	 *            Long the elementId of the given concept
	 * @param contextId
	 *            Long the context id
	 * @return boolean
	 */
	@Override
	public boolean isValidCode(final Long elementId, final Long contextId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(CONTEXT_ID, contextId);
		parameters.put(ELEMENT_ID, elementId);

		final StopWatch stopWatch = new StopWatch("call hasActiveChildren");
		stopWatch.start("call sql hasActiveChildren");
		final String hasActiveChildren = conceptMapper.hasActiveChildren(parameters);
		stopWatch.stop();
		LOGGER.info(stopWatch);

		return "N".equalsIgnoreCase(hasActiveChildren);
	}

	@Override
	public boolean reBaseChangedFromVersionId(final long elementId, final long contextId, final long classId,
			final String languageCode) {
		return operations.reBaseChangedFromVersionId(elementId, contextId, classId, languageCode);
	}

	private String removeDigitSuffix(String diagramFileName) {
		String fileName = diagramFileName;

		// remove the extra suffix:
		// i.e., E_fig3cci.gif3 --> E_fig3cci.gif
		// E_fig2cci.gif6b --> E_fig2cci.gif
		for (int index = diagramFileName.lastIndexOf("."); index < diagramFileName.length(); index++) {
			if (Character.isDigit(diagramFileName.charAt(index))) {
				fileName = diagramFileName.substring(0, index);
				break;
			}
		}

		return fileName;
	}

	@Override
	public Long retrieveContainingPagebyId(Map<String, Object> map) {
		return conceptMapper.retrieveContainingPagebyId(map);
	}

	@Override
	public Long retrievePagebyIdForFolio(Map<String, Object> params) {
		return conceptMapper.retrievePagebyIdForFolio(params);
	}

    @Override
    public Long retrievePagebyIdForClaml(Map<String, Object> params) {
        return conceptMapper.retrievePagebyIdForClaml(params);
    }
	
	public void setConceptMapper(final ConceptMapper conceptMapper) {
		this.conceptMapper = conceptMapper;
	}
}