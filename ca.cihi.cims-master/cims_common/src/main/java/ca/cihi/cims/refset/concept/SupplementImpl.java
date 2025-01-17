package ca.cihi.cims.refset.concept;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.config.annotation.Property;
import ca.cihi.cims.framework.config.annotation.Relationship;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.Element;
import ca.cihi.cims.framework.domain.PropertyValue;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.config.MetadataConfigSource;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Supplement;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:43:46 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.SUPPLEMENT)
@Relationship(classsName = RefsetConstants.PARTOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
public class SupplementImpl extends Concept implements Supplement {

	private static final String SUPPLEMENT_BK_PART = "SUPPLEMENT_";

	/**
	 * Creates a new Supplement including its inception version. Instantiates and returns the appropriate Supplement
	 * object. - pkbk = generatebusinessKey(code) - if Element.existsInContext(pkbk) -- throw duplicate exception -
	 * supplement ElementIdentifier = super.create('Supplement ' , refset.context, pkbk ) - classs =
	 * Classs.findByName('Supplement ', refset.classs. baseClassificationName) -supplement = new Supplement ( classs,
	 * refset.context, supplementElementIdentifier ) - supplement .loadProperties (REGULAR) - supplement
	 * .setProperty(<PropertyKey for PartOf>, refset.elementId); - return supplement
	 *
	 * @param refset
	 * @param code
	 * @throws DuplicateCodeNameException
	 */
	public static Supplement create(Refset refset, String code, String name) throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.SUPPLEMENT,
				RefsetConstants.CODE, code, refset.getContextElementIdentifier(), RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(null, RefsetConstants.SUPPLEMENT,
				RefsetConstants.NAME, name, refset.getContextElementIdentifier(), RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}
		String bk = generateBusinessKey(code);
		if (Element.existsInContext(bk, refset.getContextElementIdentifier().getElementVersionId())) {
			throw new DuplicateCodeNameException("This business key:" + bk + " has already existed within the context");
		}
		Context context = Context.findById(refset.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.SUPPLEMENT, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.SUPPLEMENT, context.getBaseClassificationName());
		SupplementImpl supplement = new SupplementImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		/*
		 * supplement.setProperty( MetadataConfigSource.getMetadata(SupplementImpl.class).getPropertyKeys() .get(new
		 * PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE,
		 * PropertyType.ConceptProperty).generateKeyIdentifier()), new
		 * PropertyValue(refset.getElementIdentifier().getElementId()));
		 */
		supplement.setProperty(
				new PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(refset.getElementIdentifier().getElementId()));

		supplement.setCode(code);
		supplement.setName(name);
		return supplement;
	}

	private static String generateBusinessKey(String code) {
		return new StringBuffer().append(SUPPLEMENT_BK_PART).append(code).toString();
	}

	/**
	 * assert classs.className='Supplement' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public SupplementImpl(Classs classs, Context context, ElementIdentifier elementIdentifier,
			ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(SupplementImpl.class));
		this.loadProperties(degree);
	}

	/**
	 * - Supplement( new Classs(conceptDTO.classDTO), context, conceptDTO. elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */
	public SupplementImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		populateProperties(conceptDTO);
	}

	@Override
	public String getCode() {
		return (String) (getProperty(RefsetConstants.CODE, Language.NOLANGUAGE));
	}

	/**
	 * Returns the content of the supplement.
	 *
	 * return getProperty(RefsetConstants.CONTENT, NOLANGUAGE) cast to byte[]
	 *
	 */
	@Override
	public byte[] getContent(Language language) {
		return (byte[]) (getProperty(RefsetConstants.CONTENT, language));
	}

	@Override
	public String getFilename() {
		return (String) (getProperty(RefsetConstants.FILENAME, Language.NOLANGUAGE));
	}

	@Override
	public String getName() {
		return (String) (getProperty(RefsetConstants.NAME, Language.NOLANGUAGE));
	}

	@Property(classsName = RefsetConstants.CODE, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	private void setCode(String code) throws DuplicateCodeNameException {
		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.SUPPLEMENT, RefsetConstants.CODE, code, getContext().getElementIdentifier(),
				RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The code: " + code + " aleady exists.");
		}
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.CODE, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(code));
	}

	/**
	 * - instantiate value PropertyValue object for name - setProperty(RefsetConstants.CONTENT, NOLANGUAGE, value)
	 *
	 * @param content
	 */
	@Override
	@Property(classsName = RefsetConstants.CONTENT, degrees = { ConceptLoadDegree.COMPLETE,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.XLSXProperty, languages = { Language.ENG,
					Language.FRA })
	public void setContent(byte[] content, Language language) {
		PropertyValue value = new PropertyValue();
		value.setValue(content);
		setProperty(new PropertyKey(RefsetConstants.CONTENT, language, PropertyType.XLSXProperty), value);
	}

	/**
	 *
	 * @param filename
	 */
	@Override
	@Property(classsName = RefsetConstants.FILENAME, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setFilename(String filename) {
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.FILENAME, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(filename));
	}

	/**
	 *
	 * @param name
	 * @throws DuplicateNameException
	 */
	@Override
	@Property(classsName = RefsetConstants.NAME, degrees = { ConceptLoadDegree.COMPLETE, ConceptLoadDegree.MINIMAL,
			ConceptLoadDegree.REGULAR }, propertyType = PropertyType.TextProperty)
	public void setName(String name) throws DuplicateCodeNameException {

		if (ca.cihi.cims.framework.domain.Property.checkDuplicateValue(getElementIdentifier().getElementId(),
				RefsetConstants.SUPPLEMENT, RefsetConstants.NAME, name, getContext().getElementIdentifier(),
				RefsetConstants.BASEREFSET)) {
			throw new DuplicateCodeNameException("The name: " + name + " aleady exists.");
		}
		setProperty(getConceptMetadata().getPropertyKeys()
				.get(new PropertyKey(RefsetConstants.NAME, Language.NOLANGUAGE, PropertyType.TextProperty)
						.generateKeyIdentifier()),
				new PropertyValue(name));
	}

}