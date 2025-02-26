package ca.cihi.cims.web.bean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.web.bean.LogMessage;

/**
 * Test class of LogMessage
 * 
 * @author wxing
 * 
 */
public class LogMessageTest {

	private LogMessage message;

	@Before
	public void setUp() {
		message = new LogMessage();
	}

	@Test
	public void testGetSetClassification() {
		String classification = "CCI";
		Assert.assertNull(message.getClassification());

		message.setClassification(classification);
		Assert.assertTrue(message.getClassification().equalsIgnoreCase(classification));
	}

	@Test
	public void testGetSetFiscalYear() {
		String fiscalYear = "2009";
		Assert.assertNull(message.getFiscalYear());

		message.setFiscalYear(fiscalYear);
		Assert.assertTrue(message.getFiscalYear().equalsIgnoreCase(fiscalYear));
	}

	@Test
	public void testGetSetMessage() {
		String logMessage = "This is a message!";
		Assert.assertNull(message.getMessage());

		message.setMessage(logMessage);
		Assert.assertTrue(message.getMessage().equalsIgnoreCase(logMessage));
	}

}
