package ca.cihi.cims.model.folioclamlexport;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.folioclamlexport.GenerationStatus;

public class GenerationStatusTest {
	private GenerationStatus status;
	private Date generatedDate = Calendar.getInstance().getTime();

	@Before
	public void setUp() {
		status = new GenerationStatus();
		status.setHtmlOutputLogId(1L);
		status.setGeneratedDate(generatedDate);
		status.setClassification("CCI");
		status.setYear("2016");
		status.setLanguage("ENG");
		status.setStatus("D");
		status.setDownloadUrl("file.zip");
		status.setHasDetailedLog(true);
		status.setLastGeneration(true);
	}

	@Test
	public void testGetHtmlOutputLogId() {
		assertEquals(1L, status.getHtmlOutputLogId().longValue());
	}

	@Test
	public void testGetGeneratedDate() {
		assertEquals(this.generatedDate, status.getGeneratedDate());
	}

	@Test
	public void testGetClassification() {
		assertEquals("CCI", status.getClassification());
	}

	@Test
	public void testGetYear() {
		assertEquals("2016", status.getYear());
	}

	@Test
	public void testGetLanguage() {
		assertEquals("ENG", status.getLanguage());
	}

	@Test
	public void testGetStatus() {
		assertEquals("D", status.getStatus());
	}

	@Test
	public void testGetDownloadUrl() {
		assertEquals("file.zip", status.getDownloadUrl());
	}

	@Test
	public void testGetHasDetailedLog() {
		assertEquals(true, status.getHasDetailedLog());
	}

	@Test
	public void testGetLastGeneration() {
		assertEquals(true, status.getLastGeneration());
	}
}