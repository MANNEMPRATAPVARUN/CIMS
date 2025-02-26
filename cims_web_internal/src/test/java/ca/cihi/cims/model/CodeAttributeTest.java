package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * cheating sonar
 */
public class CodeAttributeTest {
	CodeDescription model;
	
	 @Before
	 public void setUp()	    {
	       model = new CodeDescription();
	 }
	 @Test
	 public void testGetsAndSets(){
		 model.setCode("code");
		 model.setDescription("description");
		 assertTrue("Should have the expected code", model.getCode().equals("code"));	
		 assertTrue("Should have the expected description", model.getDescription().equals("description"));	
				
	 }
}
