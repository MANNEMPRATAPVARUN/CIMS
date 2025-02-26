package ca.cihi.cims.service;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.Language.FRENCH;
import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static ca.cihi.cims.model.Classification.ICD;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_CCICODE;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_GROUP;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_RUBRIC;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_SECTION;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CHAPTER;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.replace;
import static org.apache.commons.lang.StringUtils.replaceOnce;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;


import ca.cihi.cims.CIMSException;
import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciApproachTechniqueComponent;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.cci.CciTissueComponent;
import ca.cihi.cims.content.cci.CciValidation;
import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.content.icd.CategoryReferenceXml;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdIndexDrugsAndChemicalsXml;
import ca.cihi.cims.content.icd.IcdIndexNeoplasmXml;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.icd.IcdValidation;
import ca.cihi.cims.content.icd.IcdValidationXml;
import ca.cihi.cims.content.icd.IndexBaseXml;
import ca.cihi.cims.content.icd.IndexReferenceXml;
import ca.cihi.cims.content.icd.ReferenceListXml;
import ca.cihi.cims.content.icd.TabularRefXml;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.RootConcept;
import ca.cihi.cims.content.shared.SexValidation;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.content.shared.SupplementType;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.content.shared.Validation;
import ca.cihi.cims.content.shared.ValidationXml;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.IndexTerm;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.AttributeType;
import ca.cihi.cims.model.CciComponentType;
import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.DxType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.ChangeRequestPermissionWrapper;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.DrugDetailType;
import ca.cihi.cims.model.index.IndexCategoryReferenceModel;
import ca.cihi.cims.model.index.IndexChildRules;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexTermReferenceModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.model.index.NeoplasmDetailType;
import ca.cihi.cims.model.index.SiteIndicatorModel;
import ca.cihi.cims.model.index.TabularReferenceModel;
import ca.cihi.cims.model.sgsc.CCIComponentSupplement;
import ca.cihi.cims.model.sgsc.CCIRubric;
import ca.cihi.cims.model.supplement.SupplementChildRules;
import ca.cihi.cims.model.supplement.SupplementMatter;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.model.tabular.TabularConceptDetails;
import ca.cihi.cims.model.tabular.TabularConceptDiagramModel;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.model.tabular.TabularConceptXmlType;
import ca.cihi.cims.model.tabular.validation.TabularConceptCciValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptCciValidationSetReportModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetReportModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationGenderModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationSetModel;
import ca.cihi.cims.util.DtdValidator;
import ca.cihi.cims.util.SpecialCharactersUtils;
import ca.cihi.cims.util.XmlUtils;
import ca.cihi.cims.validator.CodeValidator;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.filter.CurrentContext;

public class ClassificationService {

	private static final String ASTERISC = "*";
	private static final String CANNOT_BE_REMOVED = " cannot be removed if published, has children, references or proposed changes in other active change requests";

	private static final String CIHI_CIMS_INDEX_DTD = "cihi_cims_index.dtd";
	public static final String[] CONCEPT_XML_DTDS = new String[] { "cihi_qualifierlist_note.dtd",
			"cihi_qualifierlist_also.dtd", "cihi_qualifierlist_includes.dtd", "cihi_qualifierlist_excludes.dtd",
			"cihi_table.dtd", "cihi_qualifierlist_omit.dtd", "cihi_qualifierlist_definition.dtd",
			"cihi_cims_supplement.dtd" };
	private static final String DAGGER = "+";
	private static final boolean DEBUG_ALLOW_ALL = false;
	private static final String DOCTYPE_INDEX_DTD = "<!DOCTYPE index SYSTEM \"/dtd/" + CIHI_CIMS_INDEX_DTD + "\">";

	private static final String DOCTYPE_VALIDATION_DTD = "<!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\">";
	private static final String ICD_MORPHOLOGY_CHAPTER_CODE = "22";
	private static final String INDEX_CATEGORY_REFERENCE_SORT_STRING_A = "aaa-sort-string-aaa###";
	private static final String INDEX_CATEGORY_REFERENCE_SORT_STRING_B = "aaa-sort-string-bbb###";

	private static final String INDEX_CATEGORY_REFERENCE_SORT_STRING_C = "aaa-sort-string-ccc###";
	private static final String INDEX_CATEGORY_REFERENCE_SORT_STRING_Z = "aaa-sort-string-zzz###";
	private static final String MODIFICATION_NOT_ALLOWED = "Modification is not allowed";
	private static final String VALUE_NOT_ALLOWED = "Value is not allowed";

	@Autowired
	private ChangeRequestAccessService accessService;

	@Autowired
	private ChangeRequestService changeRequestService;
	private final CodeValidator codeValidator = new CodeValidator();
	@Autowired
	private ConceptService conceptService;
	@Autowired
	@Qualifier("transformationService")
	private TransformationService conceptTransformationService;
	@Autowired
	private CurrentContext context;
	@Autowired
	private ContextOperations contextOperations;
	private volatile Long daggerAsteriskStartId = null;
	private final DtdValidator dtdValidator = new DtdValidator();
	private final List<DxType> dxTypes = DxType.types();
	@Autowired
	protected ElementOperations elementOperations;

	@Autowired
	private TransformIndexService indexTransformationService;

	private final Logger log = LogManager.getLogger(ClassificationService.class);
	@Autowired
	private NonContextOperations nonContextOperations;
	@Autowired
	private TransformSupplementService transformSupplementService;
	private final Validator validator = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();
	@Autowired
	private ViewService viewService;

	// ----------------------------------------------------------------------

	private void assignDescriptions(TabularConcept tabular, String shortTitleEng, String shortTitleFra,
			String longTitleEng, String longTitleFra) {
		tabular.shortDescription(ENGLISH, shortTitleEng);
		tabular.shortDescription(FRENCH, shortTitleFra);
		tabular.longDescription(ENGLISH, longTitleEng);
		tabular.longDescription(FRENCH, longTitleFra);
		tabular.userDescription(ENGLISH, longTitleEng);
		tabular.userDescription(FRENCH, longTitleFra);
	}

	private void assignParent(TabularConcept tabular, Long parentId) {
		if (parentId != null) {
			tabular.setParent(getTabularById(parentId));
		}
	}

	private ChangeRequestPermission checkBasicInfoEditAllowed(User user, String subject,
			ChangeRequestCategory category) {
		ChangeRequestPermission changeRequestPermission = getConceptInfoPermission(user, category);
		if (!changeRequestPermission.isCanWrite()) {
			throw new CIMSException(subject + " can not be saved");
		}
		return changeRequestPermission;
	}

	private ChangeRequestPermission checkChangeRequestEditAllowed(User user, String subject,
			ChangeRequestCategory category) {
		ChangeRequestPermission changeRequestPermission = getChangeRequestClassificationPermission(user, category);
		if (!changeRequestPermission.isCanWrite()) {
			throw new CIMSException(subject + " can not be saved");
		}
		return changeRequestPermission;
	}

	private ChangeRequestPermission checkConceptCreationAllowed(User user, ChangeRequestCategory category) {
		ChangeRequestPermission changeRequestPermission = getConceptInfoPermission(user, category);
		if (!changeRequestPermission.isCanAdd()) {
			throw new CIMSException(category.getSubject() + " can be added only in the version year");
		}
		if (category == ChangeRequestCategory.T) {
			Set<Language> languages = getChangeRequestLanguages();
			if (!languages.containsAll(Language.ALL)) {
				throw new CIMSException("New code values can only be added in bilingual change requests");
			}
		}
		return changeRequestPermission;
	}

	private ChangeRequestPermission checkConceptInfoDeleteAllowed(User user, ChangeRequestCategory category) {
		ChangeRequestPermission changeRequestPermission = getConceptInfoPermission(user, category);
		if (!changeRequestPermission.isCanDelete()) {
			throw new CIMSException(category.getSubject() + " can not be deleted");
		}
		if (category == ChangeRequestCategory.T) {
			Set<Language> languages = getChangeRequestLanguages();
			if (!languages.containsAll(Language.ALL)) {
				throw new CIMSException("Code values can only be removed in bilingual change requests");
			}
		}
		return changeRequestPermission;
	}

	private void checkConceptIsRubricOrCategory(TabularConcept concept) {
		TabularConceptType conceptType = toSubType(concept);
		if ((conceptType != CCI_RUBRIC) && (conceptType != ICD_CATEGORY)) {
			throw new CIMSException("Unsupported TABLE type: " + conceptType);
		}
	}

	private ChangeRequestPermission checkConceptNonInfoEditAllowed(User user, String subject) {
		ChangeRequestPermission p = getConceptNonInfoPermission(user);
		if (!p.isCanWrite()) {
			throw new CIMSException(subject + " can not be edited");
		}
		return p;
	}

	private void checkValidationSupported(TabularConcept tabular) {
		TabularConceptType type = toSubType(tabular);
		if (!((type == CCI_RUBRIC) || (type == CCI_CCICODE) || (type == ICD_CATEGORY))) {
			throw new CIMSException("Validation on " + type + " is not supported");
		}
	}

	private void copyValidationXml(ValidationXml xml, Validation found, TabularConceptValidationSetModel model) {
		xml.setLanguage("");
		xml.setClassification(getCurrentBaseClassification());//
		xml.setElementId(found.getElementId());
		xml.setGenderCode(model.getGenderCode());
		xml.setAgeRange(model.getAgeMinimum() + "-" + model.getAgeMaximum());
		SexValidation sex = getGenderByCode(model.getGenderCode());
		xml.setGenderDescriptionEng(sex.getDescription(ENGLISH.getCode()));
		xml.setGenderDescriptionFra(sex.getDescription(FRENCH.getCode()));
	}

	@Transactional
	public void createIndex(OptimisticLock lock, ErrorBuilder result, User user, long parentId, IndexModel model,
			Language lang) {
		checkConceptCreationAllowed(user, ChangeRequestCategory.I);
		IndexModel parent = toModel(getIndex(parentId), lang, true);
		IndexChildRules rules = new IndexChildRules(parent, isVersionYear());
		if (model.getType() != null) {
			if (!rules.canAdd(model.getType())) {
				throw new CIMSException("Can't add type: " + model.getType());
			}
		} else {
			model.setType(rules.addableChild());
		}
		IndexType type = model.getType();
		ContextAccess access = getContexAccess();
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Index", "");
		Index idx = (Index) access.createWrapper(type.getClazz(), type.getCode(), businessKey);
		idx.setParent(parent.getEntity());
		if (idx instanceof IndexTerm) {
			int nestingLevel = parent.getLevel() + 1;
			((IndexTerm) idx).setNestingLevel(nestingLevel);
			model.setLevel(nestingLevel);
		}
		access.persist();
		model.setEntity(idx);
		model.setElementId(idx.getElementId());
		saveIndex(lock, result, user, model, lang);
	}

	@Transactional
	public void createSupplement(OptimisticLock lock, ErrorBuilder result, User user, long parentId,
			SupplementModel model, Language lang) {
		checkConceptCreationAllowed(user, ChangeRequestCategory.S);
		int level = 0;
		BaseConcept parent = getById(parentId);
		if (parent instanceof Supplement) {
			Supplement p = (Supplement) parent;
			level = p.getNestingLevel();
		}
		SupplementChildRules rules = new SupplementChildRules(isVersionYear(), level);
		if (!rules.canAdd()) {
			throw new CIMSException("Can't add child");
		}
		ContextAccess access = getContexAccess();
		Supplement idx = Supplement.create(access);
		idx.setParent(parent);
		model.setLevel(level + 1);

		access.persist();

		model.setEntity(idx);
		model.setElementId(idx.getElementId());
		saveSupplement(lock, result, user, model, lang);
	}

	@Transactional
	public long createTabular(OptimisticLock lock, ErrorBuilder result, User user, TabularConceptType type,
			Long parentId, String code) {
		checkConceptCreationAllowed(user, ChangeRequestCategory.T);
		// FIXME: validate [parentId]
		String codeError = null;
		switch (type) {
		case ICD_CHAPTER: {
			codeError = codeValidator.validateOthers(ICD_CHAPTER, code);
			break;
		}
		case ICD_BLOCK: {
			TabularConceptModel parentModel = getTabularConceptLightById(parentId);
			codeError = codeValidator.validateIcdBlock(code, parentModel.isMorphology());
			break;
		}
		case ICD_CATEGORY: {
			TabularConceptModel parentModel = getTabularConceptLightById(parentId);
			int nestingLevel = parentModel.getType() == ICD_CATEGORY ? parentModel.getNestingLevel() + 1 : 1;
			codeError = codeValidator.validateIcdCategory(code, nestingLevel, parentModel.isMorphology(),
					parentModel.getCode());
			break;
		}
		case CCI_SECTION: {
			codeError = codeValidator.validateOthers(CCI_SECTION, code);
			break;
		}
		case CCI_BLOCK: {
			TabularConceptModel parentModel = getTabularConceptLightById(parentId);
			String sectionCode = getContainingPageCode(parentModel.getElementId());
			codeError = codeValidator.validateCciBlock(code, sectionCode);
			break;
		}
		}
		if (codeError != null) {
			result.rejectValue("code", codeError);
		}
		if (!result.hasErrors()) {
			TabularConcept tabular = createTabular(type, code);
			tabular.setParent(
					(type == ICD_CHAPTER) || (type == CCI_SECTION) ? getRootConcept() : getTabularById(parentId));
			saveTabular(lock, tabular, user, true, false);
			return tabular.getElementId();
		} else {
			return 0;
		}
	}

	// TODO: inline
	private TabularConcept createTabular(TabularConceptType type, String code) {
		switch (type.getClassification()) {
		case CCI:
			return createTabularCci(type, code);
		case ICD:
			return IcdTabular.create(getContexAccess(), code, type.getCode());
		default:
			throw new RuntimeException("Unsupported model: " + type.getClassification());
		}
	}

	private TabularConcept createTabularCci(TabularConceptType type, String code) {
		switch (type) {
		case CCI_BLOCK:
			return CciTabular.createBlock(getContexAccess(), code);
		case CCI_SECTION:
			return CciTabular.createSection(getContexAccess(), code);
		default:
			throw new CIMSException("Use correspondent [createCci*] method");
		}
	}

	@Transactional
	public long createTabularCciCode(OptimisticLock lock, ErrorBuilder result, User user, long parentId,
			Long cciApproachTechniqueId, Long cciDeviceId, Long cciTissueId) {
		if ((cciApproachTechniqueId == null) && (cciDeviceId == null) && (cciTissueId == null)) {
			throw new CIMSException("Sorry, you must select at least one qualifier.");
		}
		checkConceptCreationAllowed(user, ChangeRequestCategory.T);
		CciApproachTechniqueComponent approach = getById(cciApproachTechniqueId);
		CciDeviceAgentComponent deviceAgent = getById(cciDeviceId);
		CciTissueComponent tissue = getById(cciTissueId);

		CciTabular parent = getTabularById(parentId);
		CciGroupComponent group = parent.getGroupComponent();
		String sectionCode = getContainingPageCode(parent.getElementId());
		CciInterventionComponent intervention = parent.getInterventionComponent();

		CciTabular tabular = CciTabular.createCode(getContexAccess(), sectionCode, group, intervention, approach,
				deviceAgent, tissue);
		assignParent(tabular, parentId);

		// CM-RU042
		CciComponent[] components = { intervention, group, approach, deviceAgent, tissue };
		String longTitleEng = getLongTitle(ENGLISH, components);
		String longTitleFra = getLongTitle(FRENCH, components);
		String shortTitleEng = getShortTitle(ENGLISH, components);
		String shortTitleFra = getShortTitle(FRENCH, components);
		assignDescriptions(tabular, shortTitleEng, shortTitleFra, longTitleEng, longTitleFra);
		CciComponent[] componentsUser = { approach, deviceAgent, tissue };
		tabular.userDescription(ENGLISH, getLongTitle(ENGLISH, componentsUser));
		tabular.userDescription(FRENCH, getLongTitle(FRENCH, componentsUser));

		saveTabular(lock, tabular, user, true, false);
		return tabular.getElementId();
	}

	@Transactional
	public long createTabularCciGroup(OptimisticLock lock, ErrorBuilder result, User user, Long parentId,
			long cciGroupId) {
		checkConceptCreationAllowed(user, ChangeRequestCategory.T);
		CciGroupComponent group = getById(cciGroupId);
		CciTabular parent = getTabularById(parentId);
		String sectionCode = getContainingPageCode(parent.getElementId());
		CciTabular tabular = CciTabular.createGroup(getContexAccess(), sectionCode, group);
		assignParent(tabular, parentId);
		CciComponent[] components = { group };
		// CM-RU040
		String longTitleEng = getLongTitle(ENGLISH, components);
		String longTitleFra = getLongTitle(FRENCH, components);
		String shortTitleEng = getShortTitle(ENGLISH, components);
		String shortTitleFra = getShortTitle(FRENCH, components);
		assignDescriptions(tabular, shortTitleEng, shortTitleFra, longTitleEng, longTitleFra);
		saveTabular(lock, tabular, user, true, false);
		return tabular.getElementId();
	}

	@Transactional
	public long createTabularCciRubric(OptimisticLock lock, ErrorBuilder result, User user, long parentId,
			long cciInterventionId) {
		checkConceptCreationAllowed(user, ChangeRequestCategory.T);
		CciInterventionComponent intervention = getById(cciInterventionId);
		CciTabular parent = getTabularById(parentId);
		CciGroupComponent group = parent.getGroupComponent();
		String sectionCode = getContainingPageCode(parent.getElementId());
		CciTabular tabular = CciTabular.createRubric(getContexAccess(), sectionCode, group, intervention);
		assignParent(tabular, parentId);
		CciComponent[] components = { intervention, group };
		// CM-RU041
		String longTitleEng = getLongTitle(ENGLISH, components);
		String longTitleFra = getLongTitle(FRENCH, components);
		String shortTitleEng = getShortTitle(ENGLISH, components);
		String shortTitleFra = getShortTitle(FRENCH, components);
		assignDescriptions(tabular, shortTitleEng, shortTitleFra, longTitleEng, longTitleFra);
		saveTabular(lock, tabular, user, true, false);
		return tabular.getElementId();
	}

	private void deleteById(long id) {
		ContextAccess contexAccess = getContexAccess();
		nonContextOperations.remove(contexAccess.getContextId(), id);
		contexAccess.persist();
	}

	@Transactional
	public void deleteIndexById(OptimisticLock lock, User user, long id, Language lang) {
		checkConceptInfoDeleteAllowed(user, ChangeRequestCategory.I);
		IndexModel model = getIndexById(id, lang);
		if (model != null) {
			if (!isIndexDeletableShallow(model)) {
				throw new CIMSException("Delete is not allowed");
			}
			if (contextOperations.hasConceptBeenPublished(id)) {
				throw new CIMSException("The index cannot be removed when it has been published");
			}
			if (hasChildren(id, lang)) {
				throw new CIMSException("The index cannot be removed when it has children");
			}
			try {
				deleteById(id);
			} catch (Exception ex) {
				throw new CIMSException("The index" + CANNOT_BE_REMOVED, ex);
			}
			Index parent = model.getEntity().getParent();
			Index containingPage = model.getEntity().getContainingPage();
			transformIndexParentAfterDelete(parent, containingPage, lang);
			getContexAccess().persist();
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	@Transactional
	public void deleteSupplementById(OptimisticLock lock, User user, long id, Language lang) {
		checkConceptInfoDeleteAllowed(user, ChangeRequestCategory.S);
		SupplementModel model = getSupplementById(id, lang);
		if (model != null) {
			if (!isSupplementDeletableShallow(model)) {
				throw new CIMSException("Delete is not allowed");
			}
			if (contextOperations.hasConceptBeenPublished(id)) {
				throw new CIMSException("The supplement cannot be removed when it has been published");
			}
			if (hasChildren(id, lang)) {
				throw new CIMSException("The supplement cannot be removed when it has children");
			}
			try {
				deleteById(id);
			} catch (Exception ex) {
				throw new CIMSException("The supplement" + CANNOT_BE_REMOVED, ex);
			}
			getContexAccess().persist();
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	@Transactional
	public void deleteTabularById(OptimisticLock lock, User user, long id, Language language) {
		checkConceptInfoDeleteAllowed(user, ChangeRequestCategory.T);
		TabularConcept tabular = getTabularById(id);
		if (tabular != null) {
			try {
				for (Validation v : getValidations(tabular)) {
					deleteById(v.getElementId());
				}
				TabularConcept parent = getParent(tabular);
				TabularConcept containingPage = getContainingPage(tabular);
				deleteById(id);
				transformParentAfterDelete(tabular, parent, containingPage);
			} catch (Exception ex) {
				String error = ex.getMessage();
				if (StringUtils.contains(error, "Concept cannot be removed")) {
					error = "The code" + CANNOT_BE_REMOVED;
				}
				throw new CIMSException(error, ex);
			}
		}
	}

	@Transactional
	public void deleteTabularValidationSet(OptimisticLock lock, User user, long tabularId, long dataHoldingId) {
		checkChangeRequestEditAllowed(user, "Validation Set", ChangeRequestCategory.T);
		TabularConcept tabular = getTabularById(tabularId);
		checkValidationSupported(tabular);
		Validation found = getTabularValidation(tabular, dataHoldingId);
		if (found != null) {
			found.setStatus(ConceptStatus.DISABLED.name());
			//need to do more fix for issue 36640 when user removing the validation
			TabularConceptType type = toSubType(tabular);
			if (type == CCI_RUBRIC) {
				transformConcept(tabular);
			} else if (type == TabularConceptType.CCI_CCICODE) {
				transformConcept(((CciTabular) tabular).getParent());
			} else if (type == ICD_CATEGORY) {
				IcdTabular icd = (IcdTabular) tabular;
				transformConcept(icd);
			}
		}
	}

	public ChangeRequestAccessService getAccessService() {
		return accessService;
	}

	@SuppressWarnings("all")
	private <T> T getById(long id) {
		try {
			return (T) getContexAccess().load(id);
		} catch (NullPointerException ex) {
			// not found
			return null;
		}
	}

	@SuppressWarnings("all")
	private <T> T getById(Long id) {
		if (id == null) {
			return null;
		} else {
			return (T) getById(id.longValue());
		}
	}

	public List<IdCodeDescription> getCciComponentsPerSection(long sectionElementId, Language language,
			CciComponentType type) {
		return conceptService.getCciComponentsPerSection(sectionElementId, getCurrentContextId(), language, type);
	}

	public List<IdCodeDescription> getCciExtentReferences(Language lang) {
		return conceptService.getRefAttributePerType(getCurrentContextId(), lang, AttributeType.E);
	}

	/**
	 * Get group supplement content for section 1 - 3
	 *
	 * @param language
	 * @param contextId
	 * @param sectionId
	 * @param groupCode
	 * @param description
	 * @return
	 */
	public String getCCIGroupContent(String language, Long contextId, String sectionCode, String groupCode, Long id) {
		List<CCIComponentSupplement> components = viewService.getCciGroupComponentsWithDefinition(language, contextId,
				sectionCode, groupCode);
		StringBuilder result = new StringBuilder();
		result.append("<table class='conceptTable'>");
		result.append("<tr><td colspan='4'><span class='title'>(").append(groupCode).append(")&nbsp;&nbsp;")
				.append(viewService.getCCIGroupTitle(id, contextId, language)).append("</span></td></tr>");
		for (CCIComponentSupplement component : components) {
			if (component.getConceptCode().length() > 1) {
				result.append(component.toHtmlString());
			}
		}
		result.append("</table>");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	public List<CciInvasivenessLevel> getCciInvasivenessLevels() {
		Iterator<CciInvasivenessLevel> levels = getContexAccess().findAll(CciInvasivenessLevel.class);
		return IteratorUtils.toList(levels);
	}

	public List<IdCodeDescription> getCciLocationReferences(Language lang) {
		return conceptService.getRefAttributePerType(getCurrentContextId(), lang, AttributeType.L);
	}

	public List<IdCodeDescription> getCciModeOfDeliveryReferences(Language lang) {
		return conceptService.getRefAttributePerType(getCurrentContextId(), lang, AttributeType.M);
	}

	/**
	 * Get rubric supplement content for section 1 - 3
	 *
	 * @param language
	 * @param contextId
	 * @param sectionId
	 * @param groupCode
	 * @param id
	 *            group code id
	 * @return
	 */
	public String getCCIRubricContent(String language, Long contextId, String sectionCode, String groupCode, Long id) {

		if ("8".equals(sectionCode)) {
			return getCCIRubricContent8(language, contextId, sectionCode, groupCode);
		}

		long sectionId = getConceptService().getCCISectionIdBySectionCode(sectionCode, contextId);
		List<IdCodeDescription> cciGroups = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				contextId, Language.fromString(language), CciComponentType.GroupComp, "code", groupCode);
		List<IdCodeDescription> cciInterventions = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				contextId, Language.fromString(language), CciComponentType.Intervention, "description", null);

		List<CCIRubric> rubrics = viewService.findCCIRubric(contextId, sectionCode, groupCode);
		Map<String, Map<String, CCIRubric>> cciRubricFinder = new HashMap<String, Map<String, CCIRubric>>();
		for (CCIRubric rubric : rubrics) {
			Map<String, CCIRubric> interventionMap = cciRubricFinder.get(rubric.getInterventionCode());
			if (interventionMap == null) {
				interventionMap = new HashMap<String, CCIRubric>();
				cciRubricFinder.put(rubric.getInterventionCode(), interventionMap);
			}
			interventionMap.put(rubric.getGroupCode(), rubric);
		}

		StringBuilder result = new StringBuilder();

		StringBuilder header = new StringBuilder();
		StringBuilder top = new StringBuilder();
		for (int j = 0; j < cciGroups.size(); j++) {
			top.append("<th style='min-width:82px;width:82px;'></th>");
		}
		int i = 0;
		if (!StringUtils.isEmpty(groupCode)) { // section 1-3 show group header
			result.append("<table class='conceptTable'><tr><td colspan='4'><span class='title'>(").append(groupCode)
					.append(") ").append(viewService.getCCIGroupTitle(id, contextId, language))
					.append("</span></td></tr></table>");
		} else { // section 5-8 show section label
			result.append("<table class='conceptTable'><tr><td colspan='4'><span class='title'>")
					.append(viewService.getUserTitle(sectionId, contextId, language))
					.append("</span></td></tr></table>");
		}
		result.append("<div id='sticker'><table style='width:auto'>");
		for (IdCodeDescription component : cciGroups) {
			if (component.getCode().length() > 1) {
				if (i == 0) {
					result.append("<tr>");
				}
				result.append("<th style='min-width: 240px;width:240px'>").append(component.getCode()).append(" - ")
						.append(component.getDescription()).append("</th>");
				if (++i == 2) {
					result.append(top.toString());
					i = 0;
					result.append("</tr>");
				}
				header.append("<th style='border: 1px solid black;min-width:82px;width:82px; text-align:center;'>")
						.append(component.getCode()).append("</th>");
			}
		}
		if (i == 1) {
			result.append(top.toString()).append("<th style='min-width:82px;width:82px;'></th></tr>");
		}
		result.append(
				"<tr><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th>")
				.append(header.toString()).append("</tr></table></div>");
		result.append("<div><table style='width:auto'>");
		for (IdCodeDescription intervention : cciInterventions) {
			result.append("<tr><td style='border: 1px solid black;min-width: 240px;width:240px'>")
					.append(intervention.getDescription())
					.append("</td><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:center;'>(")
					.append(intervention.getCode()).append(")</td>");
			Map<String, CCIRubric> interventionMap = cciRubricFinder.get(intervention.getCode());
			for (IdCodeDescription component : cciGroups) {
				if (component.getCode().length() > 1) {
					result.append("<td style='border: 1px solid black;min-width:82px;width:82px;text-align:center;'>");
					if ((interventionMap != null) && (interventionMap.get(component.getCode()) != null)) {
						result.append("<a href=\"javascript:navigateFromDynaTree('")
								.append(interventionMap.get(component.getCode()).getContainingPath()).append("');\">")
								.append(interventionMap.get(component.getCode()).getRubricCode()).append("</a>");
					} else {
						result.append("&nbsp;");
					}
					result.append("</td>");
				}
			}
			result.append("</tr>");
		}
		result.append("</table></div>");
		return result.toString();
	}

	public String getCCIRubricContent8(String language, long contextId, String sectionCode, String groupCode) {
		long sectionId = getConceptService().getCCISectionIdBySectionCode(sectionCode, contextId);
		List<IdCodeDescription> cciGroups = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				contextId, Language.fromString(language), CciComponentType.GroupComp, "code", groupCode);
		List<IdCodeDescription> cciInterventions = getConceptService().getCciComponentsPerSectionLongTitle(sectionId,
				contextId, Language.fromString(language), CciComponentType.Intervention, "description", null);

		List<CCIRubric> rubrics = viewService.findCCIRubric(contextId, sectionCode, groupCode);
		Map<String, Map<String, CCIRubric>> cciRubricFinder = new HashMap<String, Map<String, CCIRubric>>();
		for (CCIRubric rubric : rubrics) {
			Map<String, CCIRubric> groupMap = cciRubricFinder.get(rubric.getGroupCode());
			if (groupMap == null) {
				groupMap = new HashMap<String, CCIRubric>();
				cciRubricFinder.put(rubric.getGroupCode(), groupMap);
			}
			groupMap.put(rubric.getInterventionCode(), rubric);
		}

		StringBuilder result = new StringBuilder();

		StringBuilder header = new StringBuilder();
		result.append("<table class='conceptTable'><tr><td colspan='4'><span class='title'>")
				.append(viewService.getUserTitle(sectionId, contextId, language)).append("</span></td></tr></table>");
		result.append("<div id='sticker'><table style='width:auto'>");
		for (IdCodeDescription component : cciInterventions) {

			header.append("<th style='border: 1px solid black;min-width:150px;width:150px; text-align:center;'>")
					.append(component.getDescription()).append("<br/>(").append(component.getCode()).append(")</th>");

		}
		result.append("<tr><th style='border: 1px solid black;min-width: 240px;width:240px'>&nbsp;</th>")
				.append(header.toString()).append("</tr></table></div>");
		result.append("<div><table style='width:auto'>");
		for (IdCodeDescription cciGroup : cciGroups) {
			result.append("<tr><td style='border: 1px solid black;min-width: 240px;width:240px;text-align:left'>")
					.append(cciGroup.getCode()).append(" - ").append(cciGroup.getDescription()).append("</td>");
			Map<String, CCIRubric> groupMap = cciRubricFinder.get(cciGroup.getCode());
			for (IdCodeDescription component : cciInterventions) {
				result.append("<td style='border: 1px solid black;min-width:150px;width:150px;text-align:center'>");
				if ((groupMap != null) && (groupMap.get(component.getCode()) != null)) {
					result.append("<a href=\"javascript:navigateFromDynaTree('")
							.append(groupMap.get(component.getCode()).getContainingPath()).append("');\">")
							.append(groupMap.get(component.getCode()).getRubricCode()).append("</a>");
				} else {
					result.append("&nbsp;");
				}
				result.append("</td>");
			}
			result.append("</tr>");
		}
		result.append("</table></div>");
		return result.toString();
	}

	public List<IdCodeDescription> getCciStatusReferences(Language lang) {
		return conceptService.getRefAttributePerType(getCurrentContextId(), lang, AttributeType.S);
	}

	public List<TabularConceptCciValidationSetReportModel> getCciValidationSets(long tabularId, Language lang) {
		TabularConcept tabular = getTabularById(tabularId);
		Collection<CciValidation> validations = ((CciTabular) tabular).getValidations();
		if (validations.isEmpty()) {
			return Collections.emptyList();
		} else {
			Map<String, IdCodeDescription> locationMap = toCodeMap(getCciLocationReferences(lang));
			Map<String, IdCodeDescription> extentMap = toCodeMap(getCciExtentReferences(lang));
			Map<String, IdCodeDescription> statusMap = toCodeMap(getCciStatusReferences(lang));
			Map<String, IdCodeDescription> modeMap = toCodeMap(getCciModeOfDeliveryReferences(lang));
			List<TabularConceptCciValidationSetReportModel> list = new ArrayList<TabularConceptCciValidationSetReportModel>();
			for (CciValidation v : validations) {
				if (v.isActive()) {
					CciValidationXml xml = XmlUtils.deserialize(CciValidationXml.class, v.getValidationDefinition());
					if (xml != null) {
						TabularConceptCciValidationSetReportModel m = new TabularConceptCciValidationSetReportModel();
						m.setDataHolding(v.getFacilityType().getDescription(lang.getCode()));
						m.setGender(xml.getGenderDescription(lang));
						m.setAgeRange(xml.getAgeRange());
						String locationCode = xml.getLocationReferenceCode();
						if (!isEmpty(locationCode)) {
							if (locationCode.startsWith("L")) {
								IdCodeDescription idCodeDescription = locationMap.get(locationCode);
								if (idCodeDescription == null) {
									throw new CIMSException("Unknown CCI Location Reference code: " + locationCode);
								}
								m.setLocationReference(locationCode + " " + idCodeDescription.getDescription());
							} else {
								IdCodeDescription idCodeDescription = modeMap.get(locationCode);
								if (idCodeDescription == null) {
									throw new CIMSException(
											"Unknown CCI Mode Of Delivery Reference code: " + locationCode);
								}
								m.setModeOfDeliveryReference(locationCode + " " + idCodeDescription.getDescription());
							}
						}
						String extentReferenceCode = xml.getExtentReferenceCode();
						if (!isEmpty(extentReferenceCode)) {
							IdCodeDescription extentCodeDescription = extentMap.get(extentReferenceCode);
							if (extentCodeDescription == null) {
								throw new CIMSException("Unknown CCI Extent Reference code: " + extentReferenceCode);
							}
							m.setExtentReference(extentReferenceCode + " " + extentCodeDescription.getDescription());
						}
						String statusReferenceCode = xml.getStatusReferenceCode();
						if (!isEmpty(statusReferenceCode)) {
							IdCodeDescription statusCodeDescription = statusMap.get(statusReferenceCode);
							if (statusCodeDescription == null) {
								throw new CIMSException("Unknown CCI Status Reference code: " + statusReferenceCode);
							}
							m.setStatusReference(statusReferenceCode + " " + statusCodeDescription.getDescription());
						}
						list.add(m);
					}
				}
			}
			return list;
		}
	}

	public ChangeRequestPermission getChangeRequestClassificationPermission(User user, ChangeRequestCategory category) {
		return accessService.getChangeRequestClassificationPermission(user, getCurrentChangeRequestId(), category);
	}

	public Set<Language> getChangeRequestLanguages() {
		ChangeRequest request = getCurrentChangeRequest();
		return request.getLanguages();
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public long getChangeRequestTimestamp() {
		ChangeRequest request = getCurrentChangeRequest();
		return request.getLastUpdatedTime().getTime();
	}

	// RU009
	public ChangeRequestPermission getConceptInfoPermission(User user, ChangeRequestCategory category) {
		ChangeRequestPermission p = getChangeRequestClassificationPermission(user, category);
		return new ChangeRequestPermissionWrapper(p) {
			@Override
			public boolean isCanAdd() {
				return isCanDelete();
			}

			@Override
			public boolean isCanDelete() {
				return DEBUG_ALLOW_ALL || (isVersionYear() && super.isCanDelete());
			}
		};
	}

	public ChangeRequestPermission getConceptNonInfoPermission(User user) {
		ChangeRequestPermission p = accessService.getChangeRequestClassificationPermission(user,
				getCurrentChangeRequestId(), ChangeRequestCategory.T);
		return new ChangeRequestPermissionWrapper(p) {

			@Override
			public boolean isCanAdd() {
				return isCanWrite();
			}

			@Override
			public boolean isCanDelete() {
				return isCanWrite();
			}

			@Override
			public boolean isCanWrite() {
				return DEBUG_ALLOW_ALL || (isVersionYear() && super.isCanWrite());
			}

			@Override
			public boolean isCanWrite(Language language) {
				return DEBUG_ALLOW_ALL || (isVersionYear() && super.isCanWrite(language));
			}
		};
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public TransformationService getConceptTransformationService() {
		return conceptTransformationService;
	}

	public Long getContainedPageId(long elemenId) {
		ContextAccess access = getContexAccess();
		Long containingPageId = access.determineContainingPage(elemenId);
		return containingPageId;
	}

	private TabularConcept getContainingPage(TabularConcept tabular) {
		TabularConceptType type = toSubType(tabular);
		if (type.getClassification() == ICD) {
			return ((IcdTabular) tabular).getContainingPage();
		} else {
			return ((CciTabular) tabular).getContainingPage();
		}
	}

	@Deprecated
	private String getContainingPageCode(long elemenId) {
		Long containingPageId = getContainedPageId(elemenId);
		TabularConceptModel model = getTabularConceptLightById(containingPageId);
		return model.getCode();
	}

	private ContextAccess getContexAccess() {
		ContextAccess contextAccess = context.context();
		assert contextAccess != null;
		if (log.isDebugEnabled()) {
			log.debug("Current context: " + contextAccess.getContextId());
		}
		return contextAccess;
	}

	public CurrentContext getContext() {
		return context;
	}

	public ContextOperations getContextOperations() {
		return contextOperations;
	}

	public String getCurrentBaseClassification() {
		return getContexAccess().getContextId().getBaseClassification();
	}

	private ChangeRequest getCurrentChangeRequest() {
		Long requestId = getCurrentChangeRequestId();
		if (requestId == null) {
			throw new CIMSException("No current change request");
		} else {
			return changeRequestService.findLightWeightChangeRequestById(requestId);
		}
	}

	public long getCurrentChangeRequestId() {
		ContextIdentifier contextId = getContexAccess().getContextId();
		return contextId.getRequestId();
	}

	public int getCurrentChangeRequestYear() {
		return Integer.parseInt(getCurrentChangeRequest().getBaseVersionCode());
	}

	public Classification getCurrentClassification() {
		return Classification.fromBaseClassification(getCurrentBaseClassification().substring(0, 3));
	}

	public long getCurrentContextId() {
		ContextIdentifier contextId = getContexAccess().getContextId();
		return contextId.getContextId();
	}

	public String getCurrentVersionCode() {
		return getContexAccess().getContextId().getVersionCode();
	}

	private DaggerAsterisk getDaggerAsterisk(Long id) {
		return getById(id);
	}

	private Long getDaggerAsteriskStarId() {
		if ((daggerAsteriskStartId == null) && (getCurrentClassification() == ICD)) {
			synchronized (this) {
				if (daggerAsteriskStartId == null) {
					for (DaggerAsterisk asterisk : getDaggerAsteriskTypes()) {
						if (asterisk.getCode().equals(ASTERISC)) {
							daggerAsteriskStartId = asterisk.getElementId();
							break;
						}
					}
				}
			}
		}
		return daggerAsteriskStartId;
	}

	public List<DaggerAsterisk> getDaggerAsteriskTypes() {
		return getContexAccess().findList(ref(DaggerAsterisk.class));
	}

	public List<TabularConceptValidationDadHoldingModel> getDataHoldings(Language lang) {
		List<FacilityType> facilities = getContexAccess().findList(ref(FacilityType.class));
		List<TabularConceptValidationDadHoldingModel> list = new ArrayList<TabularConceptValidationDadHoldingModel>();
		for (FacilityType ft : facilities) {
			TabularConceptValidationDadHoldingModel model = new TabularConceptValidationDadHoldingModel();
			model.setCode(ft.getCode());
			model.setElementId(ft.getElementId());
			model.setTitle(ft.getDescription(lang.getCode()));
			list.add(model);
		}
		return list;
	}

	private String getDefinitionXml(ValidationXml v) {
		String xml = XmlUtils.serialize(v);
		xml = replace(xml, " standalone=\"yes\"", "");
		xml = replace(xml, "<validation ", DOCTYPE_VALIDATION_DTD + "<validation ");
		return xml;
	}

	private Long getDxTypeId(IcdValidationXml xml) {
		for (DxType type : dxTypes) {
			if (type.getMain().equals(xml.getMRDxMain()) && type.getT1().equals(xml.getDxType1())
					&& type.getT2().equals(xml.getDxType2()) && type.getT3().equals(xml.getDxType3())
					&& type.getT4().equals(xml.getDxType4()) && type.getT6().equals(xml.getDxType6())
					&& type.getT9().equals(xml.getDxType9()) && type.getW().equals(xml.getDxTypeW())
					&& type.getX().equals(xml.getDxTypeX()) && type.getY().equals(xml.getDxTypeY())) {
				return type.getId();
			}
		}
		return null;
	}

	public ElementOperations getElementOperations() {
		return elementOperations;
	}

	public SexValidation getGenderByCode(String code) {
		Ref<SexValidation> sex = ref(SexValidation.class);
		return getContexAccess().findOne(sex, sex.eq("code", code));
	}

	public List<TabularConceptValidationGenderModel> getGenders(Language lang) {
		List<SexValidation> validations = getContexAccess().findList(ref(SexValidation.class));
		List<TabularConceptValidationGenderModel> list = new ArrayList<TabularConceptValidationGenderModel>();
		for (SexValidation sv : validations) {
			TabularConceptValidationGenderModel model = new TabularConceptValidationGenderModel();
			model.setCode(sv.getCode());
			model.setElementId(sv.getElementId());
			model.setTitle(sv.getDescription(lang.getCode()));
			list.add(model);
		}
		return list;
	}

	public DxType getIcdDxType(long dxTypeId, Language lang) {
		for (DxType type : dxTypes) {
			if (type.getId() == dxTypeId) {
				return type;
			}
		}
		return null;
	}

	public List<DxType> getIcdDxTypes(Language lang) {
		return dxTypes;
	}

	public List<TabularConceptIcdValidationSetReportModel> getIcdValidationSets(long tabularId, Language lang) {
		TabularConcept tabular = getTabularById(tabularId);
		checkValidationSupported(tabular);
		List<TabularConceptIcdValidationSetReportModel> list = new ArrayList<TabularConceptIcdValidationSetReportModel>();
		for (IcdValidation v : ((IcdTabular) tabular).getValidations()) {
			if (v.isActive()) {
				IcdValidationXml xml = XmlUtils.deserialize(IcdValidationXml.class, v.getValidationDefinition());
				if (xml != null) {
					TabularConceptIcdValidationSetReportModel m = new TabularConceptIcdValidationSetReportModel();
					m.setDataHolding(v.getFacilityType().getDescription(lang.getCode()));
					m.setGender(xml.getGenderDescription(lang));
					m.setMaximumAge(xml.getAgeMax());
					m.setMinimumAge(xml.getAgeMin());
					m.setMRDxMain(xml.getMRDxMain());
					m.setDxType1(xml.getDxType1());
					m.setDxType2(xml.getDxType2());
					m.setDxType3(xml.getDxType3());
					m.setDxType4(xml.getDxType4());
					m.setDxType6(xml.getDxType6());
					m.setDxType9(xml.getDxType9());
					m.setDxTypeW(xml.getDxTypeW());
					m.setDxTypeX(xml.getDxTypeX());
					m.setDxTypeY(xml.getDxTypeY());
					m.setNewBorn(xml.getNewBorn());
					list.add(m);
				}
			}
		}
		return list;
	}

	// FIXME: only index term supported
	private Index getIndex(long id) {
		Object concept = getById(id);
		if (concept == null) {
			return null;
		} else if ((concept instanceof IndexTerm) || (concept instanceof IcdIndexNeoplasm)
				|| (concept instanceof BookIndex) || (concept instanceof LetterIndex)) {
			return (Index) concept;
		} else {
			throw new UnsupportedElementExeption("Unsupported type: " + concept.getClass());
		}
	}

	public IndexModel getIndexById(long id, Language lang) {
		Index idx = getIndex(id);
		if (idx == null) {
			return null;
		} else {
			return toModel(idx, lang, false);
		}
	}

	public List<SiteIndicatorModel> getIndexSiteIndicators(Language lang) {
		List<SiteIndicatorModel> list = new ArrayList<SiteIndicatorModel>();
		list.add(new SiteIndicatorModel("$", "$"));
		return list;
	}

	public TransformIndexService getIndexTransformationService() {
		return indexTransformationService;
	}

	@SuppressWarnings("unchecked")
	private <T extends IndexBaseXml> T getIndexXml(Index idx, IndexType type, Language lang) {
		Class<?> clazz = type.getXmlClass();
		if (clazz == null) {
			return null;
		} else {
			String xml = idx.getIndexRefDefinition(lang.getCode());
			xml = SpecialCharactersUtils.replace(xml);
			return (T) XmlUtils.deserialize(clazz, xml);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends IndexBaseXml> T getIndexXmlOrNew(Index idx, IndexType type, Language lang) {
		T i = getIndexXml(idx, type, lang);
		if (i == null) {
			Class<?> clazz = type.getXmlClass();
			if (clazz != null) {
				try {
					return (T) clazz.newInstance();
				} catch (Exception e) {
					throw new CIMSException(e);
				}
			}
		}
		return i;
	}

	private CciInvasivenessLevel getInvasivenessLevelbyId(Long id) {
		return getById(id);
	}

	private String getLongTitle(Language lang, CciComponent... components) {
		StringBuilder b = new StringBuilder();
		for (CciComponent c : components) {
			if (c != null) {
				if (b.length() != 0) {
					b.append(" ");
				}
				b.append(c.longTitle(lang));
			}
		}
		return b.toString();
	}

	public String getNodeTitle(long elementId, String language) {
		ContextIdentifier context = getContexAccess().getContextId();
		return viewService.getTitleForNode(elementId + "", context.getBaseClassification(), context.getContextId(),
				language);
	}

	public NonContextOperations getNonContextOperations() {
		return nonContextOperations;
	}

	private TabularConcept getParent(TabularConcept tabular) {
		TabularConceptType type = toSubType(tabular);
		BaseConcept parent = null;
		if (type.getClassification() == ICD) {
			parent = ((IcdTabular) tabular).getParent();
		} else {
			parent = ((CciTabular) tabular).getParent();
		}
		return parent instanceof TabularConcept ? (TabularConcept) parent : null;
	}

	RootConcept getRootConcept() {
		return getContexAccess().findOne(ref(RootConcept.class));
	}

	private String getShortTitle(Language lang, CciComponent... components) {
		StringBuilder b = new StringBuilder();
		for (CciComponent c : components) {
			if (c != null) {
				if (b.length() != 0) {
					b.append(" ");
				}
				b.append(c.shortTitle(lang));
			}
		}
		return b.toString();
	}

	public List<IdCodeDescription> getSortedSectionApproachTechniqueComponents(long sectionElementId,
			Language language) {
		return getCciComponentsPerSection(sectionElementId, language, CciComponentType.ApproachTechnique);
	}

	public List<IdCodeDescription> getSortedSectionDeviceAgentComponents(long sectionElementId, Language language) {
		List<IdCodeDescription> deviceAgents = getCciComponentsPerSection(sectionElementId, language,
				CciComponentType.DeviceAgent);

		return deviceAgents.stream().filter(d -> !"XX - DO NOT USE".equals(d.getCodeDescription())).collect(toList());
	}

	public List<IdCodeDescription> getSortedSectionGroupComponents(long sectionId, Language language) {
		return getCciComponentsPerSection(sectionId, language, CciComponentType.GroupComp);
	}

	public List<IdCodeDescription> getSortedSectionInterventionComponents(long sectionElementId, Language language) {
		return getCciComponentsPerSection(sectionElementId, language, CciComponentType.Intervention);
	}

	public List<IdCodeDescription> getSortedSectionTissueComponents(long sectionElementId, Language language) {
		return getCciComponentsPerSection(sectionElementId, language, CciComponentType.Tissue);
	}

	/**
	 * Add sorting string for sorting all category reference later on in XSLT. multiple CATEGORY_REFERENCE_DESC should
	 * be sorted in this code order: [morphology | dagger | regular | asterisk]
	 */
	private String getSortingString(String mainCode, String mainDaggerString, boolean paired) {
		if (paired) {
			return INDEX_CATEGORY_REFERENCE_SORT_STRING_A + mainCode;
		} else {
			if (isEmpty(mainDaggerString)) {
				return INDEX_CATEGORY_REFERENCE_SORT_STRING_C + mainCode;
			} else if (DAGGER.equalsIgnoreCase(mainDaggerString)) {
				return INDEX_CATEGORY_REFERENCE_SORT_STRING_B + mainCode;
			} else {
				return INDEX_CATEGORY_REFERENCE_SORT_STRING_Z + mainCode;
			}
		}
	}

	private Supplement getSupplement(long id) {
		Object concept = getById(id);
		if (concept == null) {
			return null;
		} else {
			if (concept instanceof RootConcept) {
				throw new RootElementExeption("");
			} else {
				return (Supplement) concept;
			}
		}
	}

	public SupplementModel getSupplementById(long id, Language lang) {
		Supplement idx = getSupplement(id);
		if (idx == null) {
			return null;
		} else {
			return toModel(idx, lang, false);
		}
	}

	// FIXME: performance
	@Deprecated
	private SupplementType getSupplementType(SupplementMatter matter) {
		Iterator<SupplementType> iterator = getContexAccess().findAll(SupplementType.class);
		while (iterator.hasNext()) {
			SupplementType type = iterator.next();
			if (type.getCode().equals(matter.getCode())) {
				return type;
			}
		}
		return null;
	}

	protected TabularConcept getTabularByCode(String code) {
		if (getCurrentClassification() == ICD) {
			Ref<IcdTabular> icd = ref(IcdTabular.class);
			IcdTabular findOne = getContexAccess().findOne(icd, icd.eq("code", code));
			return findOne;
		} else {
			Ref<CciTabular> icd = ref(CciTabular.class);
			CciTabular findOne = getContexAccess().findOne(icd, icd.eq("code", code));
			return findOne;
		}
	}

	public TabularConceptModel getTabularById(long id, boolean loadParent) {
		TabularConceptDetails tab = getTabularConceptDetails(id);
		if (tab == null) {
			return null;
		} else if (tab.getClassName().equalsIgnoreCase("ClassificationRoot")) {
			throw new RootElementExeption("");
		} else if (tab.getClassName().contains("Index") || tab.getClassName().contains("Supplement")) {
			throw new UnsupportedElementExeption();
		} else {
			return toModel(tab, loadParent);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends TabularConcept> T getTabularById(Long id) {
		Object concept = getById(id);
		if (concept == null) {
			return null;
		} else if (concept instanceof TabularConcept) {
			return (T) concept;
		} else if (concept instanceof RootConcept) {
			throw new RootElementExeption("");
		} else {
			throw new UnsupportedElementExeption("Unsupported type: " + concept.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends TabularConcept> T getTabularbyIdNotNull(Long id) {
		T tabular = (T) getTabularById(id);
		if (tabular == null) {
			throw new CIMSException("Entity is not found");
		}
		return tabular;
	}

	// FIXME: get by "code" optimised
	@Deprecated
	public TabularConceptModel getTabularConceptByCode(String code) {
		TabularConcept tab = getTabularByCode(code);
		return tab == null ? null : getTabularConceptById(tab.getElementId());
	}

	public TabularConceptModel getTabularConceptById(long id) {
		return getTabularConceptById(id, true);
	}

	public TabularConceptModel getTabularConceptById(long id, boolean loadParent) {
		return getTabularById(id, loadParent);
	}

	private TabularConceptDetails getTabularConceptDetails(long elementId) {
		StopWatch stopWatch = new StopWatch("getTabularConceptDetails:" + elementId);
		stopWatch.start("getTabularConceptDetails.getContextId");
		ContextIdentifier contextId = getContexAccess().getContextId();
		stopWatch.stop();
		log.error(stopWatch);
		TabularConceptDetails concept = viewService.getTabularConceptDetails(contextId.getContextId(), elementId,
				contextId.getBaseClassification());
		return concept;
	}

	public TabularConceptModel getTabularConceptLightById(long elementId) {
		TabularConceptDetails tabular = getTabularConceptDetails(elementId);
		return toLightModel(tabular);
	}

	public byte[] getTabularDiagramContent(long id, Language lang) {
		TabularConcept tabular = getTabularById(id);
		return tabular.diagram(lang);
	}

	private Validation getTabularValidation(TabularConcept tabular, long dataHoldingId) {
		Collection<? extends Validation> validations = getValidations(tabular);
		Validation found = null;
		for (Validation val : validations) {
			if (val.getFacilityType().getElementId() == dataHoldingId) {
				found = val;
				break;
			}
		}
		return found;
	}

	@SuppressWarnings("unchecked")
	public <T extends TabularConceptValidationSetModel> T getTabularValidationSet(long elementId, long dataHoldingId) {
		TabularConcept tabular = getTabularById(elementId);
		if (tabular == null) {
			return null;
		} else {
			if (tabular instanceof CciTabular) {
				for (CciValidation v : ((CciTabular) tabular).getValidations()) {
					if (v.getFacilityType().getElementId().equals(dataHoldingId)) {
						TabularConceptCciValidationSetModel m = new TabularConceptCciValidationSetModel();
						m.setElementId(dataHoldingId);
						m.setDisabled(!v.isActive());
						if (v.isActive()) {
							CciValidationXml xml = XmlUtils.deserialize(CciValidationXml.class,
									v.getValidationDefinition());
							if (xml != null) {
								m.setGenderCode(xml.getGenderCode());
								m.setAgeMaximum(xml.getAgeMax());
								m.setAgeMinimum(xml.getAgeMin());
								if (xml.getLocationReferenceCode().startsWith("L")) {
									m.setLocationReferenceCode(xml.getLocationReferenceCode());
								} else {
									m.setDeliveryReferenceCode(xml.getLocationReferenceCode());
								}
								m.setExtentReferenceCode(xml.getExtentReferenceCode());
								m.setStatusReferenceCode(xml.getStatusReferenceCode());
							}
						}
						return (T) m;
					}
				}
				TabularConceptCciValidationSetModel m = new TabularConceptCciValidationSetModel();
				m.setElementId(dataHoldingId);
				m.setDisabled(true);
				return (T) m;
			} else {
				for (IcdValidation v : ((IcdTabular) tabular).getValidations()) {
					if (v.getFacilityType().getElementId().equals(dataHoldingId)) {
						TabularConceptIcdValidationSetModel m = new TabularConceptIcdValidationSetModel();
						m.setElementId(dataHoldingId);
						m.setDisabled(!v.isActive());
						if (v.isActive()) {
							IcdValidationXml xml = XmlUtils.deserialize(IcdValidationXml.class,
									v.getValidationDefinition());
							if (xml != null) {
								m.setGenderCode(xml.getGenderCode());
								m.setAgeMaximum(xml.getAgeMax());
								m.setAgeMinimum(xml.getAgeMin());
								m.setDxTypeId(getDxTypeId(xml));
								m.setNewBorn("Y".equals(xml.getNewBorn()));
							}
						}
						return (T) m;
					}
				}
				TabularConceptIcdValidationSetModel m = new TabularConceptIcdValidationSetModel();
				m.setElementId(dataHoldingId);
				m.setDisabled(true);
				return (T) m;
			}
		}
	}

	public TabularConceptXmlModel getTabularXml(long elementId, TabularConceptXmlType type) {
		TabularConcept concept = getTabularById(elementId);
		TabularConceptXmlModel model = new TabularConceptXmlModel();
		model.setType(type);
		model.setCode(concept.getCode());
		TabularConcept parent = getParent(concept);
		if (parent != null) {
			model.setParentCode(parent.getCode());
		}
		model.setElementId(elementId);
		model.setRawConcept(concept);
		model.setConceptType(toSubType(concept));
		model.setEnglishXml(getTabularXml(concept, type, Language.ENGLISH));
		model.setFrenchXml(getTabularXml(concept, type, Language.FRENCH));
		return model;
	}

	private String getTabularXml(TabularConcept concept, TabularConceptXmlType type, Language language) {
		return SpecialCharactersUtils.replace(getTabularXmlInternal(concept, type, language));
	}

	private String getTabularXmlInternal(TabularConcept concept, TabularConceptXmlType type, Language language) {
		switch (type) {
		case NOTE:
			return concept.getNote(language.getCode());
		case TABLE:
			checkConceptIsRubricOrCategory(concept);
			return concept.getTableOutput(language.getCode());
		case CODE_ALSO:
			return concept.getCodeAlsoXml(language.getCode());
		case EXCLUDE:
			return concept.getExcludeXml(language.getCode());
		case INCLUDE:
			return concept.getIncludeXml(language.getCode());
		case OMIT_CODE:
			return ((CciTabular) concept).getOmitCodeXml(language.getCode());
		case DEFINITION:
			return ((IcdTabular) concept).getDefinitionXml(language.getCode());
		default:
			throw new CIMSException("Unsupported xml type: " + type);
		}
	}

	public TransformSupplementService getTransformSupplementService() {
		return transformSupplementService;
	}

	private Collection<? extends Validation> getValidations(TabularConcept tabular) {
		Collection<? extends Validation> validations = tabular instanceof IcdTabular
				? ((IcdTabular) tabular).getValidations() : ((CciTabular) tabular).getValidations();
		return validations;
	}

	public ViewService getViewService() {
		return viewService;
	}

	public boolean hasChildren(long conceptId, Language lang) {
		List<ContentViewerModel> children = viewService.getTreeNodes(conceptId + "", getCurrentBaseClassification(),
				getCurrentContextId(), lang.getCode(), null);
		return !children.isEmpty();
	}

	public boolean isAddedInCurrentVersionYear(IndexModel model) {
		return !contextOperations.hasConceptBeenPublished(model.getElementId());
	}

	public boolean isAddedInCurrentVersionYear(SupplementModel model) {
		return !contextOperations.hasConceptBeenPublished(model.getElementId());
	}

	public boolean isAddQualifierEnabled(TabularConceptType type) {
		return (type == CCI_GROUP) || (type == CCI_RUBRIC) || (type == CCI_CCICODE);
	}

	public boolean isCanadianEnhancementEditable(TabularConceptModel model) {
		if (isVersionYear()) {
			return model.isIcdCategory();
		} else {
			return false;
		}
	}

	public boolean isDaggerAsteriskEditable(TabularConceptModel model) {
		if (isVersionYear()) {
			return model.isIcdCategory();
		} else {
			return false;
		}
	}

	private boolean isElementStatusEditable(long elementId) {
		if (isVersionYear()) {
			return contextOperations.hasConceptBeenPublished(elementId);
		} else {
			return false;
		}
	}

	/** Performs high-level check only */
	public boolean isIndexDeletableShallow(IndexModel model) {
		return isIndexEditableShallow(model)
				&& !((model.getType() == IndexType.ICD_BOOK_INDEX) || (model.getType() == IndexType.CCI_BOOK_INDEX));
	}

	/** Performs high-level check only */
	public boolean isIndexEditableShallow(IndexModel model) {
		return !((model.getType() == IndexType.ICD_LETTER_INDEX) || (model.getType() == IndexType.CCI_LETTER_INDEX));
	}

	public boolean isIndexStatusEditable(IndexModel model) {
		return !((model.getType() == IndexType.ICD_BOOK_INDEX) || (model.getType() == IndexType.ICD_LETTER_INDEX)
				|| (model.getType() == IndexType.CCI_BOOK_INDEX) || (model.getType() == IndexType.CCI_LETTER_INDEX))//
				&& isElementStatusEditable(model.getElementId());
	}

	/** Performs high-level check only */
	public boolean isSupplementDeletableShallow(SupplementModel model) {
		return isSupplementEditableShallow(model);
	}

	/** Performs high-level check only */
	public boolean isSupplementEditableShallow(SupplementModel model) {
		return true;
	}

	public boolean isSupplementStatusEditable(SupplementModel model) {
		return isElementStatusEditable(model.getElementId());
	}

	/** For Blocks: Value is editable **/
	public boolean isTabularCodeEditable(TabularConceptModel model) {
		if (isVersionYear()) {
			return model.isIcdBlock() || model.isCciBlock();
		} else {
			return false;
		}
	}

	public boolean isTabularStatusEditable(TabularConceptModel model) {
		if (isVersionYear()) {
			return model.isAddedInPreviouseVersionYear();
		} else {
			return false;
		}
	}

	public boolean isUserTitleEditable(TabularConceptModel model) {
		return isVersionYear();
	}

	private boolean isValidLeadTermFirstLetter(String childDescription, String parentDescription) {
		String parentFirstLetter = parentDescription.substring(0, 1).toLowerCase();
		String termFirstLetter = childDescription.substring(0, 1).toLowerCase();
		if (parentFirstLetter.equals(termFirstLetter)) {
			return true;
		} else {
			// http://webdesign.about.com/od/localization/l/blhtmlcodes-fr.htm
			int charCode = termFirstLetter.codePointAt(0);
			if ((charCode == 224) || (charCode == 226) || (charCode == 230)) {
				return parentFirstLetter.equals("a");
			} else if (charCode == 231) {
				return parentFirstLetter.equals("c");
			} else if ((charCode == 232) || (charCode == 233) || (charCode == 234) || (charCode == 235)
					|| (charCode == 128)) {
				return parentFirstLetter.equals("e");
			} else if ((charCode == 238) || (charCode == 239)) {
				return parentFirstLetter.equals("i");
			} else if ((charCode == 244) || (charCode == 156)) {
				return parentFirstLetter.equals("o");
			} else if ((charCode == 249) || (charCode == 251) || (charCode == 252)) {
				return parentFirstLetter.equals("u");
			} else if (charCode == 8355) {
				return parentFirstLetter.equals("f");
			} else if (StringUtils.isNumeric(termFirstLetter)) {
				return parentFirstLetter.equals("a");
			} else {
				return false;
			}
		}
	}

	public boolean isVersionYear() {
		ContextIdentifier contextId = getContexAccess().getContextId();
		boolean versionYear = DEBUG_ALLOW_ALL || contextId.isVersionYear();
		return versionYear;
	}

	private void rejectIfNotEquals(ErrorBuilder result, String field, Object before, Object after) {
		if (!ObjectUtils.equals(before, after)) {
			result.rejectValue(field, MODIFICATION_NOT_ALLOWED);
		}
	}

	@Transactional
	public void saveIndex(OptimisticLock lock, ErrorBuilder result, User user, IndexModel model, Language lang) {
		checkBasicInfoEditAllowed(user, "Index", ChangeRequestCategory.I);
		if (!isIndexEditableShallow(model)) {
			throw new CIMSException("Update is not allowed");
		}
		validateJsr(result, model);
		validateXml(result, TabularConceptXmlType.NOTE, "note", model.getNote());
		if (!result.hasErrors()) {
			ContextAccess contexAccess = getContexAccess();
			Index idx = model.getEntity() == null ? getIndex(model.getElementId()) : model.getEntity();
			if (!StringUtils.equals(idx.getStatus(), model.getStatus().name())) {
				if (!isElementStatusEditable(model.getElementId())) {
					result.rejectValue("status", VALUE_NOT_ALLOWED);
					return;
				}
			}
			if (model.getType().isTerm() && (model.getLevel() == 1)
					&& (model.getType() != IndexType.ICD_NEOPLASM_INDEX)) {
				Index parent = idx.getParent();
				if (!isValidLeadTermFirstLetter(model.getDescription(), parent.getDescription())) {
					throw new CIMSException(
							"The lead term description must have the same 1st letter as its Alphabetic parent");
				}
			}
			if (!StringUtils.equals(idx.getStatus(), model.getStatus().name())) {
				if (!isIndexStatusEditable(model)) {
					result.rejectValue("status", VALUE_NOT_ALLOWED);
					return;
				}
			}
			idx.setDescription(model.getDescription());
			idx.setStatus(model.getStatus().name());
			// save index references
			if (model.getType().isTerm()) {
				String currentBaseClassification = getCurrentBaseClassification();
				IndexTerm term = (IndexTerm) idx;
				if (!StringUtils.equals( //
						StringUtils.trimToNull(term.getNoteDescription(lang.getCode())), //
						StringUtils.trimToNull(model.getNote()) //
				)) {
					term.setNoteDescription(lang.getCode(), model.getNote());
				}
				IndexBaseXml xml = getIndexXmlOrNew(idx, model.getType(), lang);
				xml.setLanguage(lang.getCode());
				xml.setClassification(currentBaseClassification);
				xml.setIndexType("INDEX_TERM");
				xml.setElementId(idx.getElementId());
				xml.setLevelNum(term.getNestingLevel());
				xml.setSeeAlsoFlag(model.isSeeAlso() ? "Y" : "N");
				ReferenceListXml referenceList = xml.getReferenceList();
				if (referenceList == null) {
					referenceList = new ReferenceListXml();
					xml.setReferenceList(referenceList);
				}
				long currentContextId = getCurrentContextId();
				if (CollectionUtils.isEmpty(model.getIndexReferences())) {
					referenceList.setIndexReferenceList(null);
				} else {
					Set<Long> set = new HashSet<Long>();
					for (IndexTermReferenceModel ref : model.getIndexReferences()) {
						if (StringUtils.isBlank(ref.getCustomDescription())) {
							throw new CIMSException("Custom description cannot be blank");
						}
						if (set.contains(ref.getElementId())) {
							throw new CIMSException("Index reference with description [" + ref.getCustomDescription()
									+ "] is duplicate");
						}
						set.add(ref.getElementId());
					}
					List<IndexReferenceXml> list = new ArrayList<IndexReferenceXml>();
					for (IndexTermReferenceModel ref : model.getIndexReferences()) {
						long elementId = ref.getElementId();
						IndexReferenceXml x = new IndexReferenceXml();
						IndexModel indexById = getIndexById(elementId, lang);
						if (indexById == null) {
							throw new CIMSException("Reference Index Term Custom Description ["
									+ ref.getCustomDescription() + "] is entered without a Reference Index Term");
						}
						x.setContainerIndexIdPath(elementOperations.determineContainingIdPath(currentBaseClassification,
								currentContextId, elementId));
						x.setReferenceLinkDescription(ref.getCustomDescription());
						list.add(x);
					}
					referenceList.setIndexReferenceList(list.isEmpty() ? null : list);
				}
				if (CollectionUtils.isEmpty(model.getCategoryReferences())) {
					referenceList.setCategoryReferenceList(null);
				} else {
					// if ICD section 2 -> non-paired
					// if ICD section 1 -> paired + non-paired
					// if CCI - all ->
					Set<String> paires = new HashSet<String>();
					for (IndexCategoryReferenceModel m : model.getCategoryReferences()) {
						if (StringUtils.isBlank(m.getMainCode())) {
							throw new CIMSException("Main Code Value cannot be blank");
						}
						if (StringUtils.isBlank(m.getMainCustomDescription())) {
							throw new CIMSException("Main Custom description cannot be blank");
						}
						if (model.isIcd1()) {
							if (!isEmpty(m.getMainDaggerAsterisk()) && (m.getPairedElementId() == 0)) {
								throw new CIMSException("Only non-D/A codes may be unpaired");
							}
						}
						if (m.getPairedElementId() != 0) {
							if (StringUtils.isBlank(m.getPairedCustomDescription())) {
								throw new CIMSException("Paired custom description cannot be blank");
							}
						}
						if (StringUtils.isBlank(m.getPairedCode())
								&& !StringUtils.isBlank(m.getPairedCustomDescription())) {
							throw new CIMSException(
									"Paired Code Value cannot be blank if a custom description is entered");
						}
						// CM-RU169
						if (m.getPairedElementId() != 0) {
							if (!((DAGGER.equals(m.getMainDaggerAsterisk())
									&& ASTERISC.equals(m.getPairedDaggerAsterisk()) //
							) || (isEmpty(m.getMainDaggerAsterisk()) && ASTERISC.equals(m.getPairedDaggerAsterisk()) //
							) || (isEmpty(m.getMainDaggerAsterisk()) && isEmpty(m.getPairedDaggerAsterisk()) //
							))) {
								throw new CIMSException(
										"Only the following dagger asterisk combinations are valid: (dagger/asterisk), (non-DA/asterisk) and (non DA/non DA)");
							}
						}
						if (m.getMainElementId() == m.getPairedElementId()) {
							// TODO: add validation error
							throw new CIMSException("A code value cannot pair up with itself: " + m.getMainCode());
						}
						String pair = m.getMainElementId() + "-" + m.getPairedElementId();
						if (!paires.add(pair)) {
							// TODO: add validation error
							throw new CIMSException("Code value references are not unique: " + m.getMainCode());
						}
					}
					List<CategoryReferenceXml> categories = new ArrayList<CategoryReferenceXml>();
					for (IndexCategoryReferenceModel m : model.getCategoryReferences()) {
						CategoryReferenceXml ref = new CategoryReferenceXml();
						ref.setMainContainerConceptIdPath(elementOperations.determineContainingIdPath(
								currentBaseClassification, currentContextId, m.getMainElementId()));
						ref.setMainCode(m.getMainCode());
						ref.setMainCodePresentation(m.getMainCustomDescription());
						ref.setMainCodeDaggerAsteriskCode(defaultIfEmpty(m.getMainDaggerAsterisk(), ""));
						boolean paired = m.getPairedElementId() != 0;
						if (paired) {
							ref.setPairedContainerConceptIdPath(elementOperations.determineContainingIdPath(
									currentBaseClassification, currentContextId, m.getPairedElementId()));
							ref.setPairedCode(m.getPairedCode());
							ref.setPairedCodePresentation(m.getPairedCustomDescription());
							ref.setPairedCodeDaggerAsteriskCode(defaultIfEmpty(m.getPairedDaggerAsterisk(), ""));
							ref.setPairedFlag("Y");
						} else {
							ref.setPairedFlag("X");
						}
						ref.setSortString(
								getSortingString(ref.getMainCode(), ref.getPairedCodeDaggerAsteriskCode(), paired));
						categories.add(ref);
					}
					referenceList.setCategoryReferenceList(categories.isEmpty() ? null : categories);
				}
				if (referenceList.getIndexReferenceList() == null) {
					// <XML compatibility>
					xml.setSeeAlsoFlag("");
					// </XML compatibility>
				}
				if (false && (referenceList.getCategoryReferenceList() == null)
						&& (referenceList.getIndexReferenceList() == null)) {
					xml.setReferenceList(null);
				}
				if (model.getType() == IndexType.ICD_NEOPLASM_INDEX) {
					IcdIndexNeoplasmXml neoXml = (IcdIndexNeoplasmXml) xml;
					Map<NeoplasmDetailType, TabularReferenceModel> map = model.getNeoplasmDetails();
					List<TabularRefXml> details = new ArrayList<TabularRefXml>();
					for (NeoplasmDetailType key : NeoplasmDetailType.values()) {
						TabularReferenceModel value = map.get(key);
						details.add(toTabularRefXml(value, key.name()));
					}
					neoXml.setNeoplasmDetail(details);
					xml.setBookIndexType("N");
					xml.setSiteIndicator(defaultIfEmpty(model.getSiteIndicatorCode(), null));
				} else if (model.getType() == IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX) {
					IcdIndexDrugsAndChemicalsXml drugXml = (IcdIndexDrugsAndChemicalsXml) xml;
					Map<DrugDetailType, TabularReferenceModel> map = model.getDrugsDetails();
					List<TabularRefXml> details = new ArrayList<TabularRefXml>();
					for (DrugDetailType key : DrugDetailType.values()) {
						TabularReferenceModel value = map.get(key);
						details.add(toTabularRefXml(value, key.name()));
					}
					drugXml.setDrugsDetail(details);
					xml.setBookIndexType("D");
				} else {
					xml.setBookIndexType("A");
				}
				String xmlStr = XmlUtils.serialize(xml);
				xmlStr = SpecialCharactersUtils.replace(xmlStr);
				String error = dtdValidator.validateDocument("index", CIHI_CIMS_INDEX_DTD, xmlStr);
				if (error != null) {
					throw new CIMSException("Invalid index xml: " + error);
				}
				xmlStr = replaceOnce(xmlStr, "<index ", DOCTYPE_INDEX_DTD + "<index ");
				// <XML compatibility>
				xmlStr = StringUtils.replaceOnce(xmlStr, " standalone=\"yes\"", "");
				xmlStr = StringUtils.replaceOnce(xmlStr, " language=\"" + lang.getCode() + "\"", "");
				xmlStr = StringUtils.replaceOnce(xmlStr, "<index ", "<index language=\"" + lang.getCode() + "\" ");
				xmlStr = StringUtils.replace(xmlStr, "<REFERENCE_LIST/>", "<REFERENCE_LIST></REFERENCE_LIST>");
				xmlStr = StringUtils.replace(xmlStr, "<SEE_ALSO_FLAG></SEE_ALSO_FLAG>",
						"<SEE_ALSO_FLAG>N</SEE_ALSO_FLAG>");
				// </XML compatibility>
				idx.setIndexRefDefinition(lang.getCode(), xmlStr);
			} else if (model.getType().isBook()) {
				BookIndex book = (BookIndex) idx;
				if (!StringUtils.equals( //
						StringUtils.trimToNull(book.getNoteDescription(lang.getCode())), //
						StringUtils.trimToNull(model.getNote()) //
				)) {
					book.setNoteDescription(lang.getCode(), model.getNote());
				}
			}
			contexAccess.persist();
			if (idx.getIndexRefDefinition(lang.getCode()) != null) {
				indexTransformationService.transformIndexConcept(idx, lang.getCode(), contexAccess, false);
			}
			contexAccess.persist();
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	private void saveOrRemoveDiagram(TabularConcept tabular, Language lang, TabularConceptDiagramModel diagram) {
		if (diagram.isRemove()) {
			tabular.diagram(lang, null);
			tabular.diagramFileName(lang, null);
			diagram.setName(null);
		} else {
			MultipartFile file = diagram.getFile();
			if ((file != null) && !file.isEmpty()) {
				diagram.setName(file.getOriginalFilename());
				tabular.diagramFileName(lang, diagram.getName());
				try {
					tabular.diagram(lang, file.getBytes());
				} catch (IOException e) {
					throw new CIMSException(e);
				}
			}
		}
	}

	@Transactional
	public void saveSupplement(OptimisticLock lock, ErrorBuilder result, User user, SupplementModel model,
			Language lang) {
		checkBasicInfoEditAllowed(user, "Supplement", ChangeRequestCategory.S);
		if (!isSupplementEditableShallow(model)) {
			throw new CIMSException("Update is not allowed");
		}
		validateJsr(result, model);
		String markup = StringUtils.defaultIfEmpty(model.getMarkup(), null);
		if (!isEmpty(markup)) {
			validateXml(result, TabularConceptXmlType.SUPPLEMENT, "markup", "<supplement>" + markup + "</supplement>");
		}
		if (!result.hasErrors()) {
			ContextAccess contexAccess = getContexAccess();
			Supplement sup = model.getEntity() == null ? getSupplement(model.getElementId()) : model.getEntity();
			String status = sup.getStatus();
			if (!StringUtils.equals(status, model.getStatus().name())) {
				if (!isSupplementStatusEditable(model)) {
					result.rejectValue("status", VALUE_NOT_ALLOWED);
					return;
				}
			}
			sup.setSupplementDescription(lang.getCode(), model.getDescription());
			sup.setStatus(model.getStatus().name());
			sup.setSortingHint(model.getSortOrder());
			if (model.getLevel() == 1) {
				sup.setSupplementType(getSupplementType(model.getMatter()));
			}
			if (!StringUtils.equals(markup, sup.getSupplementDefinition(lang.getCode()))) {
				sup.setSupplementDefinition(lang.getCode(), markup);
			}
			contexAccess.persist();
			transformSupplementService.transformSupplement(getCurrentBaseClassification(), getCurrentVersionCode(), sup,
					null, contexAccess, false);
			contexAccess.persist();
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	@Transactional
	public void saveTabular(OptimisticLock lock, ErrorBuilder result, User user, TabularConceptModel model) {
		Long elementId = model.getElementId();
		StopWatch w = new StopWatch("saveTabular:" + model.getCode() + ":" + model.getElementId());
		w.start("checkChangeRequestApprovable");
		w.stop();
		w.start("checkConceptEditAllowed");
		checkBasicInfoEditAllowed(user, "Concept", ChangeRequestCategory.T);
		w.stop();
		w.start("getTabularConceptById");
		TabularConceptModel original = getTabularConceptById(elementId, false);
		w.stop();
		w.start("validateModel");
		validateModel(result, model, original);
		w.stop();
		if (!result.hasErrors()) {
			w.start("save");
			boolean codeValueOrUserTitleOrAsteriscChanged = !( //
			StringUtils.equals(model.getCode(), original.getCode()) //
					&& StringUtils.equals(model.getUserTitleEng(), original.getUserTitleEng()) //
					&& StringUtils.equals(model.getUserTitleFra(), original.getUserTitleFra()) //
			);
			if (!codeValueOrUserTitleOrAsteriscChanged
					&& (ObjectUtils.equals(model.getDaggerAsteriskId(), getDaggerAsteriskStarId()) //
							|| ObjectUtils.equals(original.getDaggerAsteriskId(), getDaggerAsteriskStarId()))) {
				codeValueOrUserTitleOrAsteriscChanged = !ObjectUtils.equals(model.getDaggerAsteriskId(),
						original.getDaggerAsteriskId());
			}
			if (!codeValueOrUserTitleOrAsteriscChanged && model.isCciCode()) {
				codeValueOrUserTitleOrAsteriscChanged = !ObjectUtils.equals(model.getStatus(), original.getStatus());
			}
			TabularConcept tabular = getTabularbyIdNotNull(elementId);
			tabular.setCode(model.getCode());
			tabular.setStatus(model.getStatus().name());
			switch (model.getClassification()) {
			case ICD: {
				IcdTabular icdTabular = (IcdTabular) tabular;
				icdTabular.setDaggerAsteriskConcept(getDaggerAsterisk(model.getDaggerAsteriskId()));
				icdTabular.setCanadianEnhancement(model.isCanadianEnhancement());
				break;
			}
			case CCI: {
				CciTabular cciTabular = (CciTabular) tabular;
				if (model.isCciCode()) {
					Long level = model.getInvasivenessLevel();
					cciTabular.setInvasivenessLevel(getInvasivenessLevelbyId(level));
				}
				break;
			}
			default:
				throw new RuntimeException("Save not supported: " + model.getClassification());
			}
			tabular.shortDescription(ENGLISH, model.getShortTitleEng());
			tabular.shortDescription(FRENCH, model.getShortTitleFra());
			tabular.longDescription(ENGLISH, model.getLongTitleEng());
			tabular.longDescription(FRENCH, model.getLongTitleFra());
			tabular.userDescription(ENGLISH, model.getUserTitleEng());
			tabular.userDescription(FRENCH, model.getUserTitleFra());
			saveOrRemoveDiagram(tabular, ENGLISH, model.getDiagramEng());
			saveOrRemoveDiagram(tabular, FRENCH, model.getDiagramFra());
			saveTabular(lock, tabular, user, false, codeValueOrUserTitleOrAsteriscChanged);
			w.stop();
		}
		log.info(w);
	}

	private void saveTabular(OptimisticLock lock, TabularConcept tabular, User user, boolean isNew,
			boolean codeValueOrUserTitleOrAsteriscChanged) {
		StopWatch w = new StopWatch("saveTabular:" + tabular.getCode() + ":" + tabular.getElementId());
		ContextAccess contexAccess = getContexAccess();
		w.start("persist");
		contexAccess.persist();
		w.stop();
		w.start("transformConcept");
		transformConcept(tabular);
		w.stop();
		w.start("transformParentAfterSave");
		transformParentAfterSave(tabular, isNew, codeValueOrUserTitleOrAsteriscChanged);
		w.stop();
		w.start("persist");
		contexAccess.persist();
		updateChangeRequestLastUpdateTime(user, lock);
		w.stop();
		log.info(w);
	}

	@Transactional
	public void saveTabularValidationSet(OptimisticLock lock, ErrorBuilder result, User user, long tabularId,
			TabularConceptValidationSetModel model, List<Long> extendToOtherDataHoldings) {
		checkChangeRequestEditAllowed(user, "Validation Set", ChangeRequestCategory.T);
		List<Long> dataHoldings = new ArrayList<Long>(extendToOtherDataHoldings);
		dataHoldings.add(model.getElementId());
		validateModel(result, model);
		if (!result.hasErrors()) {
			TabularConcept tabular = getTabularById(tabularId);
			checkValidationSupported(tabular);
			for (Long dataHoldingId : dataHoldings) {
				saveTabularValidationSetDataHolding(tabular, dataHoldingId, model);
			}
			model.setDisabled(false);
			getContexAccess().persist();
			TabularConceptType type = toSubType(tabular);
			// http://jira.cihi.ca/browse/CSRE-632
			if (type == CCI_RUBRIC) {
				transformConcept(tabular);
			} else if (type == TabularConceptType.CCI_CCICODE) {
				transformConcept(((CciTabular) tabular).getParent());
			} else if (type == ICD_CATEGORY) {
				IcdTabular icd = (IcdTabular) tabular;
				transformConcept(icd);
				if (icd.getNestingLevel() != 1) {
					while (icd.getNestingLevel() != 1) {
						icd = (IcdTabular) icd.getParent();
					}
					transformConcept(icd);
				}
			}
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	private void saveTabularValidationSetDataHolding(TabularConcept tabular, long dataHoldingId,
			TabularConceptValidationSetModel model) {
		Validation found = getTabularValidation(tabular, dataHoldingId);
		ContextAccess access = getContexAccess();
		if (tabular instanceof CciTabular) {
			if (found == null) {
				found = CciValidation.create(access, (CciTabular) tabular, dataHoldingId);
			}
			CciValidationXml xml = new CciValidationXml();
			copyValidationXml(xml, found, model);
			TabularConceptCciValidationSetModel cciModel = (TabularConceptCciValidationSetModel) model;
			xml.setStatusReferenceCode(cciModel.getStatusReferenceCode());
			xml.setExtentReferenceCode(cciModel.getExtentReferenceCode());
			xml.setLocationReferenceCode(isEmpty(cciModel.getLocationReferenceCode())
					? cciModel.getDeliveryReferenceCode() : cciModel.getLocationReferenceCode());
			found.setValidationDefinition(getDefinitionXml(xml));
		} else {
			if (found == null) {
				found = IcdValidation.create(access, (IcdTabular) tabular, dataHoldingId);
			}
			IcdValidationXml xml = new IcdValidationXml();
			copyValidationXml(xml, found, model);
			TabularConceptIcdValidationSetModel icdModel = (TabularConceptIcdValidationSetModel) model;
			xml.setNewBorn(icdModel.isNewBorn());
			{
				DxType dx = getIcdDxType(icdModel.getDxTypeId(), ENGLISH);
				xml.setMRDxMain(dx.getMain());
				xml.setDxType1(dx.getT1());
				xml.setDxType2(dx.getT2());
				xml.setDxType3(dx.getT3());
				xml.setDxType4(dx.getT4());
				xml.setDxType6(dx.getT6());
				xml.setDxType9(dx.getT9());
				xml.setDxTypeW(dx.getW());
				xml.setDxTypeX(dx.getX());
				xml.setDxTypeY(dx.getY());
			}
			found.setValidationDefinition(getDefinitionXml(xml));
		}
		found.setStatus(ConceptStatus.ACTIVE.name());
	}

	@Transactional
	public void saveTabularXml(OptimisticLock lock, ErrorBuilder result, User user, TabularConceptXmlModel model) {
		ChangeRequestPermission p = checkConceptNonInfoEditAllowed(user, "Concept XML");
		TabularConceptXmlModel original = getTabularXml(model.getElementId(), model.getType());
		validateModel(result, p, model, original);
		if (!result.hasErrors()) {
			saveTabularXml(original.getRawConcept(), model.getType(), Language.ENGLISH, model.getEnglishXml());
			saveTabularXml(original.getRawConcept(), model.getType(), Language.FRENCH, model.getFrenchXml());
			ContextAccess contexAccess = getContexAccess();
			contexAccess.persist();
			transformConcept(original.getRawConcept());
			if (model.getConceptType() == CCI_CCICODE) {
				transformConcept(((CciTabular) original.getRawConcept()).getParent());
			}
			contexAccess.persist();
			updateChangeRequestLastUpdateTime(user, lock);
		}
	}

	private void saveTabularXml(TabularConcept concept, TabularConceptXmlType type, Language language, String xml) {
		xml = StringUtils.trimToNull(xml);
		xml = SpecialCharactersUtils.replace(xml);
		switch (type) {
		case NOTE:
			concept.setNote(language.getCode(), xml);
			break;
		case TABLE:
			TabularConceptType conceptType = toSubType(concept);
			if ((conceptType == CCI_RUBRIC) || (conceptType == ICD_CATEGORY)) {
				concept.setTableOutput(language.getCode(), xml);
			} else {
				throw new CIMSException("Table XML: is supported only for CCI rubric and ICD category");
			}
			break;
		case CODE_ALSO:
			concept.setCodeAlsoXml(language.getCode(), xml);
			break;
		case EXCLUDE:
			concept.setExcludeXml(language.getCode(), xml);
			break;
		case INCLUDE:
			concept.setIncludeXml(language.getCode(), xml);
			break;
		case OMIT_CODE:
			((CciTabular) concept).setOmitCodeXml(language.getCode(), xml);
			break;
		case DEFINITION:
			((IcdTabular) concept).setDefinitionXml(language.getCode(), xml);
			break;
		default:
			throw new CIMSException("Unsupported xml type: " + type);
		}
	}

	public void setAccessService(ChangeRequestAccessService accessService) {
		this.accessService = accessService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setConceptTransformationService(TransformationService conceptTransformationService) {
		this.conceptTransformationService = conceptTransformationService;
	}

	public void setContext(CurrentContext context) {
		this.context = context;
	}

	public void setContextOperations(ContextOperations contextOperations) {
		this.contextOperations = contextOperations;
	}

	public void setElementOperations(ElementOperations elementOperations) {
		this.elementOperations = elementOperations;
	}

	public void setIndexTransformationService(TransformIndexService indexTransformationService) {
		this.indexTransformationService = indexTransformationService;
	}

	public void setNonContextOperations(NonContextOperations nonContextOperations) {
		this.nonContextOperations = nonContextOperations;
	}

	public void setTransformationService(TransformationService transformationService) {
		this.conceptTransformationService = transformationService;
	}

	public void setTransformSupplementService(TransformSupplementService transformSupplementService) {
		this.transformSupplementService = transformSupplementService;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	private Map<String, IdCodeDescription> toCodeMap(List<IdCodeDescription> list) {
		Map<String, IdCodeDescription> map = new HashMap<String, IdCodeDescription>();
		for (IdCodeDescription d : list) {
			map.put(d.getCode(), d);
		}
		return map;
	}

	private TabularConceptModel toLightModel(TabularConceptDetails tabular) {
		if (tabular == null) {
			return null;
		} else {
			TabularConceptModel model = new TabularConceptModel();
			model.setParentCode(tabular.getParentCode());
			model.setElementId(tabular.getElementId());
			model.setCode(tabular.getCode());
			String clazz = tabular.getClassName().toUpperCase();
			switch (getCurrentClassification()) {
			case CCI:
				model.setType(TabularConceptType.valueOf("CCI_" + clazz));
				model.setNestingLevel(tabular.getNestingLevel());
				break;
			case ICD:
				model.setType(TabularConceptType.valueOf("ICD_" + clazz));
				model.setNestingLevel(tabular.getNestingLevel());
				model.setMorphology(ICD_MORPHOLOGY_CHAPTER_CODE.equals(tabular.getChapterCode()));
				break;
			default:
				throw new RuntimeException("Not supported: " + model.getClassification());
			}
			return model;
		}
	}

	private IndexModel toModel(Index idx, Language lang, boolean light) {
		IndexModel model = new IndexModel();
		model.setEntity(idx);
		model.setElementId(idx.getElementId());
		model.setType(IndexType.fromInstance(idx, getCurrentClassification()));
		model.setDescription(idx.getDescription());
		model.setStatus(ConceptStatus.valueOf(idx.getStatus()));
		if (idx instanceof IndexTerm) {
			IndexTerm term = (IndexTerm) idx;
			model.setLevel(term.getNestingLevel());
			model.setNote(SpecialCharactersUtils.replace(term.getNoteDescription(lang.getCode())));
		} else if (idx instanceof BookIndex) {
			BookIndex book = (BookIndex) idx;
			model.setNote(SpecialCharactersUtils.replace(book.getNoteDescription(lang.getCode())));
		}
		// FIXME: performance: check if bookId is needed
		model.setBookElementId(idx.getContainingBook().getElementId());
		model.setSection(idx.getContainingBook().getDescription());
		// FIXME: performance: check if we need to load xml
		if (model.getType().isTerm()) {
			IndexBaseXml xml = getIndexXmlOrNew(idx, model.getType(), lang);
			model.setSeeAlso(!equalsIgnoreCase(xml.getSeeAlsoFlag(), "N"));
			if (model.isIcdSection4()) {
				model.setSiteIndicatorCode(xml.getSiteIndicator());
			}
			List<IndexTermReferenceModel> refs = new ArrayList<IndexTermReferenceModel>();
			for (IndexReferenceXml index : xml.getIndexReferenceList()) {
				IndexTermReferenceModel ref = new IndexTermReferenceModel();
				ref.setElementId(index.getElementId());
				ref.setCustomDescription(index.getReferenceLinkDescription());
				refs.add(ref);
			}
			model.setIndexReferences(refs);
			List<IndexCategoryReferenceModel> cats = new ArrayList<IndexCategoryReferenceModel>();
			for (CategoryReferenceXml cat : xml.getCategoryReferenceList()) {
				IndexCategoryReferenceModel ref = new IndexCategoryReferenceModel();
				ref.setMainElementId(cat.getMainElementId());
				ref.setMainCode(cat.getMainCode());
				ref.setMainCustomDescription(cat.getMainCodePresentation());
				ref.setMainDaggerAsterisk(cat.getMainCodeDaggerAsteriskCode());
				if (cat.getPairedElementId() != -1) {
					ref.setPairedElementId(cat.getPairedElementId());
					ref.setPairedCode(cat.getPairedCode());
					ref.setPairedCustomDescription(cat.getPairedCodePresentation());
					ref.setPairedDaggerAsterisk(cat.getPairedCodeDaggerAsteriskCode());
				}
				cats.add(ref);
			}
			model.setCategoryReferences(cats);
			if (model.getType() == IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX) {
				IcdIndexDrugsAndChemicalsXml xmlDetails = (IcdIndexDrugsAndChemicalsXml) xml;
				List<TabularRefXml> details = xmlDetails.getDrugsDetail();
				Map<DrugDetailType, TabularReferenceModel> map = new HashMap<DrugDetailType, TabularReferenceModel>();
				if (details != null) {
					for (TabularRefXml ref : details) {
						map.put(DrugDetailType.valueOf(ref.getType()), toTabularReferenceModel(ref));
					}
				}
				model.setDrugsDetails(map);
			}
			if (model.getType() == IndexType.ICD_NEOPLASM_INDEX) {
				IcdIndexNeoplasmXml xmlDetails = (IcdIndexNeoplasmXml) xml;
				List<TabularRefXml> details = xmlDetails.getNeoplasmDetail();
				Map<NeoplasmDetailType, TabularReferenceModel> map = new HashMap<NeoplasmDetailType, TabularReferenceModel>();
				if (details != null) {
					for (TabularRefXml ref : details) {
						map.put(NeoplasmDetailType.valueOf(ref.getType()), toTabularReferenceModel(ref));
					}
				}
				model.setNeoplasmDetails(map);
			}
		}
		return model;
	}

	private SupplementModel toModel(Supplement tabular, Language lang, boolean loadParent) {
		if (tabular == null) {
			return null;
		} else {
			SupplementModel model = new SupplementModel();
			model.setEntity(tabular);
			model.setElementId(tabular.getElementId());
			model.setLevel(tabular.getNestingLevel());
			if (model.getLevel() == 1) {
				model.setMatter(SupplementMatter.fromCode(tabular.getSupplementType().getCode()));
			}
			model.setStatus(ConceptStatus.valueOf(tabular.getStatus()));
			model.setDescription(tabular.getSupplementDescription(lang.getCode()));
			model.setSortOrder(tabular.getSortingHint());
			model.setMarkup(tabular.getSupplementDefinition(lang.getCode()));
			// FIXME: METTER source?
			// model.setMatter(SupplementMatter.valueOf(tabular.getSupplementType()));
			if (loadParent) {
				// FIXME:
				BaseConcept parent = tabular.getParent();
				if (parent instanceof Supplement) {
					model.setParent(toModel((Supplement) parent, lang, false));
				}
			}
			return model;
		}
	}

	private TabularConceptModel toModel(TabularConceptDetails tabular, boolean loadParent) {
		if (tabular == null) {
			return null;
		} else {
			TabularConceptModel model = toLightModel(tabular);
			model.setStatus(ConceptStatus.valueOf(tabular.getStatus()));
			model.setShortTitleEng(tabular.getShortTitleEnglish());
			model.setShortTitleFra(tabular.getShortTitleFrench());
			model.setLongTitleEng(tabular.getLongTitleEnglish());
			model.setLongTitleFra(tabular.getLongTitleFrench());
			model.setUserTitleEng(tabular.getUserTitleEnglish());
			model.setUserTitleFra(tabular.getUserTitleFrench());
			model.setChildTable(!(isEmpty(tabular.getTablePresentationEnglish())
					&& StringUtils.isEmpty(tabular.getTablePresentationFrench())));
			switch (model.getClassification()) {
			case ICD: {
				model.setCanadianEnhancement(tabular.isCanadianEnhancementBool());
				model.setDaggerAsteriskId(tabular.getDaggerAsteriscId());
				if (loadParent && (model.getType() != TabularConceptType.ICD_CHAPTER)) {
					model.setParent(toModelParent(tabular));
				}
				break;
			}
			case CCI: {
				model.setInvasivenessLevel(tabular.getInvasivenessLevelId());
				if (loadParent && (model.getType() != TabularConceptType.CCI_SECTION)) {
					model.setParent(toModelParent(tabular));
				}
				break;
			}
			default:
				throw new RuntimeException("Not supported: " + model.getClassification());
			}
			model.setDiagramEng(toModel(tabular, ENGLISH));
			model.setDiagramFra(toModel(tabular, FRENCH));
			model.setVersionCode(tabular.getVersionCode());
			model.setContextVersionCode(tabular.getContextVersionCode());
			return model;
		}
	}

	private TabularConceptDiagramModel toModel(TabularConceptDetails tabular, Language lang) {
		TabularConceptDiagramModel model = new TabularConceptDiagramModel();
		if (lang == ENGLISH) {
			model.setName(tabular.getDiagramFileNameEnglish());
		} else {
			model.setName(tabular.getDiagramFileNameFrench());
		}
		return model;
	}

	private TabularConceptModel toModelParent(TabularConceptDetails tabular) {
		if (tabular == null) {
			return null;
		} else {
			TabularConceptModel model = new TabularConceptModel();
			model.setElementId(tabular.getParentId());
			model.setCode(tabular.getParentCode());
			model.setNestingLevel(tabular.getParentNestingLevel());
			return model;
		}
	}

	private TabularConceptType toSubType(TabularConcept tabular) {
		return TabularConceptType
				.valueOf((tabular instanceof IcdTabular ? "ICD_" : "CCI_") + tabular.getTypeCode().toUpperCase());
	}

	private TabularReferenceModel toTabularReferenceModel(TabularRefXml ref) {
		TabularReferenceModel model = new TabularReferenceModel();
		model.setCustomDescription(ref.getCodePresentation());
		model.setElementId(ref.getElementId());
		return model;
	}

	private TabularRefXml toTabularRefXml(TabularReferenceModel model, String type) {
		TabularRefXml xml = new TabularRefXml();
		xml.setType(type);
		if (model != null) {
			xml.setCodePresentation(defaultIfEmpty(model.getCustomDescription(), null));
			xml.setContainerConceptIdPath(elementOperations.determineContainingIdPath(getCurrentBaseClassification(),
					getCurrentContextId(), model.getElementId()));
		}
		return xml;
	}

	private void transformConcept(BaseConcept tabular) {
		transformConcept(tabular, false);
	}

	private void transformConcept(BaseConcept tabular, boolean reloadContext) {
		if ((tabular != null) && (tabular instanceof TabularConcept)) {
			log.debug("Transforming concept: " + tabular.getElementId());
			ContextAccess ctx = getContexAccess();
			if (reloadContext) {
				ctx = ctx.reload();
				tabular = ctx.load(tabular.getElementId());
			}
			conceptTransformationService.transformConcept((TabularConcept) tabular, ctx, false);
			getContexAccess().persist();
		}
	}

	@Transactional
	public void transformConcept(OptimisticLock lock, User user, TabularConcept concept) {
		ContextAccess contexAccess = getContexAccess();
		conceptTransformationService.transformConcept(concept, contexAccess, false);
		getContexAccess().persist();
		updateChangeRequestLastUpdateTime(user, lock);
	}

	@Transactional
	public void transformIndex(OptimisticLock lock, User user, Index concept) {
		ContextAccess contexAccess = getContexAccess();
		Language lang = getChangeRequestLanguages().iterator().next();
		indexTransformationService.transformIndexConcept(concept, lang.getCode(), contexAccess, false);
		getContexAccess().persist();
		updateChangeRequestLastUpdateTime(user, lock);
	}

	private void transformIndexParentAfterDelete(Index parent, Index containingPage, Language lang) {
		ContextAccess contexAccess = getContexAccess();
		indexTransformationService.transformIndexConcept(parent, lang.getCode(), contexAccess, false);
		indexTransformationService.transformIndexConcept(containingPage, lang.getCode(), contexAccess, false);
	}

	private void transformParentAfterDelete(TabularConcept tabular, TabularConcept parent,
			TabularConcept containingPage) {
		TabularConceptType type = toSubType(tabular);
		if (type == CCI_BLOCK) {
			transformConcept(containingPage, true);
		} else if (type == CCI_CCICODE) {
			transformConcept(parent, true);
		} else if (type.getClassification() == ICD) {
			IcdTabular icdTabular = (IcdTabular) tabular;
			if (((type == ICD_CATEGORY) && (icdTabular.getNestingLevel() == 1)) || (type == ICD_BLOCK)) {
				transformConcept(containingPage, true);
			}
		}
	}

	private void transformParentAfterSave(TabularConcept tabular, boolean isNew,
			boolean codeValueOrUserTitleOrAsteriscChanged) {
		if (isNew || codeValueOrUserTitleOrAsteriscChanged) {
			TabularConceptType type = toSubType(tabular);
			if (type == CCI_BLOCK) {
				CciTabular cciTabular = (CciTabular) tabular;
				transformConcept(cciTabular.getContainingPage());
			} else if (type == CCI_CCICODE) {
				CciTabular cciTabular = (CciTabular) tabular;
				transformConcept(cciTabular.getParent());
			} else if (type.getClassification() == ICD) {
				IcdTabular icdTabular = (IcdTabular) tabular;
				if (((type == ICD_CATEGORY) && (icdTabular.getNestingLevel() == 1)) || (type == ICD_BLOCK)) {
					transformConcept(icdTabular.getContainingPage());
				}
			}
		}
	}

	@Transactional
	public void transformSupplement(OptimisticLock lock, User user, Supplement concept) {
		ContextAccess contexAccess = getContexAccess();

		transformSupplementService.transformSupplement(getCurrentBaseClassification(), getCurrentVersionCode(), concept,
				contexAccess, false);
		getContexAccess().persist();
		updateChangeRequestLastUpdateTime(user, lock);
	}

	private void updateChangeRequestLastUpdateTime(User user, OptimisticLock lock) {
		changeRequestService.updateChangeRequestLastUpdateTime(getCurrentChangeRequestId(), user, lock);
	}

	private void validateJsr(ErrorBuilder result, Object edited) {
		Set<ConstraintViolation<Object>> validate = validator.validate(edited);
		for (ConstraintViolation<Object> v : validate) {
			result.rejectValue(v.getPropertyPath().toString(), v.getMessage());
		}
	}

	private void validateModel(ErrorBuilder result, ChangeRequestPermission p, TabularConceptXmlModel edited,
			TabularConceptXmlModel original) {
		if (original.getType() == TabularConceptXmlType.TABLE) {
			checkConceptIsRubricOrCategory(original.getRawConcept());
		}
		if (!p.isCanWrite(Language.ENGLISH)) {
			if (!StringUtils.equals(edited.getEnglishXml(), original.getEnglishXml())) {
				result.rejectValue("englishXml", VALUE_NOT_ALLOWED);
			}
		} else {
			validateXml(result, original.getType(), "englishXml", edited.getEnglishXml());
		}
		if (!p.isCanWrite(Language.FRENCH)) {
			if (!StringUtils.equals(edited.getFrenchXml(), original.getFrenchXml())) {
				result.rejectValue("frenchXml", VALUE_NOT_ALLOWED);
			}
		} else {
			validateXml(result, original.getType(), "frenchXml", edited.getFrenchXml());
		}

	}

	private void validateModel(ErrorBuilder result, TabularConceptDiagramModel diagram, boolean english) {
		if (!diagram.isRemove() && (diagram.getFile() != null) && !diagram.getFile().isEmpty()) {
			String diagramName = diagram.getFile().getOriginalFilename();
			if (!diagramName.toLowerCase().endsWith(".gif")) {
				String property = (english ? "diagramEng" : "diagramFra") + ".file";
				result.rejectValue(property, "GIF file expected");
			}
		}
	}

	private void validateModel(ErrorBuilder result, TabularConceptModel edited, TabularConceptModel original) {
		validateJsr(result, edited);
		if (!StringUtils.equals(edited.getCode(), original.getCode())) {
			String codeError = null;
			if (original.isIcdBlock()) {
				codeError = codeValidator.validateIcdBlock(edited.getCode(), original.isMorphology());
			} else {
				String sectionCode = getContainingPageCode(original.getElementId());
				codeError = codeValidator.validateCciBlock(edited.getCode(), sectionCode);
			}
			if (codeError != null) {
				result.rejectValue("code", codeError);
			}
		}
		if (edited.getStatus() != original.getStatus()) {
			if (!isTabularStatusEditable(original)) {
				result.rejectValue("status", VALUE_NOT_ALLOWED);
			}
		}
		switch (original.getClassification()) {
		case ICD: {
			if (edited.getDaggerAsteriskId() != original.getDaggerAsteriskId()) {
				if (!isDaggerAsteriskEditable(edited)) {
					result.rejectValue("daggerAsteriskId", MODIFICATION_NOT_ALLOWED);
				}
			}
			if (edited.isCanadianEnhancement() != original.isCanadianEnhancement()) {
				if (!isCanadianEnhancementEditable(edited)) {
					result.rejectValue("canadianEnhancement", MODIFICATION_NOT_ALLOWED);
				}
			}
			break;
		}
		case CCI: {
			if (edited.isCciCode()) {
				if (!ObjectUtils.equals(edited.getInvasivenessLevel(), original.getInvasivenessLevel())) {
					// FIXME: any validation?
				}
			}
			break;
		}
		default:
			throw new RuntimeException("Save not supported: " + original.getClassification());
		}
		Set<Language> languages = getChangeRequestLanguages();
		boolean userTitleEditable = isUserTitleEditable(original);
		if (!languages.contains(ENGLISH)) {
			rejectIfNotEquals(result, "shortTitleEng", original.getShortTitleEng(), edited.getShortTitleEng());
			rejectIfNotEquals(result, "longTitleEng", original.getLongTitleEng(), edited.getLongTitleEng());
			rejectIfNotEquals(result, "userTitleEng", original.getUserTitleEng(), edited.getUserTitleEng());
			rejectIfNotEquals(result, "diagramEng", original.getDiagramEng(), edited.getDiagramEng());
		} else {
			if (!userTitleEditable) {
				rejectIfNotEquals(result, "userTitleEng", original.getUserTitleEng(), edited.getUserTitleEng());
				rejectIfNotEquals(result, "diagramEng", original.getDiagramEng(), edited.getDiagramEng());
			} else {
				validateModel(result, edited.getDiagramEng(), true);
			}
		}
		if (!languages.contains(FRENCH)) {
			rejectIfNotEquals(result, "shortTitleFra", original.getShortTitleFra(), edited.getShortTitleFra());
			rejectIfNotEquals(result, "longTitleFra", original.getLongTitleFra(), edited.getLongTitleFra());
			rejectIfNotEquals(result, "userTitleFra", original.getUserTitleFra(), edited.getUserTitleFra());
			rejectIfNotEquals(result, "diagramFra", original.getDiagramFra(), edited.getDiagramFra());
		} else {
			if (!userTitleEditable) {
				rejectIfNotEquals(result, "userTitleFra", original.getUserTitleFra(), edited.getUserTitleFra());
				rejectIfNotEquals(result, "diagramFra", original.getDiagramFra(), edited.getDiagramFra());
			} else {
				validateModel(result, edited.getDiagramFra(), false);
			}
		}
	}

	private void validateModel(ErrorBuilder result, TabularConceptValidationSetModel edited) {
		validateJsr(result, edited);
		if ((edited.getAgeMinimum() < 0) || (edited.getAgeMaximum() > 130)) {
			result.rejectValue("ageMinimum", "Age range must be a positive integer between 0 and 130");
		}
		if (edited.getAgeMinimum() >= edited.getAgeMaximum()) {
			result.rejectValue("ageMinimum", "Maximum age must be higher then minimal age");
		}
		if (edited instanceof TabularConceptCciValidationSetModel) {
			TabularConceptCciValidationSetModel cciEdited = (TabularConceptCciValidationSetModel) edited;
			if (!isEmpty(cciEdited.getLocationReferenceCode()) && !isEmpty(cciEdited.getDeliveryReferenceCode())) {
				String message = "Select either Location or Mode of Delivery Reference";
				result.rejectValue("locationReferenceCode", message);
				result.rejectValue("deliveryReferenceCode", message);
			}
		}
	}

	private void validateXml(ErrorBuilder result, String rootElement, String dtd, String property, String xml) {
		if (!isEmpty(xml)) {
			String error = dtdValidator.validateSegment(rootElement, dtd, xml);
			if (error != null) {
				result.rejectValue(property, error);
			}
		}
	}

	private void validateXml(ErrorBuilder result, TabularConceptXmlType xmlType, String property, String xml) {
		if (!isEmpty(xml)) {
			String dtd = CONCEPT_XML_DTDS[xmlType.ordinal()];
			String rootElement = xmlType == TabularConceptXmlType.TABLE ? "table"
					: xmlType == TabularConceptXmlType.SUPPLEMENT ? "supplement" : "qualifierlist";
			validateXml(result, rootElement, dtd, property, xml);
		}
	}
}
