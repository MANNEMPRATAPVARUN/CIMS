package ca.cihi.cims.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.tabular.TabularConceptDetails;

//FIXME: hard-coded IDs
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ViewServiceIntegrationTest {

	@Autowired
	private ViewService viewService;

	// ------------------------------------------------

	@Test
	public void testGetConceptShortPresentation() {
		String shortPresentation = viewService.getConceptShortPresentation("O08.0", "ICD-10-CA", 1L, "ENG");
		System.out.println(shortPresentation);
	}

	@Test
	public void testGetTabularConceptDetails() {
		TabularConceptDetails details = viewService.getTabularConceptDetails(6645084, 82464, "ICD-10-CA");
		System.out.println(details);
	}

}
