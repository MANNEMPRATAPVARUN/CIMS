package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SGSCServiceTest {

	@Autowired
	SGSCService sgscService;

	@Test
	public void testReplaceSystemGeneratedSupplementContent() {

		String input = "Test without report tag.";
		assertEquals(input,
				sgscService.replaceSystemGeneratedSupplementContent(input, 5245370l, 3103461l, Boolean.FALSE));

		String icdNewCodes = "<report src=\"ICDNewCode\" lang=\"eng\"></report>";

		assertNotNull(input,
				sgscService.replaceSystemGeneratedSupplementContent(icdNewCodes, 5245370l, 3103461l, Boolean.FALSE));
	}
}
