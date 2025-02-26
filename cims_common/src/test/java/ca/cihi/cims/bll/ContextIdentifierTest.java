package ca.cihi.cims.bll;

import org.junit.Assert;
import org.junit.Test;

import ca.cihi.cims.CIMSTestConstants;

public class ContextIdentifierTest {
	@Test
	public void testForChangeRequest() {
		String baseClassification = "ICD-10-CA";
		Long changeContextId = 2342342L;

		ContextDefinition cid = ContextDefinition.forChangeContext(baseClassification, changeContextId);

		Assert.assertEquals(baseClassification, cid.getBaseClassification());

		Assert.assertEquals(changeContextId, cid.getChangeContextd());
	}

	@Test
	public void testForVersion() {

		String baseClassification = "ICD-10-CA";
		ContextDefinition cid = ContextDefinition.forVersion(baseClassification, CIMSTestConstants.TEST_VERSION);

		Assert.assertEquals(baseClassification, cid.getBaseClassification());

		Assert.assertEquals(CIMSTestConstants.TEST_VERSION, cid.getVersionCode());
	}
}
