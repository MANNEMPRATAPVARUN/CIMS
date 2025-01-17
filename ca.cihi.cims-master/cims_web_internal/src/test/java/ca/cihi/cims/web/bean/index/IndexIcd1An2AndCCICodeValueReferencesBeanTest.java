package ca.cihi.cims.web.bean.index;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexType;

public class IndexIcd1An2AndCCICodeValueReferencesBeanTest {

	private IndexIcd1An2AndCCICodeValueReferencesBean bean;

	@Before
	public void setUp() {
		bean = new IndexIcd1An2AndCCICodeValueReferencesBean();
	}

	@Test
	public void testGetsAndSets() {
		bean.setBreadCrumbs("breadCrumbs");
		bean.setEditable(true);
		bean.setErrorMessage("errorMessage");
		bean.setLockTimestamp(0L);
		IndexModel im = new IndexModel();
		im.setBookElementId(0L);
		im.setType(IndexType.ICD_ALPHABETIC_INDEX);
		im.setSection(1);
		bean.setModel(im);
		bean.setNodeTitle("nodeTitle");
		bean.setReferences(null);
		bean.setResult(null);
		assertTrue("Should have  the expected breadCrumbs", bean.getBreadCrumbs().equals("breadCrumbs"));
		assertTrue("isEditable", bean.isEditable());
		assertTrue("Should have  the expected errorMessage", bean.getErrorMessage().equals("errorMessage"));
		assertTrue("Should have  LockTimestamp 0", bean.getLockTimestamp() == 0L);
		assertTrue("index Model is not null", bean.getModel() != null);
		assertTrue("Should have  the expected errorMessage", bean.getNodeTitle().equals("nodeTitle"));
		assertTrue("reference is null", bean.getReferences() == null);
		assertTrue("result is null", bean.getResult() == null);
		assertTrue("isICD1", bean.isIcd1());

	}
}
