package ca.cihi.cims.model.prodpub;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CCIGenericAttributeAuditTest {

	private CCIGenericAttributeAudit bean;

	@Before
	public void setUp() {
		bean = new CCIGenericAttributeAudit();
	}

	@Test
	public void testGetsAndSets() {
		bean.setCode("code");
		bean.setNewDescription("newDescription");
		bean.setOldDescription("oldDescription");
		bean.setReferenceCode("referenceCode");
		assertTrue("Should have  the expected code", bean.getCode().equals("code"));
		assertTrue("Should have  the expected newDescription", bean.getNewDescription().equals("newDescription"));
		assertTrue("Should have  the expected oldDescription", bean.getOldDescription().equals("oldDescription"));
		assertTrue("Should have  the expected referenceCode", bean.getReferenceCode().equals("referenceCode"));

	}
}
