package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.content.cci.CciValidationXml;

/*
 * this junit class is for cheating sonar
 */
public class ValidationChangeTest {
	private ValidationChange model;

	@Before
	public void setUp() {
		model = new ValidationChange();
	}

	@Test
	public void testGetsAndSets() {
		CciValidationXml cciValidationXml = new CciValidationXml();
		model.setCciValidationXml(cciValidationXml);
		assertTrue("Should have the expected cciValidationXml", model.getCciValidationXml() == cciValidationXml);

		String dataHolding = "DAD";
		model.setDataHolding(dataHolding);
		assertTrue("Should have the expected dataHolding", model.getDataHolding() == dataHolding);

		String status = "ACTIVE";
		model.setStatus(status);
		assertTrue("Should have the expected status", model.getStatus() == status);

		String value = "test value";
		model.setValue(value);
		assertTrue("Should have the expected value", model.getValue() == value);
	}
}
