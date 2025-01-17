package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;
import ca.cihi.cims.framework.mapper.SearchMapper;

/**
 * @author tyang
 * @version 1.0
 * @created 10-Jun-2016 3:53:43 PM
 */
@Component
public class SearchHandler {

	@Autowired
	@Qualifier("frameworkSearchMapper")
	private SearchMapper searchMapper;

	/**
	 * Searches the whole hierarchy starting with startWithConceptId using relationshipId and returns all concepts
	 * including their specified properties as a list of HierarchyDTO. The list of retrieved PropertyHierarchyDTO
	 * objects always includes properties that represent the relationship used to build the hierarchy even if not
	 * specified in the propertyClassIds. This is to ensure that all concepts part of the hierachy are retrieved even if
	 * if they don't have properties specified in the inputpropertyClassIds.
	 *
	 * @param startWithConceptId
	 * @param contextId
	 * @param relationshipId
	 * @param propertyClassIds
	 */
	public List<PropertyHierarchyDTO> searchHierarchyForProperties(Long startWithConceptId, Long contextId,
			Long relationshipId, List<Long> propertyClassIds, Integer level) {
		// For reference pls see follwoing query
		// that returns all concepts in Chapter 19:
		//
		//
		// -- select * from element where elementuuid like '%Chapter%'
		// with relationships as
		// (
		// select /*+ INLINE*/ r.classid, r.conceptpropertyid, r.domainelementid, r.elementid, r.rangeelementid
		// from structureelementversion sev, conceptpropertyversion r
		// where sev.structureid = 5251589 and r.conceptpropertyid = sev.elementversionid and r.classid=10
		// ) , concepts as
		// (
		// select /*+ INLINE*/ c.classid, c.conceptid, c.elementid, c.status
		// from structureelementversion sev, conceptversion c
		// where sev.structureid = 5251589 and c.conceptid = sev.elementversionid and c.status = 'ACTIVE'
		// ) , properties as
		// (
		// select 'TextProperty' propertyType, pv.languagecode propertyLanguage, pv.classid propertyClassId, pv.text
		// propertyValue,
		// pv.textpropertyid propertyVersionId, pv.elementid propertyElementId, pv.domainelementid
		// from textpropertyversion pv, structureelementversion sev
		// where sev.structureid = 5251589 and sev.elementversionid = pv.textpropertyid and classid in ( 7 , 10 )
		// UNION
		// select 'NumericProperty' propertyType, 'NOLANGUAGE' propertyLanguage, pv.classid propertyClassId,
		// ''||pv.numericvalue propertyValue,
		// pv.numericpropertyid propertyVersionId, pv.elementid propertyElementId, pv.domainelementid
		// from numericpropertyversion pv, structureelementversion sev
		// where sev.structureid = 5251589 and sev.elementversionid = pv.numericpropertyid
		// UNION
		// select 'ConceptProperty' propertyType, 'NOLANGUAGE' propertyLanguage, pv.classid propertyClassId,
		// ''||pv.rangeElementId propertyValue,
		// pv.conceptpropertyid propertyVersionId, pv.elementid propertyElementId, pv.domainelementid
		// from conceptpropertyversion pv, structureelementversion sev
		// where sev.structureid = 5251589 and sev.elementversionid = pv.conceptpropertyid and pv.classid in ( 7 , 10 )
		// ) , hierarchy as(
		// select /*+ INLINE*/ r.rangeelementid , r.domainelementid, c.classid, c.conceptid
		// from relationships r, concepts c
		// where r.domainelementid = c.elementid
		// connect by prior r.domainelementid = r.rangeelementid
		// start with r.domainelementid = 250823
		// )
		// select h.rangeelementid pael_elementid, sev.elementversionid pael_elementversionid, p.domainelementid
		// cel_elementid,
		// h.conceptid cel_elementversionid, h.classId conceptClassid, p.propertyClassId, p.propertyElementId
		// prel_elementid,
		// p.propertyversionid prel_elementversionid, p.propertyLanguage, p.propertyValue propertyValue
		// from hierarchy h, properties p, structureelementversion sev
		// where h.domainelementid = p.domainelementid and sev.structureid=5251589 and sev.elementid=h.rangeelementid
		// order by cel_elementid, prel_elementid
		//

		propertyClassIds.add(relationshipId);

		Map<String, Object> params = new HashMap<>();
		params.put("startWithConceptId", startWithConceptId);
		params.put("contextId", contextId);
		params.put("relationshipId", relationshipId);
		params.put("propertyClassIds", propertyClassIds);
		params.put("level", level);
		return searchMapper.searchHierarchy(params);
	}

}