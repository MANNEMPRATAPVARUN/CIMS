package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AttributeInfoTest {

	private AttributeInfo attributeInfo;

	@Before
	public void setUp() {
		attributeInfo = new AttributeInfo();
	}

	@Test
	public void testGetsAndSets() {
		String statusRef = "S07";
		String locationRef = "L09";
		String extentRef = "E03";

		attributeInfo.setStatusRef(statusRef);
		attributeInfo.setExtentRef(extentRef);
		attributeInfo.setLocationRef(locationRef);

		assertTrue("Should have the expected statusRef", statusRef == attributeInfo.getStatusRef());
		assertTrue("Should have the expected locationRef", locationRef == attributeInfo.getLocationRef());
		assertTrue("Should have the expected extentRef", extentRef == attributeInfo.getExtentRef());

	}

}
