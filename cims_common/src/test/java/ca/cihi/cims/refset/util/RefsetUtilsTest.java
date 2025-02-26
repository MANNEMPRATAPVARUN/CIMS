package ca.cihi.cims.refset.util;

import org.junit.Test;

import ca.cihi.cims.refset.config.RefsetConstants;

import static org.junit.Assert.assertTrue;

public class RefsetUtilsTest {

	@Test
	public void testGenerateVersionCode() {
		assertTrue("V1.1".equals(RefsetUtils.generateVersionCode("V1.0", "minor")));
		assertTrue("V1.30".equals(RefsetUtils.generateVersionCode("V1.29", "minor")));
		assertTrue(RefsetConstants.INCEPTION_VERSION_CODE.equals(RefsetUtils.generateVersionCode("V1.23", "major")));
	}
}
