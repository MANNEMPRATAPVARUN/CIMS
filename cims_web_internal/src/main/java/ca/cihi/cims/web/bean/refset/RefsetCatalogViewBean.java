package ca.cihi.cims.web.bean.refset;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * @author pzhu
 */
public class RefsetCatalogViewBean extends BaseSerializableCloneableObject {
	
	private static final long serialVersionUID = 1L;

	private List<RefsetCatalogBean> refsetCatalogBeanList = new ArrayList<RefsetCatalogBean>();
	private String refsetCategory;
	
	//-------------------------------------------------------------

	public List<RefsetCatalogBean> getRefsetCatalogBeanList() {
		return refsetCatalogBeanList;
	}

	public void setRefsetCatalogtBeanList(List<RefsetCatalogBean> refsetCatalogBeanList) {
		this.refsetCatalogBeanList = refsetCatalogBeanList;
	}

	public String getRefsetCategory() {
		return refsetCategory;
	}

	public void setRefsetCategory(String refsetCategory) {
		this.refsetCategory = refsetCategory;
	}
		
}
