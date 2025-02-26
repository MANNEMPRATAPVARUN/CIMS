package ca.cihi.cims.refset.concept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.ConceptMetadata;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;
import ca.cihi.cims.framework.handler.SearchHandler;
import ca.cihi.cims.refset.config.RefsetConstants;
import ca.cihi.cims.refset.dto.LightRecordDTO;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.RecordsList;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:43:18 PM
 */
public class RecordsListImpl extends Concept implements RecordsList {

	protected RecordsListImpl(Classs classs, Context context, ElementIdentifier elementIdentifier,
			ConceptMetadata conceptMetadata) {
		super(classs, context, elementIdentifier, conceptMetadata);
	}

	/**
	 * Returns a list of LightRecord objects.
	 */
	@Override
	public List<LightRecord> listRecords() {
		// - get PartOf relationship classId
		// - get class IDs for classes TextValue,
		// IDValue, DescribedBy and store in
		// propertyClassIds(list)
		// - hList = searchHandler.
		// searchHierarchy(elementIdentifier.
		// elementId, contex.contextId,
		// relationshipClassId, propertyClassIds)
		// - process hList and create a list of
		// LightRecord objects ordered by the Code
		// Note.The Sublist column values in the
		// record should concatenate all the
		// values in its Column.firstChildColumn
		// only
		//
		// Note2. If performance not good enough,
		// we may want to use a specialized query
		// to get the picklist record information
		// to be displayed in the PickListView
		// screen
		//
		SearchHandler searchHandler = ApplicationContextProvider.getApplicationContext().getBean(SearchHandler.class);
		Classs partOfClasss = Classs.findByName(RefsetConstants.PARTOF, getContext().getBaseClassificationName());
		Classs describedByClasss = Classs.findByName(RefsetConstants.DESCRIBEDBY,
				getContext().getBaseClassificationName());
		Classs textValueClasss = Classs.findByName(RefsetConstants.TEXTVALUE, getContext().getBaseClassificationName());
		Classs idValueClasss = Classs.findByName(RefsetConstants.IDVALUE, getContext().getBaseClassificationName());
		List<String> classsNames = new ArrayList<String>();
		classsNames.add(RefsetConstants.TEXTVALUE);
		classsNames.add(RefsetConstants.IDVALUE);
		classsNames.add(RefsetConstants.DESCRIBEDBY);
		List<Classs> propertyClassss = Classs.findByNames(classsNames, getContext().getBaseClassificationName());
		List<Long> propertyClassssIds = new ArrayList<Long>();
		for (Classs propertyClass : propertyClassss) {
			propertyClassssIds.add(propertyClass.getClassId());
		}
		List<PropertyHierarchyDTO> hierarchyDTOList = searchHandler.searchHierarchyForProperties(
				getElementIdentifier().getElementId(), getContext().getContextId(), partOfClasss.getClassId(),
				propertyClassssIds, 3);
		List<LightRecord> recordList = new ArrayList<LightRecord>();

		// first level record
		// find all the relationship properties which value (rangeelementid) is the id of the current concept elementid
		// the conceptid will be the elementidentifier of all the records under a picklist, which is the base of our
		// lightrecord
		hierarchyDTOList.stream()
				.filter(dto -> dto.getPropertyClasssId().equals(partOfClasss.getClassId())
						&& dto.getParentId().getElementId().equals(getElementIdentifier().getElementId()))
				.forEach(dto -> { // those are
					// all the
					// direct
					// record
					// concepts
					// of the
					// current
					// picklist
					LightRecordDTO record = new LightRecordDTO();
					record.setRecordIdentifier(dto.getConceptId());
					record.setValues(new HashMap<>());
					recordList.add(record);
				});

		// find value properties
		recordList.stream().forEach(record -> {
			// find all the properties for the given records and group by the conceptId
			Map<ElementIdentifier, List<PropertyHierarchyDTO>> dtoMaps = hierarchyDTOList.stream()
					.filter(dto -> (!dto.getPropertyClasssId().equals(partOfClasss.getClassId())
							&& (dto.getParentId().getElementId().equals(record.getRecordIdentifier().getElementId()))))
					.collect(Collectors.groupingBy(PropertyHierarchyDTO::getConceptId));
			for (ElementIdentifier conceptId : dtoMaps.keySet()) {
				processProperties(record, dtoMaps.get(conceptId), describedByClasss, idValueClasss, textValueClasss);
			}
		});

		return recordList;
	}

	private void processProperties(LightRecord record, List<PropertyHierarchyDTO> properties, Classs describedByClasss,
			Classs idValueClasss, Classs textValueClasss) {
		ValueDTO value = new ValueDTO();
		Long key = null;
		for (PropertyHierarchyDTO dto : properties) {
			if (dto.getPropertyClasssId().equals(describedByClasss.getClassId())) {
				key = Long.parseLong(dto.getPropertyValue());
			} else if (dto.getPropertyClasssId().equals(textValueClasss.getClassId())) {
				value.setTextValue(dto.getPropertyValue());
			} else if (dto.getPropertyClasssId().equals(idValueClasss.getClassId())) {
				value.setIdValue(Long.parseLong(dto.getPropertyValue()));
			}
		}
		if (key != null) {
			record.getValues().put(key, value);
		}
	}

}