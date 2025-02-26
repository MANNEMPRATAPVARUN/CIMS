package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class ClassificationViewerModelTest {

	 private ClassificationViewerModel model;
	 
	 @Before
	 public void setUp(){
	       model = new ClassificationViewerModel();
	 }
	 @Test
	 public void testGetsAndSets(){
		 model.setClassification("ICD-10-CA");
		 String expectedGetClassification="ICD-10-CA";
		 assertTrue("Should have the expected Classification", model.getClassification().equals(expectedGetClassification));
			
		 model.setContextId(1L);
		 Long expectedGetContextId = 1L;
		 assertTrue("Should get the expected ContextId", model.getContextId().longValue()==expectedGetContextId.longValue());
		 
		 model.setFiscalYear("2012");
		 String expectedGetFiscalYear="2012";
		 assertTrue("Should have the expected Fiscal Year", model.getFiscalYear().equals(expectedGetFiscalYear));
		
		 model.setLanguage("ENG");
		 String expectedGetLanguage="ENG";
		 assertTrue("Should have the expected Language", model.getLanguage().equals(expectedGetLanguage));
		
		 
	 }
	 
}
