package ca.cihi.cims.refset.service.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.PropertyCriterion;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.concept.ColumnImpl;
import ca.cihi.cims.refset.concept.PickListImpl;
import ca.cihi.cims.refset.concept.RecordImpl;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.concept.SublistImpl;
import ca.cihi.cims.refset.concept.SupplementImpl;
import ca.cihi.cims.refset.concept.ValueImpl;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.exception.PicklistNotRemovableException;
import ca.cihi.cims.refset.handler.RefsetControlHandler;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.RecordsList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetConcept;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Sublist;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.concept.Value;

/**
 * @author lzhu
 * @version 1.0
 * @created 24-Jun-2016 4:44:42 PM
 */
public class RefsetFactory {

	private static RefsetControlHandler refsetControlHandler;

	/**
	 * return ColumnImpl.create(container,columnType)
	 *
	 * @param container
	 * @param columnType
	 * @param columnName
	 * @param columnOrder
	 * @throws DuplicateNameException
	 */
	public static Column createColumn(RefsetConcept container, String columnType, String columnName,
			short columnOrder) {
		Column column = ColumnImpl.create(container, columnType, columnName);
		column.setColumnOrder(columnOrder);
		return column;
	}

	/**
	 * return PickListImpl.create(refset,code)
	 *
	 * @param refset
	 * @param code
	 * @throws DuplicateNameException
	 * @throws DuplicateCodeNameException
	 */
	public static PickList createPickList(Refset refset, String code, String name, String classificationStandard)
			throws DuplicateCodeNameException {
		return PickListImpl.create(refset, code, name, classificationStandard);
	}

	/**
	 * return SublistImpl.create(record,column)
	 *
	 * @param list
	 * @param code
	 */
	public static Record createRecord(RecordsList list, String code) {
		return RecordImpl.create(list, code);

	}

	/**
	 * return RefsetImpl.create(code,name)
	 *
	 * @param code
	 * @param name
	 * @throws DuplicateNameException
	 * @throws DuplicateCodeNameException
	 */
	public static Refset createRefset(String code, String name) throws DuplicateCodeNameException {
		return RefsetImpl.create(code, name);
	}

	/**
	 * return SublistImpl.create(record,column)
	 *
	 * @param record
	 * @param column
	 */
	public static Sublist createSublist(Record record, Column column) {
		return SublistImpl.create(record, column);
	}

	/**
	 * return SupplementImpl.create(refset,code)
	 *
	 * @param refset
	 * @param code
	 * @throws DuplicateCodeNameException
	 * @throws DuplicateNameException
	 */
	public static Supplement createSupplement(Refset refset, String code, String name, String fileName, byte[] content)
			throws DuplicateCodeNameException {
		Supplement supplement = SupplementImpl.create(refset, code, name);
		supplement.setFilename(fileName);
		supplement.setContent(content, Language.ENG);

		return supplement;
	}

	/**
	 * return ValueImpl..create(record,column)
	 *
	 * @param record
	 * @param column
	 */
	public static Value createValue(Record record, Column column) {
		return ValueImpl.create(record, column);
	}

	/**
	 * classs = Classs.findByNameAndContext('Column', contextId) context = Context.findById(contextId) return new
	 * ColumnImpl(class, context, elementIdentifier, degree)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static ColumnImpl getColumn(Long contextId, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.COLUMN, context.getBaseClassificationName());
		return new ColumnImpl(clazz, context, elementIdentifier, degree);
	}

	/**
	 * classs = Classs.findByNameAndContext('PickList', contextId) context = Context.findById(contextId) return new
	 * PickListImpl(class, context, elementIdentifier, degree)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static PickList getPickList(Long contextId, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.PICKLIST, context.getBaseClassificationName());
		return new PickListImpl(clazz, context, elementIdentifier, degree);
	}

	/**
	 * classs = Classs.findByNameAndContext('Record', contextId) context = Context.findById(contextId) return new
	 * RecordImpl(class, context, elementIdentifier)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Record getRecord(Long contextId, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.RECORD, context.getBaseClassificationName());
		return new RecordImpl(clazz, context, elementIdentifier, degree);
	}

	/**
	 * classs = Classs.findByNameAndContext('Refset', contextId) context = Context.findById(contextId) return new
	 * RefsetImpl(class, context,elementIdentifier, degree)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Refset getRefset(Long contextId, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.REFSET, context.getBaseClassificationName());
		Refset refset = new RefsetImpl(clazz, context, elementIdentifier, degree);
		return refset;
	}

	/**
	 *
	 * Returns a list of all refset versions in the system filtered by input categoryId. If input category is not
	 * specified, retrieves all refsets in the system.
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 * @return
	 */
	public static List<RefsetVersion> getRefsetVersions(Long categoryId, String status, String versionStatus) {
		return refsetControlHandler.listRefsetVersions(categoryId, status, versionStatus);
	}

	/**
	 * classs = Classs.findByNameAndContext('Sublist', contextId) context = Context.findById(contextId) return new
	 * SublistImpl(class, context, elementIdentifier)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Sublist getSublist(Long contextId, ElementIdentifier sublistIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.SUBLIST, context.getBaseClassificationName());
		return new SublistImpl(clazz, context, sublistIdentifier, degree);
	}

	/**
	 * classs = Classs.findByNameAndContext('Sublist', contextId) context = Context.findById(contextId) return new
	 * SublistImpl(class, context, elementIdentifier)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Sublist getSublist(Long contextId, ElementIdentifier columnIdentifier,
			ElementIdentifier recordElementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs descibedBy = Classs.findByName(RefsetConstants.DESCRIBEDBY, context.getBaseClassificationName());
		Classs partOf = Classs.findByName(RefsetConstants.PARTOF, context.getBaseClassificationName());
		ConceptQueryCriteria sublistQuery = new ConceptQueryCriteria(SublistImpl.class, null, degree, new ArrayList<>(),
				RefsetConstants.SUBLIST);
		PropertyCriterion descCriterion = new PropertyCriterion();
		descCriterion.setClasssId(descibedBy.getClassId());
		descCriterion.setPropertyType(PropertyType.ConceptProperty.name());
		descCriterion.setValue(columnIdentifier.getElementId());
		sublistQuery.getConditionList().add(descCriterion);

		PropertyCriterion partOfCriterion = new PropertyCriterion();
		partOfCriterion.setClasssId(partOf.getClassId());
		partOfCriterion.setPropertyType(PropertyType.ConceptProperty.name());
		partOfCriterion.setValue(recordElementIdentifier.getElementId());
		sublistQuery.getConditionList().add(partOfCriterion);

		List<Concept> concepts = Concept.findConceptsByClassAndValues(contextId, sublistQuery);

		if (!CollectionUtils.isEmpty(concepts)) {
			return (Sublist) concepts.get(0);
		} else {
			return null;
		}
	}

	/**
	 * classs = Classs.findByNameAndContext('Supplement', contextId) context = Context.findById(contextId) return new
	 * SupplementImpl(class, context, elementIdentifier,degree)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Supplement getSupplement(Long contextId, ElementIdentifier elementIdentifier,
			ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.SUPPLEMENT, context.getBaseClassificationName());
		return new SupplementImpl(clazz, context, elementIdentifier, degree);
	}

	/**
	 * classs = Classs.findByNameAndContext('Value', contextId) context = Context.findById(contextId) return new
	 * ValueImpl(class, context, elementIdentifier,degree)
	 *
	 * @param contextId
	 * @param elementIdentifier
	 * @param degree
	 */
	public static Value getValue(Long contextId, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		Context context = Context.findById(contextId);
		Classs clazz = Classs.findByName(RefsetConstants.VALUE, context.getBaseClassificationName());
		return new ValueImpl(clazz, context, elementIdentifier, degree);
	}

	public static void setRefsetControlHandler(RefsetControlHandler handler) {
		refsetControlHandler = handler;
	}

	/**
	 * Get classification search result using classification code.
	 *
	 * @param contextId
	 *            the context Id.
	 * @param classificationId
	 *            the classification id.
	 * @param classificationCode
	 *            the classification code.
	 * @param searchConceptCode
	 *            the search concept code.
	 * @param maxResults
	 *            maximum # of classification returned in the search result.
	 * @return list of classification.
	 */
	public static List<ClassificationCodeSearchReponse> getActiveClassificationByCode(Long contextId,
			Long classificationId, String classificationCode, String searchConceptCode, Integer maxResults) {
		return refsetControlHandler.getActiveClassificationByCode(contextId, classificationId, classificationCode,
				searchConceptCode, maxResults);
	}

	/**
	 * Search Common Term text entered for the same columnType
	 *
	 * @param searchText
	 * @param columnType
	 * @param conceptId
	 * @param maxResults
	 * @return
	 */
	public static List<String> searchCommonTerm(String searchText, String columnType, Long conceptId,
			Integer maxResults) {
		return refsetControlHandler.searchCommonTerm(searchText, columnType, conceptId, maxResults);
	}

	/**
	 * Find value concept based on record and column
	 *
	 * @param contextId
	 * @param recordElementId
	 * @param columnElementId
	 * @return
	 */
	public static Value getValueByRecordAndColumn(Long contextId, Long recordElementId, Long columnElementId) {
		Context context = Context.findById(contextId);
		Classs partOf = Classs.findByName(RefsetConstants.PARTOF, context.getBaseClassificationName());
		Classs describedBy = Classs.findByName(RefsetConstants.DESCRIBEDBY, context.getBaseClassificationName());

		ConceptQueryCriteria criteria = new ConceptQueryCriteria(ValueImpl.class, null, ConceptLoadDegree.REGULAR,
				new ArrayList<>(), RefsetConstants.VALUE);
		PropertyCriterion recordCriterion = new PropertyCriterion();
		recordCriterion.setPropertyType(PropertyType.ConceptProperty.name());
		recordCriterion.setValue(recordElementId);
		recordCriterion.setClasssId(partOf.getClassId());
		criteria.getConditionList().add(recordCriterion);

		PropertyCriterion columnCriterion = new PropertyCriterion();
		columnCriterion.setPropertyType(PropertyType.ConceptProperty.name());
		columnCriterion.setValue(columnElementId);
		columnCriterion.setClasssId(describedBy.getClassId());
		criteria.getConditionList().add(columnCriterion);

		List<Concept> concepts = Concept.findConceptsByClassAndValues(contextId, criteria);
		if (!CollectionUtils.isEmpty(concepts)) {
			return (Value) concepts.get(0);
		}
		return null;
	}

	/**
	 * Add New Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist output configuration.
	 * @return the picklist output configuration with new output id.
	 */
	public static PicklistOutputDTO addPicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		refsetControlHandler.addPicklistOutputConfig(picklistOutput);

		return picklistOutput;
	}

	/**
	 * Get Picklist Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistElementId
	 *            the Picklist Element Id.
	 * @return List of Picklist Output Configuration.
	 */
	public static List<PicklistOutputDTO> getPicklistOutputConfig(Long refsetContextId, Long picklistElementId) {
		return refsetControlHandler.getPicklistOutputConfig(refsetContextId, picklistElementId);
	}

	/**
	 * Delete Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the picklist Output Id.
	 */
	public static void deletePicklistOutputConfig(Integer picklistOutputId) {
		refsetControlHandler.deletePicklistOutputConfig(picklistOutputId);
	}

	/**
	 * Get Picklist Column Output Configuration.
	 *
	 * @param refsetContextId
	 *            the Refset Context Id.
	 * @param picklistElementId
	 *            the Picklist Element Id.
	 * @return List of Picklist Column Output Configuration.
	 */
	public static List<PicklistColumnOutputDTO> getPicklistColumnOutputConfig(Long refsetContextId,
			Long picklistElementId) {
		return refsetControlHandler.getPicklistColumnOutputConfig(refsetContextId, picklistElementId);
	}

	/**
	 * Update Picklist Output Configuration.
	 *
	 * @param picklistOutput
	 *            the picklist Output Configuration.
	 */
	public static void updatePicklistOutputConfig(PicklistOutputDTO picklistOutput) {
		refsetControlHandler.updatePicklistOutputConfig(picklistOutput);
	}

	/**
	 * Get list of Picklist Output Configuration by Name.
	 *
	 * @param picklistElementId
	 *            the picklist element Id.
	 * @param name
	 *            the picklist output name.
	 * @return List of Picklist Output Configuration.
	 */
	public static List<PicklistOutputDTO> getPicklistOutputConfigByName(Long picklistElementId, String name) {
		return refsetControlHandler.getPicklistOutputConfigByName(picklistElementId, name);
	}

	/**
	 * Get Picklist Output Configuration using Picklist Output Id.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @return the Picklist Output Configuration.
	 */
	public static PicklistOutputDTO getPicklistOutputConfigByOutputId(Integer picklistOutputId) {
		return refsetControlHandler.getPicklistOutputConfigByOutputId(picklistOutputId);
	}

	public static List<RefsetVersion> getAllRefsets(String status) {
		return refsetControlHandler.getAllRefsets(status);
	}

	/**
	 * Update Picklist Output Accessibility Configuration.
	 *
	 * @param picklistOutputId
	 *            the Picklist Output Id.
	 * @param outputTabName
	 *            the Excel Output Tab Name.
	 * @param dataTableDescription
	 *            the Excel Data Table Description.
	 */
	public static void updatePicklistOutputAccessbilityConfig(Integer picklistOutputId, String asotReleaseIndCode,
			String outputTabName, String dataTableDescription) {
		refsetControlHandler.updatePicklistOutputAccessbilityConfig(picklistOutputId, asotReleaseIndCode, outputTabName,
				dataTableDescription);
	}

	/**
	 * Get Picklist Column Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the picklist output Id.
	 * @return list of picklist column output configuration.
	 */
	public static List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigById(Integer picklistOutputId) {
		return refsetControlHandler.getPicklistColumnOutputConfigById(picklistOutputId);
	}

	/**
	 * Add new Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutput
	 *            the picklist column output configuration.
	 */
	public static void addPicklistColumnOutput(PicklistColumnOutputDTO picklistColumnOutput) {
		refsetControlHandler.addPicklistColumnOutput(picklistColumnOutput);
	}

	/**
	 * Update Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutputId
	 *            the picklist column output id.
	 * @param orderNumber
	 *            the order number.
	 * @param displayModeCode
	 *            the display mode code.
	 */
	public static void updatePicklistColumnOutputConfig(Integer picklistColumnOutputId, Integer orderNumber,
			String displayModeCode) {
		refsetControlHandler.updatePicklistColumnOutputConfig(picklistColumnOutputId, orderNumber, displayModeCode);
	}

	/**
	 * Delete Picklist Column Output Configuration.
	 *
	 * @param picklistColumnOutputId
	 *            the picklist column output id.
	 */
	public static void deletePicklistColumnOutputConfig(Integer picklistColumnOutputId) {
		refsetControlHandler.deletePicklistColumnOutputConfig(picklistColumnOutputId);
	}

	/**
	 * Delete Picklist Column Output Configuration by columnId and contextId.
	 *
	 * @param columnId
	 *            the picklist column id.
	 * @param contextId
	 *            the refsetContextId
	 */
	public static void deletePicklistColumnOutputConfig(Long columnId, Long contextId) {
		refsetControlHandler.deletePicklistColumnOutputConfig(columnId, contextId);
	}

	/**
	 * Add New Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 * @return the refset output configuration with new output id.
	 */
	public static RefsetOutputDTO addRefsetOutputConfig(RefsetOutputDTO refsetOutput) {
		refsetControlHandler.addRefsetOutputConfig(refsetOutput);

		return refsetOutput;
	}

	/**
	 * Get Refset Output Configuration using refset context id and refset id. element id.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @param refsetElementId
	 *            the refset element id.
	 * @return List of refset output configuration.
	 */
	public static List<RefsetOutputDTO> getRefsetOutputConfigById(Long refsetContextId, Long refsetElementId) {
		return refsetControlHandler.getRefsetOutputConfigById(refsetContextId, refsetElementId);
	}

	public static void checkPickListRemovable(Long contextId, Long picklistElementId)
			throws PicklistNotRemovableException {
		if (refsetControlHandler.checkPicklistRemovable(contextId, picklistElementId) > 0) {
			throw new PicklistNotRemovableException();
		}
	}

	/**
	 * Update Refset Output Configuration.
	 *
	 * @param refsetOutput
	 *            the refset output configuration.
	 */
	public static void updateRefsetOutputConfig(RefsetOutputDTO refsetOutput) {
		refsetControlHandler.updateRefsetOutputConfig(refsetOutput);
	}

	/**
	 * Delete Refset Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output configuration id.
	 */
	public static void deleteRefsetOutputConfig(Integer refsetOutputId) {
		refsetControlHandler.deleteRefsetOutputConfig(refsetOutputId);
	}

	/**
	 * Get Refset Output Configuration using refset output id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output configuration.
	 */
	public static RefsetOutputDTO getRefsetOutputConfigByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlHandler.getRefsetOutputConfigByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Add New Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public static void addRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle) {
		refsetControlHandler.addRefsetOutputTitle(refsetOutputTitle);
	}

	/**
	 * Update Refset Output Title Configuration.
	 *
	 * @param refsetOutputTitle
	 *            the refset output title configuration.
	 */
	public static void updateRefsetOutputTitle(RefsetOutputTitleDTO refsetOutputTitle) {
		refsetControlHandler.updateRefsetOutputTitle(refsetOutputTitle);
	}

	/**
	 * Get Refset Output Title Configuration using Refset Output Id.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return the refset output title configuration.
	 */
	public static RefsetOutputTitleDTO getRefsetOutputTitleByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlHandler.getRefsetOutputTitleByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Get all Picklist Output Configurations for refset.
	 *
	 * @param refsetContextId
	 *            the refset context id.
	 * @return list of picklist output configuration.
	 */
	public static List<PicklistOutputDTO> getPicklistOutputConfigByRefsetContextId(Long refsetContextId) {
		return refsetControlHandler.getPicklistOutputConfigByRefsetContextId(refsetContextId);
	}

	/**
	 * Get Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return the refset picklist output configuration.
	 */
	public static RefsetPicklistOutputDTO getRefsetPicklistOutputById(Integer refsetOutputId,
			Integer picklistOutputId) {
		return refsetControlHandler.getRefsetPicklistOutputById(refsetOutputId, picklistOutputId);
	}

	/**
	 * Add New Refset Picklist Output Configuration.
	 *
	 * @param refsetPicklistOutput
	 *            the refset picklist output configuration.
	 */
	public static void addRefsetPicklistOutput(RefsetPicklistOutputDTO refsetPicklistOutput) {
		refsetControlHandler.addRefsetPicklistOutput(refsetPicklistOutput);
	}

	/**
	 * Get List of Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @return list of refset picklist output configurations.
	 */
	public static List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlHandler.getRefsetPicklistOutputByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Get Refset Supplement Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 * @return the refset supplement output configuration.
	 */
	public static RefsetSupplementOutputDTO getRefsetSupplementOutputById(Integer refsetOutputId, Long supplementId) {
		return refsetControlHandler.getRefsetSupplementOutputById(refsetOutputId, supplementId);
	}

	/**
	 * Add New Refset Supplement Output Configuration.
	 *
	 * @param refsetSupplementOutput
	 *            the refset supplement output configuration.
	 */
	public static void addRefsetSupplementOutput(RefsetSupplementOutputDTO refsetSupplementOutput) {
		refsetControlHandler.addRefsetSupplementOutput(refsetSupplementOutput);
	}

	/**
	 * Get Refset Supplement Output Configuration using Refset Output Id.
	 *
	 * @param refsetOutputId
	 *            the refset output Id.
	 * @return list of refset supplement output configuration.
	 */
	public static List<RefsetSupplementOutputDTO> getRefsetSupplementOutputByRefsetOutputId(Integer refsetOutputId) {
		return refsetControlHandler.getRefsetSupplementOutputByRefsetOutputId(refsetOutputId);
	}

	/**
	 * Delete Refset Picklist Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param picklistOutputId
	 *            the picklist output id.
	 */
	public static void deleteRefsetPicklistOutputConfig(Integer refsetOutputId, Integer picklistOutputId) {
		refsetControlHandler.deleteRefsetPicklistOutputConfig(refsetOutputId, picklistOutputId);
	}

	/**
	 * Delete Refset Supplement Output Configuration.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 * @param supplementId
	 *            the supplement id.
	 */
	public static void deleteRefsetSupplementOutputConfig(Integer refsetOutputId, Long supplementId) {
		refsetControlHandler.deleteRefsetSupplementOutputConfig(refsetOutputId, supplementId);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public static List<PicklistColumnEvolutionResultDTO> getPicklistColumnEvolutionList(
			PicklistColumnEvolutionRequestDTO request) {
		return refsetControlHandler.getPicklistColumnEvolutionList(request);
	}

	/**
	 * Delete Refset Output Title.
	 *
	 * @param refsetOutputId
	 *            the refset output id.
	 */
	public static void deleteRefsetOutputTitlePage(Integer refsetOutputId) {
		refsetControlHandler.deleteRefsetOutputTitlePage(refsetOutputId);
	}

	/**
	 * Get List of Refset Picklist Output Configuration.
	 *
	 * @param picklistOutputId
	 *            the picklist output id.
	 * @return list of refset picklist output configurations.
	 */
	public static List<RefsetPicklistOutputDTO> getRefsetPicklistOutputByPicklistOutputId(Integer picklistOutputId) {
		return refsetControlHandler.getRefsetPicklistOutputByPicklistOutputId(picklistOutputId);
	}

	/**
	 * Delete Picklist Column Output using Parent Column Output Id.
	 *
	 * @param parentOutputId
	 *            the parent picklist column output id.
	 */
	public static void deletePicklistColumnOutputConfigByParentOutputId(Integer parentOutputId) {
		refsetControlHandler.deletePicklistColumnOutputConfigByParentOutputId(parentOutputId);
	}

	/**
	 * Get list of Picklist Output Configuration by Picklist Configuration Code.
	 *
	 * @param refsetContextId
	 *            the picklist element Id.
	 * @param outputCode
	 *            the picklist output configuration code.
	 * @return List of Picklist Output Configuration.
	 */
	public static List<PicklistOutputDTO> getPicklistOutputConfigByOutputCode(Long refsetContextId, String outputCode) {
		return refsetControlHandler.getPicklistOutputConfigByOutputCode(refsetContextId, outputCode);
	}

	/**
	 *
	 * @param conceptCode
	 * @param icd10caContextId
	 * @param cciContextId
	 * @return
	 */
	public static String getConceptStatus(String conceptCode, Long icd10caContextId, Long cciContextId) {
		return refsetControlHandler.getConceptStatus(conceptCode, icd10caContextId, cciContextId);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public static List<PicklistColumnConfigEvolutionDTO> getPicklistColumnConfigEvolutionList(
			PicklistColumnEvolutionRequestDTO request) {
		return refsetControlHandler.getPicklistColumnConfigEvolutionList(request);
	}
	
    /**
     * Get Picklist Column Output Configuration.
     *
     * @param refsetContextId
     *            the Refset Context Id.
     * @param columnId
     *            the column Element Id.
     * @return List of Picklist Column Output Configuration.
     */
    public static List<PicklistColumnOutputDTO> getPicklistColumnOutputConfigByColumnId(Long refsetContextId, Long columnId) {
        return refsetControlHandler.getPicklistColumnOutputConfigByColumnId(refsetContextId, columnId);
    }
    
    /**
     * Get List of refset supplement output configurations.
     * 
     * @param refsetContextId
     *            the refset context Id
     * @param supplementId
     *            the supplement Id.
     * @return list of refset supplement output configuration.
     */
    public static List<RefsetSupplementOutputDTO> getRefsetSupplementOutputBySupplementId(Long refsetContextId,
            Long supplementId) {
        return refsetControlHandler.getRefsetSupplementOutputBySupplementId(refsetContextId, supplementId);
    }
}