package ca.cihi.cims.web.bean.index;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.index.IndexModel;

public class IndexIcd3An4CodeValueReferencesBean extends CodeValueReferencesBean {

	private Map<String, IndexTabularReferenceBean> references = new HashMap<String, IndexTabularReferenceBean>();

	// ------------------------------------------

	public Map<String, IndexTabularReferenceBean> getReferences() {
		return references;
	}

	public boolean isIcd3() {
		IndexModel model = getModel();
		Classification classification = model.getType().getClassification();
		return classification == Classification.ICD && model.getSection() == 3;
	}

	public void setReferences(Map<String, IndexTabularReferenceBean> references) {
		this.references = references;
	}

}
