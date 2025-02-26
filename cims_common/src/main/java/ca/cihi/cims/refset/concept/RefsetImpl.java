package ca.cihi.cims.refset.concept;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.FrameworkConstants;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.config.annotation.Property;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.Element;
import ca.cihi.cims.framework.domain.PropertyValue;
import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.refset.config.MetadataConfigSource;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.handler.RefsetControlHandler;
import ca.cihi.cims.refset.service.ValueRefreshFactory;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Supplement;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:42:08 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.REFSET)
public class RefsetImpl extends Concept implements Refset {

	private static final Log LOGGER = LogFactory.getLog(RefsetImpl.class);
	private static final String REFSET_BK_PART = "REFSET_CONCEPT_";

	/**
	 * Creates a new Refset including its inception version. Instantiates and returns the appropriate Refset object.
	 *
	 * @param code
	 * @param name
	 * @throws WrongClasssException
	 */
	public static Refset create(String code, String name) throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.REFSET,
				RefsetConstants.CODE, code, null, RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.REFSET,
				RefsetConstants.NAME, name, null, RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}

		// TODO: need to store below information somewhere. In the matadata mapper?
		Classs refsetContextClasss = Classs.create(new ClasssDTO(RefsetConstants.BASEREFSET, code, code, name));
		// Create Concept classes
		Classs refsetClass = Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code,
				RefsetConstants.REFSET, RefsetConstants.REFSET));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.PICKLIST,
				RefsetConstants.PICKLIST));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.SUPPLEMENT,
				RefsetConstants.SUPPLEMENT));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.SUBLIST,
				RefsetConstants.SUBLIST));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.RECORD,
				RefsetConstants.RECORD));
		Classs.create(
				new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.VALUE, RefsetConstants.VALUE));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_VERSION, code, RefsetConstants.COLUMN,
				RefsetConstants.COLUMN));
		// Create Property classes
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.NAME,
				RefsetConstants.NAME));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.CODE,
				RefsetConstants.CODE));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.CATEGORY,
				RefsetConstants.CATEGORY));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.DEFINITION,
				RefsetConstants.DEFINITION));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code,
				RefsetConstants.EFFECTIVEYEARFROM, RefsetConstants.EFFECTIVEYEARFROM));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.EFFECTIVEYEARTO,
				RefsetConstants.EFFECTIVEYEARTO));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.ICD10CACONTEXTID,
				RefsetConstants.ICD10CACONTEXTID));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.ICD10CAYEAR,
				RefsetConstants.ICD10CAYEAR));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.CCICONTEXTID,
				RefsetConstants.CCICONTEXTID));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.CCIYEAR,
				RefsetConstants.CCIYEAR));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.SCTVERSION,
				RefsetConstants.SCTVERSION));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.NOTES,
				RefsetConstants.NOTES));
		Classs.create(new ClasssDTO(FrameworkConstants.XLSX_PROPERTY_VERSION, code, RefsetConstants.CONTENT,
				RefsetConstants.CONTENT));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.COLUMNTYPE,
				RefsetConstants.COLUMNTYPE));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.COLUMNNAME,
				RefsetConstants.COLUMNNAME));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.COLUMNORDER,
				RefsetConstants.COLUMNORDER));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.TEXTVALUE,
				RefsetConstants.TEXTVALUE));
		Classs.create(new ClasssDTO(FrameworkConstants.NUMERIC_PROPERTY_VERSION, code, RefsetConstants.IDVALUE,
				RefsetConstants.IDVALUE));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code, RefsetConstants.FILENAME,
				RefsetConstants.FILENAME));
		Classs.create(new ClasssDTO(FrameworkConstants.TEXT_PROPERTY_VERSION, code,
				RefsetConstants.CLASSIFICATIONSTANDARD, RefsetConstants.CLASSIFICATIONSTANDARD));
		// Create Relationship classes
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_PROPERTY_VERSION, code, RefsetConstants.PARTOF,
				RefsetConstants.PARTOF));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_PROPERTY_VERSION, code, RefsetConstants.DESCRIBEDBY,
				RefsetConstants.DESCRIBEDBY));
		Classs.create(new ClasssDTO(FrameworkConstants.CONCEPT_PROPERTY_VERSION, code, RefsetConstants.COLUMNOF,
				RefsetConstants.COLUMNOF));

		Context context = Context.createInceptionVersion(refsetContextClasss, Element.generateBusinessKey(),
				RefsetConstants.INCEPTION_VERSION_CODE);
		String rfbk = generateBusinessKey(code);
		ElementIdentifier refsetElementIdentifier = Concept.create(RefsetConstants.REFSET, context, rfbk);
		RefsetImpl rfs = new RefsetImpl(refsetClass, context, refsetElementIdentifier, ConceptLoadDegree.REGULAR);
		rfs.setCode(code);
		rfs.setName(name, Language.ENG);
		return rfs;
	}

	/**
	 * Generates a Refset Business Key based on the Refset code. The business key format should be: REFSET_CONCEPT_
	 * <code> e.g. REFSET_CONCEPT_CDEX
	 *
	 * @param code
	 */
	private static String generateBusinessKey(String code) {
		return new StringBuffer().append(REFSET_BK_PART).append(code).toString();
	}

	private Long assigneeId;

	private String assigneeName;

	private RefsetStatus status;

	/**
	 * assert classs.className='Refset' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public RefsetImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(RefsetImpl.class));
		loadProperties(degree);
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		RefsetVersion refsetVersion = refsetControlHandler.getRefsetDTO(context.getContextId());
		if (refsetVersion != null) {
			this.assigneeName = refsetVersion.getAssigneeName();
			this.status = refsetVersion.getRefsetStatus();
			this.assigneeId = refsetVersion.getAssigneeId();
		}
	}

	/**
	 * - Refset( new Classs(conceptDTO.classDTO), context, conceptDTO. elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */

	public RefsetImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		populateProperties(conceptDTO);
	}

	@Override
	public void assign(Long assigneeId) {
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		refsetControlHandler.updateAssignee(assigneeId, this.getElementIdentifier().getElementId());
		this.assigneeId = assigneeId;
	}

	@Override
	public void assign(String userName) {
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		LOGGER.debug("userName=" + userName);
		Long savedAssigneeId = refsetControlHandler.getAssigneeId(userName);
		LOGGER.debug("assigneeId=" + savedAssigneeId);
		refsetControlHandler.updateAssignee(savedAssigneeId, this.getElementIdentifier().getElementId());
		this.assigneeId = savedAssigneeId;
	}

	/**
	 * -- getContext.createSubsequentVersion( versionCode); Classs classs = Classs.<i>findById</i>
	 * (this.getClasss().getClassId()); rfs = new Refset(classs, getContext.createSubsequentVersion( versionCode,this.
	 * getElementIdentifier()); rfs.loadProperties (ConceptLoadDegree.<i>REGULAR</i>); return rfs
	 *
	 * @param versionCode
	 * @throws PropertyKeyNotFoundException
	 */
	@Override
	public Refset createNewVersion(String versionCode, Long newICD10CAContextId, Long newCCIContextId,
			String newSCTVersionCode) {
		Context context = getContext().createSubsequentVersion(versionCode);
		Classs classs = Classs.findById(getClasss().getClassId());
		RefsetImpl refset = new RefsetImpl(classs, context, getElementIdentifier(), ConceptLoadDegree.COMPLETE);

		refset.setCCIContextId(newCCIContextId);
		refset.setICD10CAContextId(newICD10CAContextId);
		refset.setSCTVersionCode(newSCTVersionCode);

		ValueRefreshFactory.init().RefreshAll(this, refset);

		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		refsetControlHandler.removeEmptyRecords(refset.getContext());
		refsetControlHandler.copyConfiguration(getContext(), refset.getContext());
		return refset;
	}

	@Override
	public void disableRefset() {
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		refsetControlHandler.disableRefset(this.getElementIdentifier().getElementId());
		this.status = RefsetStatus.DISABLED;
	}

	@Override
	public void enableRefset() {
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		refsetControlHandler.enableRefset(this.getElementIdentifier().getElementId());
		this.status = RefsetStatus.ACTIVE;
	}

	@Override
	public Long getAssignee() {
		return this.assigneeId;
	}

	@Override
	public String getAssigneeName() {
		return this.assigneeName;
	}

	@Override
	public Long getCategoryId() {
		return Long.parseLong((String) (getProperty(RefsetConstants.CATEGORY, Language.NOLANGUAGE)));
	}

	@Override
	public Long getCCIContextId() {
		Object value = getProperty(RefsetConstants.CCICONTEXTID, Language.NOLANGUAGE);
		if (value != null) {
			if (value instanceof Long) {
				return (Long) value;
			}
			return Long.parseLong((String) (value));
		} else {
			return null;
		}
	}

	@Override
	public String getCCIYear() {
		return (String) (getProperty(RefsetConstants.CCIYEAR, Language.NOLANGUAGE));
	}

	@Override
	public String getCode() {
		return (String) (getProperty(RefsetConstants.CODE, Language.NOLANGUAGE));
	}

	@Override
	public String getDefinition() {
		return (String) (getProperty(RefsetConstants.DEFINITION, Language.NOLANGUAGE));
	}

	@Override
	public Short getEffectiveYearFrom() {
		return Short.valueOf((String) (getProperty(RefsetConstants.EFFECTIVEYEARFROM, Language.NOLANGUAGE)));
	}

	@Override
	public Short getEffectiveYearTo() {
		String value = (String) (getProperty(RefsetConstants.EFFECTIVEYEARTO, Language.NOLANGUAGE));
		if (value == null) {
			return null;
		}
		return Short.valueOf((String) (getProperty(RefsetConstants.EFFECTIVEYEARTO, Language.NOLANGUAGE)));
	}

	@Override
	public Long getICD10CAContextId() {
		Object value = getProperty(RefsetConstants.ICD10CACONTEXTID, Language.NOLANGUAGE);
		if (value != null) {
			if (value instanceof Long) {
				return (Long) value;
			}
			return Long.parseLong((String) (value));
		} else {
			return null;
		}
	}

	@Override
	public String getICD10CAYear() {
		return (String) (getProperty(RefsetConstants.ICD10CAYEAR, Language.NOLANGUAGE));
	}

	/**
	 * return getProperty(RefsetConstants.NAME, language) cast to String
	 *
	 * @param language
	 */
	@Override
	public String getName(Language language) {
		return (String) (getProperty(RefsetConstants.NAME, language));
	}

	@Override
	public String getNotes() {
		return (String) (getProperty(RefsetConstants.NOTES, Language.NOLANGUAGE));
	}

	@Override
	public String getSCTVersionCode() {
		return (String) (getProperty(RefsetConstants.SCTVERSION, Language.NOLANGUAGE));
	}

	@Override
	public String getStatus() {
		return this.status.getStatus();
	}

	@Override
	public String getVersionCode() {
		return this.getContext().getVersionCode();
	}

	/**
	 * call this.getRelatedConcepts(...) return list
	 *
	 * @throws Exception
	 */
	@Override
	public List<PickList> listPickLists() {
		List<Concept> conceptList = getReferencingConcepts(new ConceptQueryCriteria(PickListImpl.class,
				RefsetConstants.PARTOF, ConceptLoadDegree.REGULAR, null, RefsetConstants.PICKLIST));
		List<PickList> picklistList = new ArrayList<PickList>();
		for (Concept concept : conceptList) {
			picklistList.add((PickList) concept);
		}
		return picklistList;
	}

	@Override
	public List<Supplement> listSupplements() {
		List<Concept> conceptList = getReferencingConcepts(new ConceptQueryCriteria(SupplementImpl.class,
				RefsetConstants.PARTOF, ConceptLoadDegree.REGULAR, null, RefsetConstants.SUPPLEMENT));
		List<Supplement> supplements = new ArrayList<Supplement>();
		for (Concept concept : conceptList) {
			supplements.add((Supplement) concept);
		}
		return supplements;
	}

	@Override
	@Property(classsName = RefsetConstants.CATEGORY, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setCategoryId(long categoryId) {
		setProperty(new PropertyKey(RefsetConstants.CATEGORY, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(categoryId));
	}

	/**
	 *
	 * @param contextId
	 */
	@Override
	@Property(classsName = RefsetConstants.CCICONTEXTID, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR, ConceptLoadDegree.MINIMAL }, propertyType = PropertyType.NumericProperty)
	public void setCCIContextId(Long contextId) {
		setProperty(new PropertyKey(RefsetConstants.CCICONTEXTID, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(contextId));

	}

	/**
	 *
	 * @param year
	 */
	@Override
	@Property(classsName = RefsetConstants.CCIYEAR, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setCCIYear(String year) {
		setProperty(new PropertyKey(RefsetConstants.CCIYEAR, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(year));
	}

	/**
	 *
	 * @param code
	 * @throws DuplicateCodeNameException
	 */
	@Property(classsName = RefsetConstants.CODE, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	private void setCode(String code) throws DuplicateCodeNameException {

		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.REFSET, RefsetConstants.CODE, code, null, RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		setProperty(new PropertyKey(RefsetConstants.CODE, Language.NOLANGUAGE, PropertyType.TextProperty),
				new PropertyValue(code));
	}

	/**
	 *
	 * @param definition
	 */
	@Override
	@Property(classsName = RefsetConstants.DEFINITION, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setDefinition(String definition) {
		setProperty(new PropertyKey(RefsetConstants.DEFINITION, Language.NOLANGUAGE, PropertyType.TextProperty),
				new PropertyValue(definition));
	}

	/**
	 *
	 * @param year
	 */
	@Override
	@Property(classsName = RefsetConstants.EFFECTIVEYEARFROM, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setEffectiveYearFrom(short year) {
		setProperty(
				new PropertyKey(RefsetConstants.EFFECTIVEYEARFROM, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(year));
	}

	/**
	 *
	 * @param year
	 */
	@Override
	@Property(classsName = RefsetConstants.EFFECTIVEYEARTO, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setEffectiveYearTo(short year) {
		setProperty(
				getConceptMetadata().getPropertyKeys().get(new PropertyKey(RefsetConstants.EFFECTIVEYEARTO,
						Language.NOLANGUAGE, PropertyType.NumericProperty).generateKeyIdentifier()),
				new PropertyValue(year));
	}

	/**
	 *
	 * @param contextId
	 */
	@Override
	@Property(classsName = RefsetConstants.ICD10CACONTEXTID, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR, ConceptLoadDegree.MINIMAL }, propertyType = PropertyType.NumericProperty)
	public void setICD10CAContextId(Long contextId) {
		setProperty(
				new PropertyKey(RefsetConstants.ICD10CACONTEXTID, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(contextId));
	}

	/**
	 *
	 * @param year
	 */
	@Override
	@Property(classsName = RefsetConstants.ICD10CAYEAR, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setICD10CAYear(String year) {
		setProperty(new PropertyKey(RefsetConstants.ICD10CAYEAR, Language.NOLANGUAGE, PropertyType.NumericProperty),
				new PropertyValue(year));
	}

	/**
	 * - instantiate value PropertyValue object for name - setProperty(RefsetConstants.NAME, language, value)
	 *
	 * Note. See if this method can be generated with CGI lib. Not urgent
	 *
	 * @param name
	 * @param language
	 * @throws DuplicateNameException
	 */
	@Override
	@Property(classsName = RefsetConstants.NAME, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty, languages = { Language.ENG,
					Language.FRA })
	public void setName(String name, Language language) throws DuplicateCodeNameException {

		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.REFSET, RefsetConstants.NAME, name, getContext().getElementIdentifier(),
				RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}
		setProperty(getConceptMetadata().getPropertyKeys().get(
				new PropertyKey(RefsetConstants.NAME, language, PropertyType.TextProperty).generateKeyIdentifier()),
				new PropertyValue(name));
	}

	/**
	 *
	 * @param notes
	 */
	@Override
	@Property(classsName = RefsetConstants.NOTES, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setNotes(String notes) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.NOTES, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(notes));
	}

	/**
	 *
	 * @param versionCode
	 */
	@Override
	@Property(classsName = RefsetConstants.SCTVERSION, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR, ConceptLoadDegree.MINIMAL }, propertyType = PropertyType.TextProperty)
	public void setSCTVersionCode(String versionCode) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.SCTVERSION, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(versionCode));
	}

	@Override
	public ContextStatus getVersionStatus() {
		return getContext().getContextStatus();
	}

	@Override
	public String getCategoryName(Long categoryId) {
		RefsetControlHandler refsetControlHandler = ApplicationContextProvider.getApplicationContext()
				.getBean(RefsetControlHandler.class);
		return refsetControlHandler.getCategoryName(categoryId);
	}

	// Removes the Refset version
	@Override
	public void remove() {
		super.remove();
		this.getContext().remove();
	}

	@Override
	public void closeRefsetVersion() {
		this.getContext().closeContext();
	}

	@Override
	public Long getLatestClosedVersion() {
		return getContext().getLatestClosedVersion();
	}

	@Override
	public boolean isOpenVersionExists() {
		return getContext().isOpenVersionExists();
	}

	@Override
	public boolean isLatestClosedVersion() {
		Long latestClosedVersion = getLatestClosedVersion();
		return ((latestClosedVersion != null) && latestClosedVersion.equals(getContext().getContextId()));
	}

}