package ca.cihi.cims.refset.concept;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.config.annotation.Property;
import ca.cihi.cims.framework.config.annotation.Relationship;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.PropertyCriterion;
import ca.cihi.cims.framework.domain.PropertyValue;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.enums.ComparisonOperator;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.config.MetadataConfigSource;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Refset;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:42:27 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.PICKLIST)
@Relationship(classsName = RefsetConstants.PARTOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
public class PickListImpl extends RecordsListImpl implements PickList {

	private static final Logger LOGGER = LogManager.getLogger(PickListImpl.class);
	private static final String PICKLIST_BK_PART = "PICKLIST_CONCEPT_";

	/**
	 * Creates a new PickList including its inception version. Instantiates and returns the appropriate PickList object.
	 * - pkbk = generatebusinessKey(code) - if Element.existsInContext(pkbk) -- throew duplicate exeception -
	 * picklistElementIdentifier = super.create('PickList' , refset.context, pkbk ) - classs =
	 * Classs.findByName('PickList', refset.classs.baseClassificationName) -picklist = new PickList( classs,
	 * refset.context, picklistElementIdentifier ) - picklist .loadProperties (REGULAR) -
	 * picklist.setProperty(<PropertyKey for PartOf>, refset.elementId); - return picklist
	 *
	 * @param refset
	 * @param code
	 */
	public static PickList create(Refset refset, String code, String name, String classificationStandard)
			throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.PICKLIST,
				RefsetConstants.CODE, code, refset.getContextElementIdentifier(), RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.PICKLIST,
				RefsetConstants.NAME, name, refset.getContextElementIdentifier(), RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}
		LOGGER.debug("Create picklist start.");
		String bk;
		bk = generateBusinessKey(refset.getCode() + "_" + code);

		Context context = Context.findById(refset.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.PICKLIST, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.PICKLIST, context.getBaseClassificationName());
		PickListImpl pickList = new PickListImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		pickList.setProperty(new PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(refset.getElementIdentifier().getElementId()));
		pickList.setCode(code);
		pickList.setName(name);
		pickList.setClassificationStandard(classificationStandard);
		LOGGER.debug("Create picklist end.");
		return pickList;
	}

	/**
	 * Generates a PickList Business Key based on the picklist code . The business key format should be:
	 *
	 * PICKLIST_CONCEPT_<code> e.g. PICKLIST_CONCEPT_CDEXPL
	 *
	 * @param code
	 */
	private static String generateBusinessKey(String code) {
		return new StringBuffer().append(PICKLIST_BK_PART).append(code).toString();
	}

	/**
	 * assert classs.className='PickList' super(classs, context, elementIdentifier)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */

	public PickListImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(PickListImpl.class));
		loadProperties(degree);
	}

	/**
	 * - PickList( new Classs(conceptDTO.classDTO), context, conceptDTO. elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */

	public PickListImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.NONE);
		populateProperties(conceptDTO);
	}

	@Override
	public String getClassificationStandard() {
		return (String) (getProperty(RefsetConstants.CLASSIFICATIONSTANDARD, Language.NOLANGUAGE));
	}

	@Override
	public String getCode() {
		return (String) (getProperty(RefsetConstants.CODE, Language.NOLANGUAGE));
	}

	@Override
	public String getName() {
		return (String) (getProperty(RefsetConstants.NAME, Language.NOLANGUAGE));
	}

	/**
	 * call this.getReferencingConcepts(...) return list
	 *
	 * @throws Exception
	 */
	@Override
	public List<Column> listColumns() {
		List<Concept> conceptList = getReferencingConcepts(new ConceptQueryCriteria(ColumnImpl.class,
				RefsetConstants.COLUMNOF, ConceptLoadDegree.REGULAR, null, RefsetConstants.COLUMN));
		List<Column> columnList = new ArrayList<Column>();
		for (Concept concept : conceptList) {
			columnList.add((Column) concept);
		}
		return columnList;

	}

	@Override
	public List<Column> listSublistColumns() {
		List<PropertyCriterion> queryCondition = new ArrayList<>();
		PropertyCriterion criterion = new PropertyCriterion();
		criterion.setClasssId(Classs.findByName(RefsetConstants.COLUMNTYPE, getBaseClassificationName()).getClassId());
		criterion.setValue(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		criterion.setLanguageCode(null);
		criterion.setOperator(ComparisonOperator.EQUALS.name());
		criterion.setPropertyType(PropertyType.TextProperty.name());
		queryCondition.add(criterion);
		List<Concept> conceptList = getReferencingConcepts(new ConceptQueryCriteria(ColumnImpl.class,
				RefsetConstants.COLUMNOF, ConceptLoadDegree.REGULAR, queryCondition, RefsetConstants.COLUMN));
		List<Column> columnList = new ArrayList<Column>();
		for (Concept concept : conceptList) {
			columnList.add((Column) concept);
		}
		return columnList;

	}

	@Property(classsName = RefsetConstants.CLASSIFICATIONSTANDARD, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	private void setClassificationStandard(String classificationStandard) {
		setProperty(
				getConceptMetadata().getPropertyKeys()
						.get(new PropertyKey(RefsetConstants.CLASSIFICATIONSTANDARD, Language.NOLANGUAGE,
								PropertyType.TextProperty).generateKeyIdentifier()),
				new PropertyValue(classificationStandard));
	}

	@Property(classsName = RefsetConstants.CODE, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	private void setCode(String code) throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.PICKLIST, RefsetConstants.CODE, code, getContext().getElementIdentifier(),
				RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		String keyIdentifier = new PropertyKey(RefsetConstants.CODE, Language.NOLANGUAGE, PropertyType.TextProperty)
				.generateKeyIdentifier();
		setProperty(getConceptMetadata().getPropertyKeys().get(keyIdentifier), new PropertyValue(code));
	}

	/**
	 * keyIdentifier = buildPropertyIdentifier(RefsetConstants.NAME, NOLANGUAGE) key = getKey(keyIdentifier )
	 * instantiate value PropertyValue object for name setProperty(key, value);
	 *
	 * Note. See if this method can be generated with CGI lib. Not urgent
	 *
	 * @throws DuplicateNameException
	 */
	@Override
	@Property(classsName = RefsetConstants.NAME, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setName(String name) throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.PICKLIST, RefsetConstants.NAME, name, getContext().getElementIdentifier(),
				RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.NAME, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(name));
	}

}