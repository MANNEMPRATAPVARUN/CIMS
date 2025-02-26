package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ca.cihi.cims.CIMSException;

public class ReportGeneratorFactoryTest {

	@Test(expected = CIMSException.class)
	public void testCreateReportGenerator() {
		ReportGeneratorFactory factory = new ReportGeneratorFactoryImpl();
		assertNotNull(factory.createReportGenerator("MissingValidationICD"));

		factory.createReportGenerator("Test");
	}
}
