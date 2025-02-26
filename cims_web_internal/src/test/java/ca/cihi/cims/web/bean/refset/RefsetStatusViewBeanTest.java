package ca.cihi.cims.web.bean.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class RefsetStatusViewBeanTest {
	private RefsetStatusViewBean viewBean = new RefsetStatusViewBean();

	@Before
	public void setup() {
		viewBean.setContextId("1");
		viewBean.setElementVersionId("22");
		viewBean.setElementId("123");
		viewBean.setStatusGroup("333");
		viewBean.setNewStatus("Active");
	}

	@Test
	public void testGetContextId() {
		assertEquals("1", viewBean.getContextId());
	}

	@Test
	public void testGetElementVersionId() {
		assertEquals("22", viewBean.getElementVersionId());
	}

	@Test
	public void testGetElementId() {
		assertEquals("123", viewBean.getElementId());
	}

	@Test
	public void testGetStatusGroup() {
		assertEquals("333", viewBean.getStatusGroup());
	}

	@Test
	public void testGetNewStatus() {
		assertEquals("Active", viewBean.getNewStatus());
	}

	@Test
	public void testGetRefsetVersionList() {
		assertNotNull(viewBean.getRefsetVersionList());
	}

}
