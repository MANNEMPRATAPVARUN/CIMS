package ca.cihi.cims.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//FIXME: hard-coded IDs
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ContextServiceIntegrationTest {

	@Autowired
	private ContextService contextService;

	@Test
	public void updateChangeContextTest() {
		contextService.deleteContext(0L);
	}
}
