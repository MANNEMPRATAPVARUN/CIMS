package ca.cihi.cims.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class PropertyManagerTest {

	@org.springframework.beans.factory.annotation.Autowired
	private PropertyManager propertyManager;

	// ------------------------------------------------------

	@Test
	public void testGetProperty() {
		junit.framework.Assert.assertEquals("Classifications Information Management System",
				propertyManager.getMessage("cims.common.header"));
	}

}
