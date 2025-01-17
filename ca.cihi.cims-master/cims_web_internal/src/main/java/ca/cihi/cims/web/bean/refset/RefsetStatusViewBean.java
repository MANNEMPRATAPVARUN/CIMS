package ca.cihi.cims.web.bean.refset;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.refset.service.concept.RefsetVersion;

public class RefsetStatusViewBean extends BaseSerializableCloneableObject {
	private static final long serialVersionUID = 1L;

	private List<RefsetVersion> refsetVersionList = new ArrayList<>();
	private String statusGroup;
	private String contextId;
	private String elementId;
	private String elementVersionId;
	private String newStatus;

	public List<RefsetVersion> getRefsetVersionList() {
		return refsetVersionList;
	}

	public void setRefsetVersionList(List<RefsetVersion> refsetVersionList) {
		this.refsetVersionList = refsetVersionList;
	}

	public String getStatusGroup() {
		return statusGroup;
	}

	public void setStatusGroup(String statusGroup) {
		this.statusGroup = statusGroup;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getElementVersionId() {
		return elementVersionId;
	}

	public void setElementVersionId(String elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

}
