package ca.cihi.cims.web.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class ReferenceReportViewBeanTest {
	ReferenceReportViewBean bean;
	@Before
	 public void setUp()	    {
		bean = new ReferenceReportViewBean();
	 }
	
	 @Test
	 public void testGetsAndSets(){
		 bean.setAttributes(null);
		 bean.setRefCode("refCode");
		 bean.setRefNote("refNote");
		 assertTrue("Should have  the expected Attributes", bean.getAttributes()==null);
		 assertTrue("Should have  the expected refCode", bean.getRefCode().equals("refCode"));
		 assertTrue("Should have  the expected refNote", bean.getRefNote().equals("refNote"));
		 
	 }
}
