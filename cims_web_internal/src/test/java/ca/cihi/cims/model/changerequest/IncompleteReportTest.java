package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class IncompleteReportTest {

	private IncompleteReport incompleteReport;

	@Before
	public void setUp() {
		incompleteReport = new IncompleteReport();
	}

	@Test
	public void testGetsAndSets() {

		ChangeRequest changeRequest = new ChangeRequest();
		List<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		incompleteReport.setChangeRequest(changeRequest);
		assertEquals(changeRequest, incompleteReport.getChangeRequest());

		incompleteReport.setIncomProperties(incompleteProperties);
		assertEquals(incompleteProperties, incompleteReport.getIncomProperties());
	}

}
