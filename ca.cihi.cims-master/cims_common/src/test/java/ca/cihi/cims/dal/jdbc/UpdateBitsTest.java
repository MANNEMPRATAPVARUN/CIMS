package ca.cihi.cims.dal.jdbc;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class UpdateBitsTest {

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void updateBitsTest() {

		UpdateBits update = new UpdateBits("TextPropertyVersion");
		update.addWhere("textPropertyId", "1234");

		update.addColumn("text", "text");

		LOGGER.debug(update.toString());

		assertTrue(update.toString().contains("textPropertyId=1234"));
		assertTrue(update.toString().contains("text=:text"));

	}

}
