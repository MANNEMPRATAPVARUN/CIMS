package ca.cihi.cims.model.folioclamlexport;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class HtmlOutputLogTest {

	private HtmlOutputLog log;
	private Date creationDate = Calendar.getInstance().getTime();

	@Before
	public void setUp() {
		log = new HtmlOutputLog();
		log.setHtmlOutputLogId(1L);
		log.setFiscalYear("2016");
		log.setClassificationCode("CCI");
		log.setLanguageCode("ENG");
		log.setCreatedByUserId(100L);
		log.setCreationDate(creationDate);
		log.setStatusCode("D");
		log.setZipFileName("file.zip");
	}

	@Test
	public void testGetHtmlOutputLogId() {
		assertEquals(1L, log.getHtmlOutputLogId().longValue());
	}

	@Test
	public void testGetFiscalYear() {
		assertEquals("2016", log.getFiscalYear());
	}

	@Test
	public void testGetClassificationCode() {
		assertEquals("CCI", log.getClassificationCode());
	}

	@Test
	public void testGetLanguageCode() {
		assertEquals("ENG", log.getLanguageCode());
	}

	@Test
	public void testGetCreatedByUserId() {
		assertEquals(100L, log.getCreatedByUserId().longValue());
	}

	@Test
	public void testGetCreationDate() {
		assertEquals(creationDate, log.getCreationDate());
	}

	@Test
	public void testGetStatusCode() {
		assertEquals("D", log.getStatusCode());
	}

	@Test
	public void testGetZipFileName() {
		assertEquals("file.zip", log.getZipFileName());
	}
}