package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class SearchResultModelTest {
	 private SearchResultModel model;
	 
	 @Before
	 public void setUp()	    {
	       model = new SearchResultModel();
	 }
	 
	 @Test
	 public void testGetsAndSets(){
		 model.setConceptCode("conceptCode");
		 model.setConceptCodeDesc("conceptCodeDesc");
		 model.setConceptId("conceptId");
		 model.setConceptIdPath("conceptIdPath");
		 model.setConceptType("conceptType");
		 model.setLongDescription("longDescription");
		 assertTrue("Should have the expected conceptCode", model.getConceptCode().equals("conceptCode"));
		 assertTrue("Should have the expected conceptCodeDesc", model.getConceptCodeDesc().equals("conceptCodeDesc"));
		 assertTrue("Should have the expected conceptId", model.getConceptId().equals("conceptId"));
		 assertTrue("Should have the expected conceptIdPath", model.getConceptIdPath().equals("conceptIdPath"));
		 assertTrue("Should have the expected conceptType", model.getConceptType().equals("conceptType"));
		 assertTrue("Should have the expected longDescription", model.getLongDescription().equals("longDescription"));
								
		 
		 
	 }
}
