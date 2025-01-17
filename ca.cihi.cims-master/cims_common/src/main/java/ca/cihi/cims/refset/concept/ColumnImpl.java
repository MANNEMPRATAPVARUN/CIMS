package ca.cihi.cims.refset.concept;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.CIMSConstants;
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
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetConcept;
import ca.cihi.cims.refset.service.concept.Value;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:42:39 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.COLUMN)
@Relationship(classsName = RefsetConstants.COLUMNOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
public class ColumnImpl extends Concept implements Column {

	private static final Logger LOGGER = LogManager.getLogger(ColumnImpl.class);

	public final static String CIMS_COLUMN_CANNOT_DELETE = "CIMS Classification Code Column is a mandatory Column";
	public final static String SCT_CONCEPT_ID_CANNOT_DELETE = "The SCT Concept ID Column Type cannot be deleted unless the associated SNOMED CT Terms are deleted";

	/**
	 * - if the container is a Column assert that the type of the container column is Sublist - if the container is
	 * PickList assert that the columnType represents a valid column - else throw system exception Creates a new Column
	 * including its inception version. Instantiates and returns the appropriate Columnobject. - pkbk =
	 * generatebusinessKey() - elementIdentifier = super.create('Column' , container.context, pkbk ) - classs =
	 * Classs.findByName('Column', container.classs. baseClassificationName) - column = new Column( classs,
	 * container.context, elementIdentifier ) - column .loadProperties (REGULAR) - column .setProperty(<PropertyKey for
	 * ColumnOf>, container.elementId); - return column
	 *
	 * @param container
	 * @param columnType
	 */
	public static Column create(RefsetConcept container, String columnType, String columnName) {

		// TODO validate unique except for sublist column
		if (ColumnType.getColumnTypeByType(columnType) == null) {
			throw new ColumnTypeWrongException("Column type: " + columnType + " not exists.");
		}
		if (container instanceof Column) {
			if (ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay().equals(columnType)) {
				throw new ColumnTypeWrongException("Sublist column type can not be nested.");
			}
		} else if (!(container instanceof PickList)) {
			throw new ColumnTypeWrongException("Container type wrong: " + columnType);
		}
		String bk = generateBusinessKey();
		Context context = Context.findById(container.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.COLUMN, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.COLUMN, context.getBaseClassificationName());
		ColumnImpl column = new ColumnImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		column.setProperty(new PropertyKey(RefsetConstants.COLUMNOF, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(container.getElementIdentifier().getElementId()));
		column.setColumnType(columnType);
		column.setColumnName(columnName);
		if ((container instanceof PickList)
				&& ColumnType.getColumnTypeByType(columnType).getAutoPopulate().equals("Y")) {
			column.populateColumnValues(container);
		}
		return column;

	}

	private void populateColumnValues(RefsetConcept container) {
		ConceptQueryCriteria picklistQuery = new ConceptQueryCriteria(PickListImpl.class, RefsetConstants.COLUMNOF,
				ConceptLoadDegree.REGULAR, null, RefsetConstants.PICKLIST);
		Concept picklist = getReferencedConcept(picklistQuery);
		ConceptQueryCriteria refsetQuery = new ConceptQueryCriteria(RefsetImpl.class, RefsetConstants.PARTOF,
				ConceptLoadDegree.REGULAR, null, RefsetConstants.REFSET);
		Refset refset = (Refset) (picklist.getReferencedConcept(refsetQuery));

		// do not populate Snomed value here
		String classificationStandard = ((PickList) picklist).getClassificationStandard();
		Long classificationContextId = (CIMSConstants.ICD_10_CA.equals(classificationStandard)
				? refset.getICD10CAContextId() : refset.getCCIContextId());
		ColumnType columnType = ColumnType.getColumnTypeByType(getColumnType());
		if ("Y".equals(columnType.getAutoPopulate()) && (classificationContextId != null)
				&& columnType.getClassification().equals(classificationStandard)) {

			Context classificationContext = Context.findById(classificationContextId);

			Classs columnTypeClasss = Classs.findByName(RefsetConstants.COLUMNTYPE,
					getContext().getBaseClassificationName());
			Classs columnOfClasss = Classs.findByName(RefsetConstants.COLUMNOF,
					getContext().getBaseClassificationName());

			ConceptQueryCriteria columnCriteria = new ConceptQueryCriteria(ColumnImpl.class, null,
					ConceptLoadDegree.MINIMAL, new ArrayList<>(), RefsetConstants.COLUMN);
			PropertyCriterion columnProperty = new PropertyCriterion();
			columnProperty.setPropertyType(PropertyType.TextProperty.name());
			columnProperty.setValue(CIMSConstants.ICD_10_CA.equals(classificationStandard)
					? ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay()
					: ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay());
			columnProperty.setClasssId(columnTypeClasss.getClassId());
			columnCriteria.getConditionList().add(columnProperty);

			PropertyCriterion partOfProperty = new PropertyCriterion();
			partOfProperty.setPropertyType(PropertyType.ConceptProperty.name());
			partOfProperty.setValue(container.getElementIdentifier().getElementId());
			partOfProperty.setClasssId(columnOfClasss.getClassId());
			columnCriteria.getConditionList().add(partOfProperty);

			List<ElementIdentifier> columnIds = Concept.findConceptIDsByClassAndValues(getContext().getContextId(),
					columnCriteria);

			Classs describedByClasss = Classs.findByName(RefsetConstants.DESCRIBEDBY,
					getContext().getBaseClassificationName());
			ConceptQueryCriteria valueCriteria = new ConceptQueryCriteria(ValueImpl.class, null,
					ConceptLoadDegree.REGULAR, new ArrayList<>(), RefsetConstants.VALUE);

			PropertyCriterion criterion = new PropertyCriterion();
			criterion.setPropertyType(PropertyType.ConceptProperty.name());
			criterion.setClasssId(describedByClasss.getClassId());
			criterion.setValue(columnIds.stream().map(id -> id.getElementId()).collect(toList()));
			criterion.setOperator(ComparisonOperator.IN.name());
			valueCriteria.getConditionList().add(criterion);

			List<Concept> values = Concept.findConceptsByClassAndValues(getContext().getContextId(), valueCriteria);
			PropertyKey propertyKey = new PropertyKey(columnType.getTextPropertyClasssName(), columnType.getLanguage(),
					PropertyType.TextProperty);
			Map<Long, ca.cihi.cims.framework.domain.Property> propertyCache = new HashMap<>();
			Classs narrower = Classs.findByName("Narrower", classificationContext.getBaseClassificationName());
			Classs chapterOrSection = Classs.findByName(
					CIMSConstants.ICD_10_CA.equals(classificationStandard) ? "Chapter" : "Section",
					classificationContext.getBaseClassificationName());
			for (Concept value : values) {
				Long conceptId = ((Value) value).getIdValue();
				if (columnType.isChapterOrSectionProperty()) {
					conceptId = Concept.findAncestorIdByRelationshipClasssId(classificationContextId,
							chapterOrSection.getClassId(), narrower.getClassId(), conceptId);
				}
				ca.cihi.cims.framework.domain.Property property = propertyCache.get(conceptId);
				if (property == null) {
					property = ca.cihi.cims.framework.domain.Property.loadProperty(classificationContext, conceptId,
							propertyKey);
					propertyCache.put(conceptId, property);
				}
				ConceptQueryCriteria recordQuery = new ConceptQueryCriteria(RecordImpl.class, RefsetConstants.PARTOF,
						ConceptLoadDegree.MINIMAL, new ArrayList<>(), RefsetConstants.RECORD);
				Concept record = value.getReferencedConcept(recordQuery);
				Value newValue = ValueImpl.create((Record) record, this);
				newValue.setIdValue(conceptId);
				String textValue = (String) (property.getValue() != null ? property.getValue().getValue() : "");
				if (columnType.getCodeFomatter() != null) {
					textValue = columnType.getCodeFomatter().format(textValue);
				}
				newValue.setTextValue(textValue);
			}

		}

	}

	/**
	 * assert classs.className='Column' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public ColumnImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(ColumnImpl.class));
		loadProperties(degree);
	}

	/**
	 * - Column( Classs(conceptDTO.classDTO),context, elementIdentifier) - populateProperties(dto)
	 *
	 * @param dto
	 * @param context
	 */
	public ColumnImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.NONE);
		populateProperties(conceptDTO);
	}

	// get methods
	@Override
	public String getColumnName() {
		return (String) (getProperty(RefsetConstants.COLUMNNAME, Language.NOLANGUAGE));
	}

	@Override
	public Short getColumnOrder() {
		try {

			return Short.valueOf((String) (getProperty(RefsetConstants.COLUMNORDER, Language.NOLANGUAGE)));
		} catch (ClassCastException e) {
			return (Short) getProperty(RefsetConstants.COLUMNORDER, Language.NOLANGUAGE);
		} catch (NumberFormatException | NullPointerException e) {
			return null;
		}
	}

	@Override
	public String getColumnType() {
		return (String) (getProperty(RefsetConstants.COLUMNTYPE, Language.NOLANGUAGE));
	}

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

	// set methods
	@Override
	@Property(classsName = RefsetConstants.COLUMNNAME, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setColumnName(String columnName) {

		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.COLUMNNAME, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(columnName));
	}

	@Override
	@Property(classsName = RefsetConstants.COLUMNORDER, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setColumnOrder(Short columnSortOrder) {
		setProperty(
				getConceptMetadata().getPropertyKeys()
						.get(new PropertyKey(RefsetConstants.COLUMNORDER, Language.NOLANGUAGE,
								PropertyType.NumericProperty).generateKeyIdentifier()),
				new PropertyValue(columnSortOrder));
	}

	@Property(classsName = RefsetConstants.COLUMNTYPE, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	private void setColumnType(String columnType) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.COLUMNTYPE, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(columnType));
	}

	@Override
	public ObjectError removable() {
		ObjectError error = null;
		if (getColumnType().equals(ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay())
				|| getColumnType().equals(ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay())) {
			error = new ObjectError("refest.picklist.column.cannotdelete", CIMS_COLUMN_CANNOT_DELETE);
		} else if (getColumnType().equals(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay())) {
			PropertyKey key = new PropertyKey(RefsetConstants.COLUMNOF, Language.NOLANGUAGE,
					PropertyType.ConceptProperty);
			ca.cihi.cims.framework.domain.Property property = getProperty(key);
			Long parentId = Long.parseLong((String) (property.getValue().getValue()));
			Classs relationshipClasss = Classs.findByName(RefsetConstants.COLUMNOF,
					getContext().getBaseClassificationName());
			PropertyCriterion criterion = new PropertyCriterion();
			criterion.setClasssId(relationshipClasss.getClassId());
			criterion.setValue(parentId);
			criterion.setPropertyType(PropertyType.ConceptProperty.name());
			criterion.setOperator(ComparisonOperator.EQUALS.name());
			List<PropertyCriterion> propertyCriteria = new ArrayList<>();
			propertyCriteria.add(criterion);
			ConceptQueryCriteria criteria = new ConceptQueryCriteria(ColumnImpl.class, RefsetConstants.COLUMNOF,
					ConceptLoadDegree.REGULAR, propertyCriteria, RefsetConstants.COLUMN);

			List<Concept> siblings = Concept.findConceptsByClassAndValues(getContext().getContextId(), criteria);
			if (!CollectionUtils.isEmpty(siblings)) {
				for (Concept concept : siblings) {
					Column column = (Column) concept;
					ColumnType columnType = ColumnType.getColumnTypeByType(column.getColumnType());
					if (!column.getElementIdentifier().getElementId().equals(getElementIdentifier().getElementId())
							&& columnType.getAutoPopulate().equals("Y")
							&& columnType.getClassification().equals(CIMSConstants.SCT)) {
						error = new ObjectError("refest.picklist.column.sctconceptid.cannotdelete",
								SCT_CONCEPT_ID_CANNOT_DELETE);
						break;
					}
				}
			}
		}
		return error;
	}

}