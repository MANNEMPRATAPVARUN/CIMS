package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class ChangeSummaryTest {

	private ChangeSummary changeSummary;

	@Before
	public void setUp() {
		changeSummary = new ChangeSummary();
	}

	@Test
	public void testGetsAndSets() {
		changeSummary.setFailedRealization(true);
		assertTrue(changeSummary.isFailedRealization());
		changeSummary.setFailedRealization(false);
		assertFalse(changeSummary.isFailedRealization());

		changeSummary.setNoChange(true);
		assertTrue(changeSummary.isNoChange());
		changeSummary.setNoChange(false);
		assertFalse(changeSummary.isNoChange());

		ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		changeSummary.setConceptModifications(conceptModifications);
		assertTrue("Should have the expected concept modifications",
				conceptModifications == changeSummary.getConceptModifications());

	}

}
