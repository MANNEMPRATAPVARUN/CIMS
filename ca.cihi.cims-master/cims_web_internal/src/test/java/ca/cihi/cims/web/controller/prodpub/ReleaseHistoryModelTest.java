package ca.cihi.cims.web.controller.prodpub;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class ReleaseHistoryModelTest {
	ReleaseHistoryModel model;

	static Date today = Calendar.getInstance().getTime();

	@Before
	public void setUp() {
		model = new ReleaseHistoryModel();
	}

	@Test
	public void testGetsAndSets() {
		model.setCciSnapShotDate(today);
		model.setEmailOfficialInternalQARelease(true);
		model.setEmailOfficialRelease(true);
		model.setEmailPreliminaryInternalQARelease(true);
		model.setEmailPreliminaryRelease(true);
		model.setIcdSnapShotDate(today);
		model.setOfficialInternalQARelease("officialInternalQARelease");
		model.setOfficialInternalQAReleaseId(1L);
		model.setOfficialRelease("officialRelease");
		model.setOfficialReleaseId(1L);
		model.setPreliminaryInternalQARelease("preliminaryInternalQARelease");
		model.setPreliminaryInternalQAReleaseId(1L);
		model.setPreliminaryRelease("preliminaryRelease");
		model.setPreliminaryReleaseId(1L);
		model.setVersionYear("versionYear");
		assertTrue(model.getCciSnapShotDate().equals(today));
		assertTrue(model.getIcdSnapShotDate().equals(today));
		assertTrue(model.getOfficialInternalQARelease().equalsIgnoreCase("officialInternalQARelease"));
		assertTrue(model.getOfficialInternalQAReleaseId() == 1l);
		assertTrue(model.getOfficialRelease().equalsIgnoreCase("officialRelease"));
		assertTrue(model.getOfficialReleaseId() == 1l);
		assertTrue(model.getPreliminaryInternalQARelease().equalsIgnoreCase("preliminaryInternalQARelease"));
		assertTrue(model.getPreliminaryInternalQAReleaseId() == 1l);
		assertTrue(model.getPreliminaryRelease().equalsIgnoreCase("preliminaryRelease"));
		assertTrue(model.getPreliminaryReleaseId() == 1l);
		assertTrue(model.isEmailOfficialInternalQARelease());
		assertTrue(model.isEmailOfficialRelease());
		assertTrue(model.isEmailPreliminaryInternalQARelease());
		assertTrue(model.isEmailPreliminaryRelease());

	}
}
