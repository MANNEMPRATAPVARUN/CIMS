package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.dto.ContextDTO;
import ca.cihi.cims.framework.mapper.ContextMapper;

@Component
public class ContextHandler {

	@Autowired
	@Qualifier("frameworkContextMapper")
	private ContextMapper contextMapper;

	@Autowired
	private ElementHandler elementHandler;

	/**
	 * super.CreateElement(classId, key)
	 *
	 * @param classId
	 * @param key
	 */
	public Long createContextElement(Long classsId, String key) {
		return elementHandler.createElement(classsId, key);
	}

	/**
	 * elementVersionId = super.createContextVersion(classId, elementId, versionCode) insert into
	 * structureVersion(elementVersionId, classId, 'ACTIVE', elementId , baseContextid, 'OPEN', SYSDATE, 'N',null, null)
	 * -- if basecontextid is not null ---- insert into structureelementversion select sev.elementversionid,
	 * elementVersionId(from above), elementid, null, null,null from structureelementversion sev where
	 * sev.structureid=baseContextId
	 *
	 * return elementVersionId
	 *
	 * @param classId
	 * @param elementId
	 * @param versionCode
	 * @param baseContextId
	 * @throws WrongClasssException
	 */
	public Long createContextVersion(Long classsId, Long elementId, String versionCode, Long baseContextId) {
		Long elementVersionId = elementHandler.createContextVersion(classsId, elementId, versionCode);
		Map<String, Object> params = new HashMap<>();
		params.put("elementVersionId", elementVersionId);
		params.put("classsId", classsId);
		params.put("elementId", elementId);
		params.put("baseContextId", baseContextId);
		contextMapper.createStructure(params);
		return elementVersionId;
	}

	/**
	 * Finds the context record and returns the ContextDTO object for it
	 *
	 * @param contextId
	 */
	public ContextDTO getContext(Long contextId) {
		return contextMapper.findContextDTO(contextId);
	}

	/**
	 * When removing a context: -- select ev.elementid from elementversion ev, structureelementversion sev where
	 * ev.changedFromVersionId is null and ev.elementversionId = sev.elementversionId and sev.structureid = contextid -
	 * save this in elementlist
	 *
	 * - detach everything -- delete structureelementversion where structureid=contextid - delete everything where
	 * originatingcontextid= contextId - delete from elementversion( and structureversion) where structureid=contextid -
	 * delete from element where elementid in (elementlist)
	 *
	 * - delete select elementid into elid from elementversion ev where ev.changedFromVesrionId is null and
	 * ev.elementvesrionId = contextid delete from element where elementid = elid
	 *
	 * @param contextId
	 */
	public void remove(Long contextId) {
		contextMapper.remove(contextId);
	}

	/**
	 *
	 * @param contextId
	 * @param status
	 */
	public void closeContext(Long contextId) {
		ContextDTO context = getContext(contextId);
		if (context != null) {
			contextMapper.closeContext(contextId);
		}
	}

	public Long getLatestClosedVersion(Long elementId) {
		return contextMapper.getLatestClosedVersion(elementId);
	}

	public int getOpenVersionCount(Long elementId) {
		return contextMapper.getOpenVersionCount(elementId);
	}
}
