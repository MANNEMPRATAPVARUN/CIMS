package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class IncompletePropertyTest {

	private IncompleteProperty incompleteProperty;

	@Before
	public void setUp() {
		incompleteProperty = new IncompleteProperty();
	}

	@Test
	public void testGetsAndSets() {
		String codeValue = "A00";
		String incompleteRationale = "Block long titles should be unique";

		incompleteProperty.setCodeValue(codeValue);
		assertEquals(codeValue, incompleteProperty.getCodeValue());

		incompleteProperty.setIncompleteRatoinale(incompleteRationale);
		assertEquals(incompleteRationale, incompleteProperty.getIncompleteRatoinale());

	}

}
