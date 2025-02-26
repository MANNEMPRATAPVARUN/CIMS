package ca.cihi.cims.refset.concept;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
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
import ca.cihi.cims.refset.service.concept.Sublist;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:44:09 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.SUBLIST)
@Relationship(classsName = RefsetConstants.PARTOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
@Relationship(classsName = RefsetConstants.DESCRIBEDBY, degrees = { ConceptLoadDegree.MINIMAL,
		ConceptLoadDegree.REGULAR, ConceptLoadDegree.COMPLETE })
public class SublistImpl extends RecordsListImpl implements Sublist {

	/**
	 * assert that the container is a Record - else throw system exception
	 *
	 * Creates a new Sublist including its inception version. Instantiates and returns the appropriate Sublist object.
	 * pkbk = generatebusinessKey()
	 *
	 * - elementIdentifier = super.create('Sublist ', container.context, pkbk ) - classs = Classs.findByName('Sublist ',
	 * container.classs. baseClassificationName) - sublist = new Sublist ( classs, container.context, elementIdentifier
	 * ) - sublist .loadProperties (REGULAR) - sublist .setProperty(<PropertyKey for PartOf>, container.elementId); -
	 * sublist .setProperty(<PropertyKey for DescribedBy>, column.elementId); - return sublist
	 *
	 * @param container
	 * @param column
	 */
	public static Sublist create(Record container, Column column) {
		// assert container instanceof Sublist;
		String bk = generateBusinessKey();
		Context context = Context.findById(container.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.SUBLIST, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.SUBLIST, context.getBaseClassificationName());
		SublistImpl sublist = new SublistImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		sublist.setProperty(new PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(container.getElementIdentifier().getElementId()));
		sublist.setProperty(
				new PropertyKey(RefsetConstants.DESCRIBEDBY, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(column.getElementIdentifier().getElementId()));
		return sublist;
	}

	/**
	 * assert classs.className='Sublist' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public SublistImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(SublistImpl.class));
		loadProperties(degree);
	}

	/**
	 * - Sublist( new Classs(conceptDTO.classDTO), context, conceptDTO. elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */

	public SublistImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		populateProperties(conceptDTO);
	}

}