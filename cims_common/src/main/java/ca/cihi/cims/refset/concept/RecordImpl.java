package ca.cihi.cims.refset.concept;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.config.annotation.Relationship;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.Element;
import ca.cihi.cims.framework.domain.PropertyValue;
import ca.cihi.cims.framework.dto.ConceptDTO;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.config.MetadataConfigSource;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.service.concept.RecordsList;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Sublist;
import ca.cihi.cims.refset.service.concept.Value;

/*
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:44:22 PM
 */
@ca.cihi.cims.framework.config.annotation.Concept(classsName = RefsetConstants.RECORD)
@Relationship(classsName = RefsetConstants.PARTOF, degrees = { ConceptLoadDegree.MINIMAL, ConceptLoadDegree.REGULAR,
		ConceptLoadDegree.COMPLETE })
public class RecordImpl extends Concept implements Record {

	/**
	 * - assert that the container is either a PickList or Sublist - else throw system exception Creates a new Record
	 * including its inception version. Instantiates and returns the appropriate Record object. - If code specified -
	 * pkbk = generatebusinessKey(code) - if Element.existsInContext(pkbk) -- throw duplicate exeception - else pkbk =
	 * generatebusinessKey()
	 *
	 * - elementIdentifier = super.create('Record' , container.context, pkbk ) - classs = Classs.findByName('Record',
	 * container.classs. baseClassificationName) - record= new Record( classs, container.context, elementIdentifier ) -
	 * record.loadProperties (REGULAR) - record.setProperty(<PropertyKey for PartOf>, container.elementId); - return
	 * record
	 *
	 * @param container
	 * @param code
	 *            this code will be the ICD/CCI conceptCode of the record or main record
	 */
	public static Record create(RecordsList container, String code) {
		assert (container instanceof PickList) || (container instanceof Sublist);
		String bk = code != null ? generateBusinessKey(code) : generateBusinessKey();
		Context context = Context.findById(container.getContextElementIdentifier().getElementVersionId());
		ElementIdentifier elementIdentifier = Concept.create(RefsetConstants.RECORD, context, bk);
		Classs classs = Classs.findByName(RefsetConstants.RECORD, context.getBaseClassificationName());
		RecordImpl record = new RecordImpl(classs, context, elementIdentifier, ConceptLoadDegree.REGULAR);
		record.setProperty(new PropertyKey(RefsetConstants.PARTOF, Language.NOLANGUAGE, PropertyType.ConceptProperty),
				new PropertyValue(container.getElementIdentifier().getElementId()));
		return record;
	}

	private static String generateBusinessKey(String code) {
		return new StringBuffer().append(generateBusinessKey()).append(RefsetConstants.RECODE_BK_PART).append(code)
				.toString();
	}

	/**
	 * assert classs.className='Record' super(classs, context, elementIdentifier, metadata)
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public RecordImpl(Classs classs, Context context, ElementIdentifier elementIdentifier, ConceptLoadDegree degree) {
		super(classs, context, elementIdentifier, MetadataConfigSource.getMetadata(RecordImpl.class));
		this.loadProperties(degree);
	}

	/**
	 * - Record( Classs(conceptDTO.classDTO),context, elementIdentifier) - populateProperties(dto)
	 *
	 * @param conceptDTO
	 * @param context
	 */

	public RecordImpl(ConceptDTO conceptDTO, Context context) {
		this(new Classs(conceptDTO.getClasss()), context, conceptDTO.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		populateProperties(conceptDTO);
	}

	/**
	 * call this.getRelatedConcepts('PartOf',MINIMAL, 'Value' ) return list
	 *
	 * @throws Exception
	 */
	@Override
	public java.util.List<Value> listValues() {
		List<Concept> conceptList = this.getReferencingConcepts(new ConceptQueryCriteria(ValueImpl.class,
				RefsetConstants.PARTOF, ConceptLoadDegree.REGULAR, null, RefsetConstants.VALUE));
		List<Value> columnRecordList = new ArrayList<Value>();
		for (Concept concept : conceptList) {
			columnRecordList.add((Value) concept);
		}
		return columnRecordList;
	}

	@Override
	public String getConceptCode() {
		ElementDTO element = Element.findElementInContext(getContext().getContextId(),
				getElementIdentifier().getElementId());
		if (element.getElementUUID().contains(RefsetConstants.RECODE_BK_PART)) {
			return element.getElementUUID()
					.substring(element.getElementUUID().indexOf(RefsetConstants.RECODE_BK_PART) + 3);
		} else {
			return null;
		}
	}

}