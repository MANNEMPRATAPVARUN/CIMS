package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AsteriskBlockInfoTest {

	private AsteriskBlockInfo asteriskBlockInfo;

	@Before
	public void setUp() {
		asteriskBlockInfo = new AsteriskBlockInfo();
	}

	@Test
	public void testGetsAndSets() {
		String code = "A11";
		String userDescEng = "Test user desc English";
		String userDescFra = "Test user desc French";

		asteriskBlockInfo.setCode(code);
		asteriskBlockInfo.setUserDescEng(userDescEng);
		asteriskBlockInfo.setUserDescFra(userDescFra);

		assertTrue("Should have the expected code", code == asteriskBlockInfo.getCode());
		assertTrue("Should have the expected userDesc english", userDescEng == asteriskBlockInfo.getUserDescEng());
		assertTrue("Should have the expected userDesc french", userDescFra == asteriskBlockInfo.getUserDescFra());
	}

}
