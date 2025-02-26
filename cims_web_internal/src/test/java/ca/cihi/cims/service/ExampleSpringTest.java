package ca.cihi.cims.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class is an example of of a partially integrated unit test, of the sort that would be used to test bits of
 * Spring configuration (e.g. is my complicated factory working, or persistence tests).
 * 
 * @author MPrescott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-test.xml",
		"classpath:spring/applicationContext-beans.xml" })
public class ExampleSpringTest {

	// @Autowired
	// private Whatever injectedDependencyFromTheSpringContext;

	@Test
	@Ignore
	public void placeboMethod() {
		// This method does nothing, it's only here so the test suite doesn't
		// complain
	}
}
