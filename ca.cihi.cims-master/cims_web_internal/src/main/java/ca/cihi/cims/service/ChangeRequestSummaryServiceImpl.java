package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.content.icd.IcdValidationXml;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ChangeRequestIndexSummaryMapper;
import ca.cihi.cims.data.mapper.ChangeRequestSummaryMapper;
import ca.cihi.cims.data.mapper.ChangeRequestSupplementSummaryMapper;
import ca.cihi.cims.data.mapper.IncompleteReportMapper;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ConflictProposedChange;
import ca.cihi.cims.model.changerequest.ConflictProposedIndexChange;
import ca.cihi.cims.model.changerequest.ConflictProposedSupplementChange;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.model.changerequest.ValidationChange;
import ca.cihi.cims.util.XmlUtils;
import ca.cihi.cims.web.filter.CurrentContext;

public class ChangeRequestSummaryServiceImpl implements ChangeRequestSummaryService {

	private static final String CHANGE_OLD = "Old";
	private static final String CHANGE_PROPOSED = "Proposed";

	private static final String CHANGE_CONFLICT = "Conflict";
	private static final String CHANGE_NEW = "New";
	private static final String VALIDATION_DEFINITION = "ValidationDefinition";
	private static final String NO_VALUE = "no_value";
	private static final String NO_CONFLICT = "no_conflict";

	@Autowired
	private CurrentContext context;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private ClassificationService classificationService;
	private ChangeRequestSummaryMapper changeRequestSummaryMapper;
	private ChangeRequestIndexSummaryMapper changeRequestIndexSummaryMapper;
	private ChangeRequestSupplementSummaryMapper changeRequestSupplementSummaryMapper;
	private IncompleteReportMapper incompleteReportMapper;
	private ConceptService conceptService;

	private void addProposedChange(final List<ProposedChange> proposedChanges, final ProposedChange newProposedChange) {
		final ProposedChange existingProposedChange = proposedChanges.get(proposedChanges.size() - 1);
		if (newProposedChange.getFieldName().equals(existingProposedChange.getFieldName())
				&& newProposedChange.getTableName().equals(existingProposedChange.getTableName())) {
			existingProposedChange.setProposedValue(newProposedChange.getProposedValue());
			existingProposedChange.setConflictValue(newProposedChange.getConflictValue());
		} else {
			proposedChanges.add(newProposedChange);
		}
	}

	private void addProposedValidationChange(final List<ProposedChange> proposedChanges,
			final ProposedChange newProposedChange) {
		final ProposedChange existingProposedChange = proposedChanges.get(proposedChanges.size() - 1);
		if (newProposedChange.getFieldName().equals(existingProposedChange.getFieldName())) {
			existingProposedChange.setProposedValue(newProposedChange.getProposedValue());
			existingProposedChange.setTableName(newProposedChange.getTableName());
			existingProposedChange.setConflictValue(newProposedChange.getConflictValue());
		} else {
			proposedChanges.add(newProposedChange);
		}
	}

	// @Autowired
	// private SynchronizationService synchronizationService;

	private void addRealizedChange(final List<RealizedChange> realizedChanges, final RealizedChange newRealizedChange) {
		final RealizedChange existingRealizedChange = realizedChanges.get(realizedChanges.size() - 1);
		if (newRealizedChange.getFieldName().equals(existingRealizedChange.getFieldName())
				&& newRealizedChange.getTableName().equals(existingRealizedChange.getTableName())) {
			existingRealizedChange.setNewValue(newRealizedChange.getNewValue());
		} else {
			realizedChanges.add(newRealizedChange);
		}
	}

	@Transactional
	@Override
	public String findHtmlTextFromHtmlPropertyId(final Long htmlPropertyId) {
		return changeRequestSummaryMapper.findHtmlTextFromHtmlPropertyId(htmlPropertyId);
	}

	@Transactional
	@Override
	public String findIndexDesc(final Long maxStructureId, final Long indexRefId) {
		return changeRequestIndexSummaryMapper.findIndexDesc(maxStructureId, indexRefId);
	}

	@Transactional
	@Override
	public Long findMaxStructureId(final Long changeRequestId) {
		return changeRequestSummaryMapper.findMaxStructureId(changeRequestId);
	}

	@Transactional
	@Override
	public ConceptModification findModifiedConceptElementCode(final Long changeRequestId, final Long maxStructureId,
			final Long validationId) {
		ConceptModification modifiedConcept = null;
		final List<ConceptModification> conceptModifications = findModifiedConceptElementCodes(changeRequestId,
				maxStructureId);
		for (ConceptModification conceptModification : conceptModifications) {
			if (conceptModification.getValidationId().longValue() == validationId.longValue()) {
				modifiedConcept = conceptModification;
				break;
			}
		}
		return modifiedConcept;
	}

	@Transactional
	@Override
	public List<ConceptModification> findModifiedConceptElementCodes(final Long changeRequestId,
			final Long maxStructureId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("maxStructureId", maxStructureId);
		parameters.put("ICDValidationDefinition",
				conceptService.getICDClassID("XMLPropertyVersion", "ValidationDefinition"));
		parameters.put("CCIValidationDefinition",
				conceptService.getCCIClassID("XMLPropertyVersion", "ValidationDefinition"));
		return changeRequestSummaryMapper.findModifiedConceptElementCodes(parameters);
	}

	@Transactional
	@Override
	public ConceptModification findModifiedIndexConceptElementCode(final Long changeRequestId,
			final Long maxStructureId, final Long elementId) {
		ConceptModification modifiedConcept = null;
		final List<ConceptModification> conceptModifications = findModifiedIndexConceptElementCodes(changeRequestId,
				maxStructureId);
		for (ConceptModification conceptModification : conceptModifications) {
			if (conceptModification.getElementId().longValue() == elementId.longValue()) {
				modifiedConcept = conceptModification;
				break;
			}
		}
		return modifiedConcept;
	}

	@Transactional
	@Override
	public List<ConceptModification> findModifiedIndexConceptElementCodes(final Long changeRequestId,
			final Long maxStructureId) {
		return changeRequestIndexSummaryMapper.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId);
	}

	@Transactional
	@Override
	public List<ConceptModification> findModifiedSupplementConceptElementCodes(final Long changeRequestId,
			final Long maxStructureId, String language) {
		return changeRequestSupplementSummaryMapper.findModifiedSupplementConceptElementCodes(changeRequestId,
				maxStructureId, language);
	}

	@Override
	public List<ProposedChange> findProposedAndConflictIndexChanges(final Long contextId, final Long domainElementId) {
		final List<ProposedChange> proposedChanges = findProposedIndexChanges(contextId, domainElementId);
		for (ProposedChange proposedChange : proposedChanges) {
			if (StringUtils.isNotBlank(proposedChange.getConflictValue())) { // has conflict
				final ContextIdentifier realizedBy = changeRequestSummaryMapper.findConflictRealizedByContext(
						proposedChange.getElementVersionId(), contextId);
				proposedChange.setConflictRealizedByContext(realizedBy);
			}
		}
		return proposedChanges;
	}

	@Override
	public List<ProposedChange> findProposedAndConflictSupplementChanges(final Long contextId,
			final Long domainElementId, String language) {
		final List<ProposedChange> proposedChanges = findProposedSupplementChanges(contextId, domainElementId, language);
		for (ProposedChange proposedChange : proposedChanges) {
			if (StringUtils.isNotBlank(proposedChange.getConflictValue())) { // has conflict
				final ContextIdentifier realizedBy = changeRequestSummaryMapper.findConflictRealizedByContext(
						proposedChange.getElementVersionId(), contextId);
				proposedChange.setConflictRealizedByContext(realizedBy);
			}
		}
		return proposedChanges;
	}

	@Override
	public List<ProposedChange> findProposedAndConflictTabularChanges(final Long contextId, final Long domainElementId) {
		final List<ProposedChange> proposedChanges = findProposedTabularChanges(contextId, domainElementId);
		for (ProposedChange proposedChange : proposedChanges) {
			if (StringUtils.isNotBlank(proposedChange.getConflictValue())) { // has conflict
				// Long
				// realizedByCR=changeRequestSummaryMapper.findConflictRealizedByChangeRequestId(proposedChange.getElementVersionId(),
				// contextId);
				// proposedChange.setConflictValueRealizedByCR(realizedByCR);
				final ContextIdentifier realizedBy = changeRequestSummaryMapper.findConflictRealizedByContext(
						proposedChange.getElementVersionId(), contextId);
				proposedChange.setConflictRealizedByContext(realizedBy);
			}
		}
		return proposedChanges;
	}

	@Override
	public List<ProposedChange> findProposedAndConflictValidationChanges(final String classification,
			final Long contextId, final Long validationId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("validationId", validationId);
		parameters.put("validationCCIcid", conceptService.getCCIClassID("ConceptVersion", "ValidationCCI"));
		parameters.put("validationICDcid", conceptService.getICDClassID("ConceptVersion", "ValidationICD"));
		final List<ProposedChange> proposedChanges = processProposedValidationChange(
				changeRequestSummaryMapper.findProposedValidationChanges(parameters), classification);
		// temporary to get realized by change request Id there
		for (ProposedChange proposedChange : proposedChanges) {
			if (StringUtils.isNotBlank(proposedChange.getConflictValue())) { // has conflict
				// Long
				// realizedByCR=changeRequestSummaryMapper.findConflictRealizedByChangeRequestId(proposedChange.getElementVersionId(),
				// contextId);
				// proposedChange.setConflictValueRealizedByCR(realizedByCR);
				final ContextIdentifier realizedBy = changeRequestSummaryMapper.findConflictRealizedByContext(
						proposedChange.getElementVersionId(), contextId);
				proposedChange.setConflictRealizedByContext(realizedBy);
				proposedChange.setValidationId(validationId);

			}
		}
		return proposedChanges;
	}

	@Transactional
	@Override
	public List<ProposedChange> findProposedIndexChanges(final long contextId, final Long domainElementId) {
		final List<ProposedChange> rawProposedChanges = changeRequestIndexSummaryMapper.findProposedIndexChanges(
				contextId, domainElementId);
		final List<ProposedChange> proposedChanges = processProposedChange(rawProposedChanges);

		return proposedChanges;
	}

	@Transactional
	@Override
	public String findProposedStatus(final Long contextId, final Long domainElementId) {
		return changeRequestSummaryMapper.findProposedStatus(contextId, domainElementId);
	}

	@Transactional
	@Override
	public List<ProposedChange> findProposedStatusChanges(final Long contextId, final Long domainElementId) {
		final List<ProposedChange> rawProposedChanges = changeRequestSummaryMapper.findProposedStatusChanges(contextId,
				domainElementId);
		return processProposedChange(rawProposedChanges);
	}

	@Override
	public List<ProposedChange> findProposedSupplementChanges(final Long contextId, final Long domainElementId,
			String language) {
		final List<ProposedChange> rawProposedChanges = changeRequestSupplementSummaryMapper
				.findProposedSupplementChanges(contextId, domainElementId, language);
		final List<ProposedChange> proposedChanges = processProposedChange(rawProposedChanges);

		return proposedChanges;
	}

	@Transactional
	@Override
	public List<ProposedChange> findProposedTabularChanges(final Long contextId, final Long domainElementId) {
		final List<ProposedChange> rawProposedChanges = changeRequestSummaryMapper.findProposedTabularChanges(
				contextId, domainElementId);
		final List<ProposedChange> proposedChanges = processProposedChange(rawProposedChanges);

		return proposedChanges;
	}

	@Transactional
	@Override
	public List<ValidationChange> findProposedValidationChanges(final String classification, final Long contextId,
			final Long validationId, final boolean showOldValue) {
		final List<ValidationChange> validationChanges = new ArrayList<ValidationChange>();
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("validationId", validationId);
		parameters.put("validationCCIcid", conceptService.getCCIClassID("ConceptVersion", "ValidationCCI"));
		parameters.put("validationICDcid", conceptService.getICDClassID("ConceptVersion", "ValidationICD"));

		final List<ProposedChange> proposedChanges = processProposedValidationChange(
				changeRequestSummaryMapper.findProposedValidationChanges(parameters), classification);
		for (ProposedChange proposedChange : proposedChanges) {
			final String dataHolding = proposedChange.getFieldName();
			final String oldValue = proposedChange.getOldValue();
			final String proposedValue = proposedChange.getProposedValue();
			final String conflictValue = proposedChange.getConflictValue();

			if (VALIDATION_DEFINITION.equals(proposedChange.getTableName())) {
				if (isValidationChanged(oldValue, proposedValue, classification, proposedChange.getTableName())) {
					if (showOldValue) {
						setValidationChange(classification, dataHolding, validationChanges, oldValue, CHANGE_OLD);
					}
					setValidationChange(classification, dataHolding, validationChanges, proposedValue, CHANGE_PROPOSED);
					setValidationChange(classification, dataHolding, validationChanges, conflictValue, CHANGE_CONFLICT);
				}
			} else {
				if (showOldValue) {
					setValidationChange(classification, dataHolding, validationChanges, oldValue, CHANGE_OLD);
				}
				setValidationStatusChange(dataHolding, validationChanges, proposedValue, CHANGE_PROPOSED);
			}
		}
		return validationChanges;
	}

	@Transactional
	@Override
	public List<RealizedChange> findRealizedIndexChanges(final Long changeRequestId, final Long domainElementId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		final List<RealizedChange> rawRealizedChanges = changeRequestIndexSummaryMapper
				.findRealizedIndexChanges(parameters);
		return processRealizedChange(rawRealizedChanges);
	}

	@Transactional
	@Override
	public String findRealizedStatus(final Long changeRequestId, final Long domainElementId) {

		return changeRequestSummaryMapper.findRealizedStatus(changeRequestId, domainElementId);
	}

	@Transactional
	@Override
	public List<RealizedChange> findRealizedStatusChanges(final Long changeRequestId, final Long domainElementId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		final List<RealizedChange> rawRealizedChanges = changeRequestSummaryMapper
				.findRealizedStatusChanges(parameters);
		return processRealizedChange(rawRealizedChanges);
	}

	@Transactional
	@Override
	public List<RealizedChange> findRealizedSupplementChanges(final Long changeRequestId, final Long domainElementId,
			String language) {

		final List<RealizedChange> rawRealizedChanges = changeRequestSupplementSummaryMapper
				.findRealizedSupplementChanges(changeRequestId, domainElementId, language);

		return processRealizedChange(rawRealizedChanges);
	}

	@Transactional
	@Override
	public List<RealizedChange> findRealizedTabularChanges(final Long changeRequestId, final Long domainElementId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("domainElementId", domainElementId);
		final List<RealizedChange> rawRealizedChanges = changeRequestSummaryMapper
				.findRealizedTabularChanges(parameters);

		return processRealizedChange(rawRealizedChanges);
	}

	@Transactional
	@Override
	public List<RealizedChange> findRealizedValidationChanges(final String classification, final Long structureId,
			final Long validationId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("structureId", structureId);
		parameters.put("validationId", validationId);
		parameters.put("validationCCIcid", conceptService.getCCIClassID("ConceptVersion", "ValidationCCI"));
		parameters.put("validationICDcid", conceptService.getICDClassID("ConceptVersion", "ValidationICD"));
		final List<RealizedChange> realizedChanges = changeRequestSummaryMapper
				.findRealizedValidationChanges(parameters);
		return realizedChanges;
	}

	@Transactional
	@Override
	public String findXmlTextFromXmlPropertyId(final Long xmlPropertyId) {
		return changeRequestSummaryMapper.findXmlTextFromXmlPropertyId(xmlPropertyId);
	}

	public ChangeRequestIndexSummaryMapper getChangeRequestIndexSummaryMapper() {
		return changeRequestIndexSummaryMapper;
	}

	public ChangeRequestSummaryMapper getChangeRequestSummaryMapper() {
		return changeRequestSummaryMapper;
	}

	public ChangeRequestSupplementSummaryMapper getChangeRequestSupplementSummaryMapper() {
		return changeRequestSupplementSummaryMapper;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public IncompleteReportMapper getIncompleteReportMapper() {
		return incompleteReportMapper;
	}

	@Transactional
	private boolean hasIncompleteIndex(final ContextIdentifier changeContext, ConceptModification conceptModification) {
		boolean hasIncompleteIndex = false;
		Long conceptId = conceptModification.getElementId();

		String incompleteString = incompleteReportMapper.checkIndexConcept(changeContext.getContextId(), conceptId);
		if (!StringUtils.isEmpty(incompleteString)) {
			hasIncompleteIndex = true;
		}
		return hasIncompleteIndex;
	}

	@Override
	public boolean hasIncompleteProperties(final ChangeRequest changeRequest) {
		boolean hasIncompletes = false;
		Long changeRequestId = changeRequest.getChangeRequestId();
		final ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		final Long maxStructureId = findMaxStructureId(changeRequestId);
		ChangeRequestCategory changeRequestCategory = changeRequest.getCategory();

		if (ChangeRequestCategory.T == changeRequestCategory) {
			// Get modified concept list
			final List<ConceptModification> rawConceptModifications = findModifiedConceptElementCodes(changeRequestId,
					maxStructureId);
			for (ConceptModification conceptModification : rawConceptModifications) {
				hasIncompletes = hasIncompleteTabular(changeContext, conceptModification);
				if (hasIncompletes) {
					break;
				}
			}
		} else if (ChangeRequestCategory.I == changeRequestCategory) {
			List<ConceptModification> rawConceptModifications = findModifiedIndexConceptElementCodes(changeRequestId,
					maxStructureId);
			for (ConceptModification conceptModification : rawConceptModifications) {
				hasIncompletes = hasIncompleteIndex(changeContext, conceptModification);
				if (hasIncompletes) {
					break;
				}
			}

		} else if (ChangeRequestCategory.S == changeRequestCategory) {
			List<ConceptModification> rawConceptModifications = findModifiedSupplementConceptElementCodes(
					changeRequestId, maxStructureId, changeRequest.getLanguageCode());
			for (ConceptModification conceptModification : rawConceptModifications) {
				hasIncompletes = hasIncompleteSupplement(changeContext, conceptModification);
				if (hasIncompletes) {
					break;
				}
			}

		}
		return hasIncompletes;
	}

	@Transactional
	private boolean hasIncompleteSupplement(final ContextIdentifier changeContext,
			ConceptModification conceptModification) {
		boolean hasIncompleteSupplement = false;
		Long conceptId = conceptModification.getElementId();

		String incompleteString = incompleteReportMapper
				.checkSupplementConcept(changeContext.getContextId(), conceptId);
		if (!StringUtils.isEmpty(incompleteString)) {
			hasIncompleteSupplement = true;
		}
		return hasIncompleteSupplement;
	}

	@Transactional
	private boolean hasIncompleteTabular(final ContextIdentifier changeContext, ConceptModification conceptModification) {

		boolean hasIncompleteTabular = false;
		final Long conceptId = conceptModification.getElementId();
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", changeContext.getContextId());
		parameters.put("conceptId", conceptId);
		parameters.put("isVersionYear", changeContext.getIsVersionYear() ? 'Y' : 'N');
		final String incompleteString = incompleteReportMapper.checkTabularConcept(parameters);
		if (!StringUtils.isEmpty(incompleteString)) {
			hasIncompleteTabular = true;
		}
		return hasIncompleteTabular;
	}

	private boolean isChanged(String oldValue, String proposedValue, String tableName) {
		boolean isPresented = false;

		// Get the old xml/html data with the given property id
		if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(oldValue) && StringUtils.isNumeric(oldValue)) {
			if ("XMLPropertyVersion".equals(tableName)) {
				oldValue = changeRequestSummaryMapper.findXmlTextFromXmlPropertyId(Long.valueOf(oldValue));
			} else if ("HTMLPropertyVersion".equals(tableName)) {
				oldValue = changeRequestSummaryMapper.findHtmlTextFromHtmlPropertyId(Long.valueOf(oldValue));
			}
		}

		// Get the proposed xml/html data with the given property id
		if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(proposedValue)
				&& StringUtils.isNumeric(proposedValue)) {
			if ("XMLPropertyVersion".equals(tableName)) {
				proposedValue = changeRequestSummaryMapper.findXmlTextFromXmlPropertyId(Long.valueOf(proposedValue));
			} else if ("HTMLPropertyVersion".equals(tableName)) {
				proposedValue = changeRequestSummaryMapper.findHtmlTextFromHtmlPropertyId(Long.valueOf(proposedValue));
			}
		}

		if (StringUtils.isEmpty(oldValue) && !StringUtils.isEmpty(proposedValue) && !proposedValue.equals("&nbsp;")
				& !ConceptStatus.REMOVED.toString().equals(proposedValue)) {
			// remove the generated empty <td> for Drugs and Neoplams
			String shortProposedValue = proposedValue.replace(
					"<td xmlns:ora=\"http://www.oracle.com/XSL/Transform/java\"></td>", "").trim();
			if (!StringUtils.isEmpty(shortProposedValue)) {
				isPresented = true;
			}
		} else if (!StringUtils.isEmpty(oldValue) && StringUtils.isEmpty(proposedValue)) {
			isPresented = true;
		} else if (!StringUtils.isEmpty(oldValue) && !StringUtils.isEmpty(proposedValue)
				&& !oldValue.trim().equals(proposedValue.trim())) {
			isPresented = true;
		} else {
			isPresented = false;
		}

		return isPresented;
	}

	/**
	 * Check if the validation definition gets changed.
	 * 
	 * @param oldValue
	 *            String the old validation definition
	 * @param proposedValue
	 *            String the proposed validation definition
	 * @param classification
	 *            String the given classification
	 * @param validationType
	 *            String the given validation type: either validation_definition or validation_status
	 * @return boolean
	 */
	private boolean isValidationChanged(final String oldValue, final String proposedValue, final String classification,
			final String validationType) {
		boolean isChanged = false;

		if (oldValue == null || oldValue.length() == 0) {
			if (VALIDATION_DEFINITION.equalsIgnoreCase(validationType)) {
				isChanged = true;
			} else {
				isChanged = false;
			}
		} else {

			if (VALIDATION_DEFINITION.equalsIgnoreCase(validationType)) {

				if (CIMSConstants.CCI.equalsIgnoreCase(classification)) {
					final CciValidationXml oldValidationXml = XmlUtils.deserialize(CciValidationXml.class, oldValue);
					final CciValidationXml proposedValidationXml = XmlUtils.deserialize(CciValidationXml.class,
							proposedValue);
					isChanged = !oldValidationXml.equals(proposedValidationXml);
				} else {
					final IcdValidationXml oldValidationXml = XmlUtils.deserialize(IcdValidationXml.class, oldValue);
					final IcdValidationXml proposedValidationXml = XmlUtils.deserialize(IcdValidationXml.class,
							proposedValue);
					isChanged = !oldValidationXml.equals(proposedValidationXml);
				}
			} else {
				isChanged = true;
			}
		}

		return isChanged;
	}

	protected List<ProposedChange> processProposedChange(final List<ProposedChange> rawProposedChanges) {
		// Combine multiple records to one record for each field and present the oldest value and latest proposed value
		final List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		for (int i = 0; i < rawProposedChanges.size(); i++) {
			if (i == 0) {
				proposedChanges.add(rawProposedChanges.get(0));
			} else {
				final ProposedChange newProposedChange = rawProposedChanges.get(i);
				addProposedChange(proposedChanges, newProposedChange);
			}
		}

		// filter out the records with "null conflict" and "old value = proposed value"
		final List<ProposedChange> resultList = new ArrayList<ProposedChange>();
		for (int i = 0; i < proposedChanges.size(); i++) {
			final ProposedChange proposedChange = proposedChanges.get(i);
			String oldValue = proposedChange.getOldValue();
			String proposedValue = proposedChange.getProposedValue();
			String conflictValue = proposedChange.getConflictValue();

			if ((!StringUtils.isEmpty(conflictValue) && !NO_CONFLICT.equalsIgnoreCase(conflictValue))
					|| ((StringUtils.isEmpty(conflictValue) || NO_CONFLICT.equalsIgnoreCase(conflictValue)) && isChanged(
							oldValue, proposedValue, ""))) {
				resultList.add(proposedChange);
			}
		}

		return resultList;
	}

	private List<ProposedChange> processProposedValidationChange(final List<ProposedChange> rawProposedChanges,
			final String classification) {

		// Combine multiple records to each validation and present the oldest value and latest proposed value
		final List<ProposedChange> proposedChanges = new ArrayList<ProposedChange>();
		for (int i = 0; i < rawProposedChanges.size(); i++) {
			if (i == 0) {
				proposedChanges.add(rawProposedChanges.get(0));
			} else {
				final ProposedChange newProposedChange = rawProposedChanges.get(i);
				addProposedValidationChange(proposedChanges, newProposedChange);
			}
		}

		// filter out records with "old value = proposed value"
		final List<ProposedChange> resultList = new ArrayList<ProposedChange>();
		for (int i = 0; i < proposedChanges.size(); i++) {
			final ProposedChange proposedChange = proposedChanges.get(i);
			final String oldValue = proposedChange.getOldValue();
			final String proposedValue = proposedChange.getProposedValue();
			final String validationType = proposedChange.getTableName();

			if (isValidationChanged(oldValue, proposedValue, classification, validationType)) {
				resultList.add(proposedChange);
			}
		}

		return resultList;
	}

	private List<RealizedChange> processRealizedChange(final List<RealizedChange> rawRealizedChanges) {
		// Combine multiple records to one record for each field and present the oldest value and latest value
		final List<RealizedChange> realizedChanges = new ArrayList<RealizedChange>();
		for (int i = 0; i < rawRealizedChanges.size(); i++) {
			if (i == 0) {
				realizedChanges.add(rawRealizedChanges.get(0));
			} else {
				final RealizedChange newRealizedChange = rawRealizedChanges.get(i);
				addRealizedChange(realizedChanges, newRealizedChange);
			}
		}

		// filter out records with "old value = new value"
		final List<RealizedChange> resultList = new ArrayList<RealizedChange>();
		for (int i = 0; i < realizedChanges.size(); i++) {
			final RealizedChange realizedChange = realizedChanges.get(i);
			String oldValue = realizedChange.getOldValue();
			String newValue = realizedChange.getNewValue();

			if (isChanged(oldValue, newValue, realizedChange.getTableName())) {
				resultList.add(realizedChange);
			}
		}

		return resultList;
	}

	@Override
	public List<ValidationChange> processRealizedValidationChange(
			final HashMap<String, RealizedChange> realizedValidationMap, final String classification) {
		final List<ValidationChange> validationChanges = new ArrayList<ValidationChange>();

		for (String key : realizedValidationMap.keySet()) {
			final RealizedChange realizedChange = realizedValidationMap.get(key);
			final String dataHolding = realizedChange.getFieldName();
			final String oldValue = realizedChange.getOldValue();
			final String newValue = realizedChange.getNewValue();
			final String validationType = realizedChange.getTableName();

			if (VALIDATION_DEFINITION.equals(validationType)) {
				if (isValidationChanged(oldValue, newValue, classification, validationType)) {
					setValidationChange(classification, dataHolding, validationChanges, oldValue, CHANGE_OLD);
					setValidationChange(classification, dataHolding, validationChanges, newValue, CHANGE_NEW);
				}
			} else {
				if (isValidationChanged(oldValue, newValue, classification, validationType)) {
					setValidationChange(classification, dataHolding, validationChanges, oldValue, CHANGE_OLD);
					setValidationStatusChange(dataHolding, validationChanges, newValue, CHANGE_NEW);
				}
			}
		}

		return validationChanges;

	}

	@Override
	public void resolveConflicts(final ResolveConflict resolveConflict, User currentUser) {
		Set<Long> discardedElementIds = new HashSet<Long>(); // actually domainelementId
		final List<ConflictProposedChange> conflicts = resolveConflict.getConflictChanges();

		for (ConflictProposedChange conflict : conflicts) {
			if (ConflictProposedChange.ActionCode_Keep.equalsIgnoreCase(conflict.getResolveActionCode())) {
				final Long baseElementVersionId = changeRequestSummaryMapper.findBaseElementVersionIdByRealizedContext(
						conflict.getConflictRealizedByContext().getContextId(), conflict.getElementVersionId(),
						resolveConflict.getCurrentContextId());
				changeRequestSummaryMapper.updateElementVersionChangedFromVersionId(baseElementVersionId,
						conflict.getElementVersionId());
				if (ConflictProposedChange.ChangeType_Validation.equalsIgnoreCase(conflict.getChangeType())) {
					final List<Long> conceptAndPropertyElementVersionIds = changeRequestSummaryMapper
							.findValidationConceptAndPropertyIdsByValidationDefinitionElementVersionId(
									conflict.getElementVersionId(), resolveConflict.getCurrentContextId());
					for (Long conceptOrPropertyElementVersionId : conceptAndPropertyElementVersionIds) {
						final Long baseConceptOrPropertyElementVersionId = changeRequestSummaryMapper
								.findBaseElementVersionIdByRealizedContext(conflict.getConflictRealizedByContext()
										.getContextId(), conceptOrPropertyElementVersionId, resolveConflict
										.getCurrentContextId());
						changeRequestSummaryMapper.updateElementVersionChangedFromVersionId(
								baseConceptOrPropertyElementVersionId, conceptOrPropertyElementVersionId);
					}
				}

			} else {
				if (ConflictProposedChange.ActionCode_Discard.equalsIgnoreCase(conflict.getResolveActionCode())) {
					if (ConflictProposedChange.ChangeType_Validation.equalsIgnoreCase(conflict.getChangeType())) {
						final List<Long> conceptAndPropertyElementVersionIds = changeRequestSummaryMapper
								.findValidationConceptAndPropertyIdsByValidationDefinitionElementVersionId(
										conflict.getElementVersionId(), resolveConflict.getCurrentContextId());
						for (Long conceptOrPropertyElementVersionId : conceptAndPropertyElementVersionIds) {
							changeRequestSummaryMapper.deleteStructureElementVersion(conceptOrPropertyElementVersionId);
						}
					}
					changeRequestSummaryMapper.deleteStructureElementVersion(conflict.getElementVersionId());
					// need transform
					Long discardElementId = conflict.getElementId();
					discardedElementIds.add(discardElementId);

				}
			}
		}
		if (discardedElementIds.size() > 0) {
			Iterator<Long> itDiscardedElementIds = discardedElementIds.iterator();
			while (itDiscardedElementIds.hasNext()) {
				transformElement(currentUser, itDiscardedElementIds.next(), context.context());
			}
		}

	}

	@Override
	public void resolveIndexConflicts(final ResolveConflict resolveConflict, User currentUser) {
		Set<Long> discardedElementIds = new HashSet<Long>(); // actually domainelementId
		Set<Long> keptElementIds = new HashSet<Long>(); // actually domainelementId

		final List<ConflictProposedIndexChange> conflicts = resolveConflict.getConflictIndexChanges();

		for (ConflictProposedIndexChange conflict : conflicts) {
			if (ConflictProposedIndexChange.ActionCode_Keep.equalsIgnoreCase(conflict.getResolveActionCode())) {
				final Long baseElementVersionId = changeRequestSummaryMapper.findBaseElementVersionIdByRealizedContext(
						conflict.getConflictRealizedByContext().getContextId(), conflict.getElementVersionId(),
						resolveConflict.getCurrentContextId());
				changeRequestSummaryMapper.updateElementVersionChangedFromVersionId(baseElementVersionId,
						conflict.getElementVersionId());
				Long keptElementId = conflict.getElementId();
				keptElementIds.add(keptElementId);

			} else {
				if (ConflictProposedIndexChange.ActionCode_Discard.equalsIgnoreCase(conflict.getResolveActionCode())) {
					// need sync
					changeRequestSummaryMapper.deleteStructureElementVersion(conflict.getElementVersionId());
					Long discardElementId = conflict.getElementId();
					discardedElementIds.add(discardElementId);
				}
			}
		}
		if (discardedElementIds.size() > 0) {
			Iterator<Long> itDiscardedElementIds = discardedElementIds.iterator();
			while (itDiscardedElementIds.hasNext()) {
				transformElement(currentUser, itDiscardedElementIds.next(), context.context());
			}
		}

		if (keptElementIds.size() > 0) {
			Iterator<Long> itKeptElementIds = keptElementIds.iterator();
			while (itKeptElementIds.hasNext()) {
				transformElement(currentUser, itKeptElementIds.next(), context.context());
			}
		}

	}

	@Override
	public void resolveSupplementConflicts(final ResolveConflict resolveConflict, User currentUser) {
		Set<Long> discardedElementIds = new HashSet<Long>(); // actually domainelementId
		Set<Long> keptElementIds = new HashSet<Long>(); // actually domainelementId

		final List<ConflictProposedSupplementChange> conflicts = resolveConflict.getConflictSupplementChanges();

		for (ConflictProposedSupplementChange conflict : conflicts) {
			if (ConflictProposedSupplementChange.ACTIONCODE_KEEP.equalsIgnoreCase(conflict.getResolveActionCode())) {
				final Long baseElementVersionId = changeRequestSummaryMapper.findBaseElementVersionIdByRealizedContext(
						conflict.getConflictRealizedByContext().getContextId(), conflict.getElementVersionId(),
						resolveConflict.getCurrentContextId());
				changeRequestSummaryMapper.updateElementVersionChangedFromVersionId(baseElementVersionId,
						conflict.getElementVersionId());
				Long keptElementId = conflict.getElementId();
				keptElementIds.add(keptElementId);

			} else {
				if (ConflictProposedSupplementChange.ACTIONCODE_DISCARD.equalsIgnoreCase(conflict
						.getResolveActionCode())) {
					// need sync
					changeRequestSummaryMapper.deleteStructureElementVersion(conflict.getElementVersionId());
					Long discardElementId = conflict.getElementId();
					discardedElementIds.add(discardElementId);
				}
			}
		}
		if (discardedElementIds.size() > 0) {
			Iterator<Long> itDiscardedElementIds = discardedElementIds.iterator();
			while (itDiscardedElementIds.hasNext()) {
				transformElement(currentUser, itDiscardedElementIds.next(), context.context());
			}
		}

		if (keptElementIds.size() > 0) {
			Iterator<Long> itKeptElementIds = keptElementIds.iterator();
			while (itKeptElementIds.hasNext()) {
				transformElement(currentUser, itKeptElementIds.next(), context.context());
			}
		}

	}

	@Autowired
	public void setChangeRequestIndexSummaryMapper(final ChangeRequestIndexSummaryMapper changeRequestIndexSummaryMapper) {
		this.changeRequestIndexSummaryMapper = changeRequestIndexSummaryMapper;
	}

	@Autowired
	public void setChangeRequestSummaryMapper(final ChangeRequestSummaryMapper changeRequestSummaryMapper) {
		this.changeRequestSummaryMapper = changeRequestSummaryMapper;
	}

	@Autowired
	public void setChangeRequestSupplementSummaryMapper(
			ChangeRequestSupplementSummaryMapper changeRequestSupplementSummaryMapper) {
		this.changeRequestSupplementSummaryMapper = changeRequestSupplementSummaryMapper;
	}

	public void setClassificationService(ClassificationService classificationService) {
		this.classificationService = classificationService;
	}

	@Autowired
	public void setConceptService(final ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setContext(CurrentContext context) {
		this.context = context;
	}

	@Autowired
	public void setIncompleteReportMapper(final IncompleteReportMapper incompleteReportMapper) {
		this.incompleteReportMapper = incompleteReportMapper;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	private void setValidationChange(final String classification, final String dataHolding,
			final List<ValidationChange> validationChanges, final String value, final String valueType) {

		if (!StringUtils.isEmpty(value) && !value.equals(NO_CONFLICT)) {
			if (value.equals(NO_VALUE)) {
				if (CHANGE_CONFLICT.equals(valueType)) {
					final ValidationChange validationChange = new ValidationChange();
					validationChanges.add(validationChange);
					validationChange.setValue(valueType);
					validationChange.setDataHolding(dataHolding);
					validationChange.setStatus(NO_VALUE);
				}
			} else {
				final ValidationChange validationChange = new ValidationChange();
				validationChanges.add(validationChange);
				validationChange.setValue(valueType);
				validationChange.setDataHolding(dataHolding);

				if (CIMSConstants.CCI.equalsIgnoreCase(classification)) {
					final CciValidationXml cciValidationXml = XmlUtils.deserialize(CciValidationXml.class, value);
					validationChange.setCciValidationXml(cciValidationXml);
				} else {
					final IcdValidationXml icdValidationXml = XmlUtils.deserialize(IcdValidationXml.class, value);
					validationChange.setIcdValidationXml(icdValidationXml);
				}
			}
		}
	}

	private void setValidationStatusChange(final String dataHolding, final List<ValidationChange> validationChanges,
			final String value, final String valueType) {
		final ValidationChange validationChange = new ValidationChange();
		validationChanges.add(validationChange);
		validationChange.setValue(valueType);
		validationChange.setDataHolding(dataHolding);

		// Change the status value from "disabled" to "removed" to avoid making user confused
		if (ConceptStatus.DISABLED.toString().equalsIgnoreCase(value)) {
			validationChange.setStatus(ConceptStatus.REMOVED.toString());
		}
	}

	// public void transformConcept(OptimisticLock lock, User user, TabularConcept concept, XslTransformer
	// xslTransformer) {
	private void transformElement(User user, Long elementId, ContextAccess access) {
		Object concept = access.load(elementId);
		if (concept instanceof TabularConcept) {
			TabularConcept tabular = (TabularConcept) concept;
			classificationService.transformConcept(new OptimisticLock(Long.MIN_VALUE), user, tabular);
		} else if (concept instanceof Supplement) {
			Supplement supplement = (Supplement) concept;
			classificationService.transformSupplement(new OptimisticLock(Long.MIN_VALUE), user, supplement);
		} else {
			Index index = (Index) concept;
			classificationService.transformIndex(new OptimisticLock(Long.MIN_VALUE), user, index);
		}
	}
}
