package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.mapper.ElementMapper;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 11:23:28 AM
 */
@Component("frameworkElementHandler")
public class ElementHandler {

	@Autowired
	private ElementMapper elementMapper;

	/**
	 * Handles the element and elementversion records part of the creation of a new context version
	 *
	 * @param classsId
	 * @param elementId
	 * @param versionCode
	 * @throws WrongClasssException
	 */
	public Long createContextVersion(Long classsId, Long elementId, String versionCode) {
		// - check that class with classId
		// indicates a context element (tablename
		// in the class record should be
		// BaseRefset), throw exception if not
		// -- insert into elementversion (<system
		// generated elementVersionId>,elementId,
		// versionCode,SYSDATE,ACTIVE,null,
		// classId, null , null)
		// -- return
		Map<String, Object> params = new HashMap<>();
		params.put("classsId", classsId);
		params.put("elementId", elementId);
		params.put("versionCode", versionCode);
		params.put("contextId", null);
		elementMapper.createContextVersion(params);

		return (Long) params.get("contextId");
	}

	/**
	 * Creates a new element record and returns its elementId.
	 *
	 * @param classId
	 * @param key
	 */
	protected Long createElement(Long classsId, String key) {
		// - If element with business key already
		// exits, return elementId for it
		// - Else
		// -- insert in element table ( <system
		// generated elementId>, classId, key,
		// null)
		// -- return elementId

		Map<String, Object> params = new HashMap<>();
		params.put("classsId", classsId);
		params.put("key", key);
		params.put("elementId", null);
		elementMapper.createElement(params);

		return (Long) params.get("elementId");
	}

	/**
	 * Used for create element version (not context version), so has to make sure contextId is not null
	 *
	 * @param contextId
	 * @param elementId
	 * @param changedFromVersionId
	 * @param originatingContextId
	 * @return elementVersionId of the newly created version
	 */
	private Long createElementVersionInContext(Long contextId, Long elementId, Long changedFromVersionId) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("elementId", elementId);
		params.put("changedFromVersionId", changedFromVersionId);
		params.put("elementVersionId", null);
		elementMapper.createElementVersionInContext(params);
		return (Long) params.get("elementVersionId");
	}

	/**
	 * Verifies element with input business key exists in context. Returns true if element version is found.
	 *
	 * @param businessKey
	 * @param contextId
	 */
	public Boolean existsInContext(String businessKey, Long contextId) {
		// Please see query for reference:
		// select * from element e,
		// structureelementversion sev
		// where sev.structureid = 5251589
		// and sev.elementid = e.elementid
		// and e.elementuuid = 'ICD-10-CA:
		// ConceptVersion:Category:A00.9'
		Integer count = elementMapper.countInContext(businessKey, contextId);
		return ((count != null) && (count > 0)) ? Boolean.TRUE : Boolean.FALSE;
	}

	public ElementDTO findElementInContext(Long contextId, Long elementId) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("elementId", elementId);
		return elementMapper.findElementInContext(params);
	}

	/**
	 * Updates the element in the specified context Returns elementversionid of the element in the context.
	 *
	 * @param contextId
	 * @param elementId
	 */
	public Long updateElementInContext(Long contextId, Long elementId) {
		// - assert elementid > 0 and element
		// record for elementid exists
		// - assert that the element is not a
		// context
		//
		// - if a version v1 of the elementId
		// exists in the context
		// ---- if v1.originatingcontext is same
		// as context
		// ------ update the timestamp in element
		// version table
		// ---- else
		// ------ create a new version v2 as
		// follows
		// -------- insert into elementversion
		// (elementId, versionCode,SYSDATE,ACTIVE,
		// null, classId, v1.elementVersionId ,
		// contextId)
		// - else
		// ------ create a new version v2 as
		// follows
		// --------- cfvd=null
		// -------- check if the element exists in
		// the baseContext of the context, if yes
		// cfvd=elementversionid of the element in
		// the base context
		// -------- create v2 : insert into
		// elementversion (elementId, versionCode,
		// SYSDATE,ACTIVE,null, classId, cfvd,
		// contextId)
		// - if v2 was created - link v2 to
		// ContextId and unlink v1 from contextId
		// if exists
		//
		// Notes.
		// Originating contextId - context where
		// the version was created so is the input
		// contextId
		// changedFromVersionId - the previous
		// version of the this element

		ElementDTO element = findElementInContext(contextId, elementId);
		if (element != null) {
			if (!contextId.equals(element.getOriginatingContextId())) {
				// not created in this context, create new version
				return createElementVersionInContext(contextId, elementId,
						element.getElementIdentifier().getElementVersionId());
			} else {
				elementMapper.updateVersionTimestamp(element.getElementIdentifier().getElementVersionId());
				return element.getElementIdentifier().getElementVersionId();
			}
		} else {
			// TODO check if element is a context or not
			return createElementVersionInContext(contextId, elementId, null);
		}
	}

}