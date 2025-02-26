package ca.cihi.cims.refset.concept;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.config.annotation.Property;
import ca.cihi.cims.framework.config.annotation.Relationship;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.PropertyValue;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.config.MetadataConfigSource;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Value;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:43:57 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.VALUE)
@Relationship(classsName = RefsetConstants.PARTOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
@Relationship(classsName = RefsetConstants.DESCRIBEDBY, degrees = { ConceptLoadDegree.MINIMAL,
		ConceptLoadDegree.REGULAR, ConceptLoadDegree.COMPLETE })
public class ValueImpl extends Concept implements Value {

	/**
	 * assert that the container is a Record - else throw system exception
	 *
	 * Creates a new Value including its inception version. Instantiates and returns the appropriate Value object. pkbk
	 * = generatebusinessKey()
	 *
	 * - elementIdentifier = super.create('Value' , container.context, pkbk ) - classs = Classs.findByName('Value',
	 * container.classs.baseClassificationName) - value= new Value( classs, container.context, elementIdentifier ) -
	 * value.loadProperties (REGULAR) - value.setProperty(<PropertyKey for PartOf>, container.elementId); -
	 * value.setProperty(<PropertyKey for DescribedBy>, column.elementId); - return value
	 *
	 * @param container
	 * @param column
	 */
	public static Value create(Record container, Column column) {
		assert container instanceof Record;
		String bk = generateBusinessKey();
		Context context = Context.findById(container.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.VALUE, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.VALUE, context.getBaseClassificationName());
		ValueImpl value = new ValueImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		value.setProperty(
				MetadataConfigSource.getMetadata(ValueImpl.class).getPropertyKeys()
						.get(new PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE, PropertyType.ConceptProperty)
								.generateKeyIdentifier()),
				new PropertyValue(container.getElementIdentifier().getElementId()));
		value.setProperty(
				MetadataConfigSource.getMetadata(ValueImpl.class).getPropertyKeys()
						.get(new PropertyKey(RefsetConstants.DESCRIBEDBY, Language.NOLANGUAGE,
								PropertyType.ConceptProperty).generateKeyIdentifier()),
				new PropertyValue(column.getElementIdentifier().getElementId()));
		return value;
	}

	/**
	 * assert classs.className='Value' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */

	public ValueImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(ValueImpl.class));
		this.loadProperties(degree);
	}

	/**
	 * - Value( new Classs(conceptDTO.classDTO), context, conceptDTO. elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */
	public ValueImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		populateProperties(conceptDTO);
	}

	@Override
	public Long getIdValue() {
		Object idValue = getProperty(RefsetConstants.IDVALUE, Language.NOLANGUAGE);
		if (idValue == null) {
			return null;
		} else {
			if (idValue instanceof String) {
				return Long.parseLong((String) idValue);
			} else {
				return (Long) idValue;
			}
		}
	}

	@Override
	public String getTextValue() {
		return (String) (getProperty(RefsetConstants.TEXTVALUE, Language.NOLANGUAGE));
	}

	/**
	 *
	 * @param idValue
	 */
	@Override
	@Property(classsName = RefsetConstants.IDVALUE, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.NumericProperty)
	public void setIdValue(Long idValue) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.IDVALUE, Language.NOLANGUAGE, PropertyType.NumericProperty)
						.generateKeyIdentifier()),
				new PropertyValue(idValue));
	}

	/**
	 *
	 * @param textValue
	 */
	@Override
	@Property(classsName = RefsetConstants.TEXTVALUE, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setTextValue(String textValue) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.TEXTVALUE, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(textValue));
	}

	// help methods

	@Override
	public Long getDescribedBy() {
		PropertyKey describeByKey = new PropertyKey(RefsetConstants.DESCRIBEDBY, Language.NOLANGUAGE,
				PropertyType.ConceptProperty);
		Object describedBy = getProperty(describeByKey).getValue().getValue();
		if (describedBy == null) {
			return null;
		} else {
			if (describedBy instanceof String) {
				return Long.parseLong((String) describedBy);
			}
			return (Long) (describedBy);
		}
	}
}