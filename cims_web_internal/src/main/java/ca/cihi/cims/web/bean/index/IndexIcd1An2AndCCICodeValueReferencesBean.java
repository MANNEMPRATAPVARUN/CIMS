package ca.cihi.cims.web.bean.index;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.index.IndexModel;

public class IndexIcd1An2AndCCICodeValueReferencesBean extends CodeValueReferencesBean {

	private List<IndexCategoryReferenceBean> references = new ArrayList<IndexCategoryReferenceBean>();

	// ------------------------------------------

	public List<IndexCategoryReferenceBean> getReferences() {
		return references;
	}

	public boolean isIcd1() {
		IndexModel model = getModel();
		Classification classification = model.getType().getClassification();
		return classification == Classification.ICD && model.getSection() == 1;
	}

	public void setReferences(List<IndexCategoryReferenceBean> references) {
		this.references = references;
	}

}
