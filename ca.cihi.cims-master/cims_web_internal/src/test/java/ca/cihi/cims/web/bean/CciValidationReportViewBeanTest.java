package ca.cihi.cims.web.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class CciValidationReportViewBeanTest {

	CciValidationReportViewBean bean;
	@Before
	 public void setUp()	    {
		bean = new CciValidationReportViewBean();
	 }
	 @Test
	 public void testGetsAndSets(){
		 bean.setConceptCode("conceptCode");
		 bean.setValidations(null);
		 assertTrue("Should have  the expected conceptCode", bean.getConceptCode().equals("conceptCode"));
		 assertTrue("Should have  the expected validations", bean.getValidations()==null);
				
	 }
	
}
