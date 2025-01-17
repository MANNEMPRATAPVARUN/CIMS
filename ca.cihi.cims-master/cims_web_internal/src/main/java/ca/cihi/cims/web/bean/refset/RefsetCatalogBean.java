package ca.cihi.cims.web.bean.refset;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * @author pzhu
 */
public class RefsetCatalogBean extends BaseSerializableCloneableObject {
	
	private static final long serialVersionUID = 8225544886068833171L;

	private String refsetName;
	private String category;
	private String refsetVersionName;
	
	private Long contextId;
	private Long elementId;
	private Long elementVersionId;
	
	//------------------------------------------------------
	
	public String getRefsetName() {
		return refsetName;
	}
	public void setRefsetName(String refsetName) {
		this.refsetName = refsetName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getRefsetVersionName() {
		return refsetVersionName;
	}
	public void setRefsetVersionName(String refsetVersionName) {
		this.refsetVersionName = refsetVersionName;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public Long getElementId() {
		return elementId;
	}
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	public Long getElementVersionId() {
		return elementVersionId;
	}
	public void setElementVersionId(Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}
    @Override
    public String toString() {
        return "RefsetCatalogBean [refsetName=" + refsetName + ", category=" + category + ", refsetVersionName="
                + refsetVersionName + ", contextId=" + contextId + ", elementId=" + elementId + ", elementVersionId="
                + elementVersionId + "]";
    }
 }
