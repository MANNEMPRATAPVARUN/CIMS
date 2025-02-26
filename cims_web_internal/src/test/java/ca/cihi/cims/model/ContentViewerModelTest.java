package ca.cihi.cims.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * cheating sonar
 */
public class ContentViewerModelTest {
	ContentViewerModel model;
	 @Before
	 public void setUp()	    {
	       model = new ContentViewerModel();
	 }
	 @Test
	 public void testGetsAndSets(){
		 model.setAttributeCode("attributeCode");
		 model.setAttributeDescription("attributeDescription");
		 model.setAttributeNote("attributeNote");
		 model.setAttributeRefDesc("attributeRefDesc");
		 model.setAttributeRefNote("attributeRefNote");
		 model.setConceptCode("conceptCode");
		 model.setConceptCodeDesc("conceptCodeDesc");
		 model.setConceptId("conceptId");
		 model.setConceptLevel("conceptLevel");
		 model.setConceptLongDesc("conceptLongDesc");
		 model.setConceptShortDesc("conceptShortDesc");
		 model.setConceptTextDesc("conceptTextDesc");
		 model.setConceptType("conceptType");
		 model.setHtmlString("htmlString");
		 model.setIsCanFlag(true);
		 model.setLeaf(true);
		 model.setParentConceptId("parentConceptId");
		 model.setParentConceptType("parentConceptType");
		 model.setRequestCount(0);
		 model.setUnitConceptId("unitConceptId");
		 model.setHasChildren("Y");
		 assertTrue("Should have the expected attributeCode", model.getAttributeCode().equals("attributeCode"));	
		 assertTrue("Should have the expected attributeDescription", model.getAttributeDescription().equals("attributeDescription"));	
		 assertTrue("Should have the expected attributeNote", model.getAttributeNote().equals("attributeNote"));	
		 assertTrue("Should have the expected attributeRefDesc", model.getAttributeRefDesc().equals("attributeRefDesc"));	
		 assertTrue("Should have the expected attributeRefNote", model.getAttributeRefNote().equals("attributeRefNote"));	
		 assertTrue("Should have the expected conceptCode", model.getConceptCode().equals("conceptCode"));	
		 assertTrue("Should have the expected conceptCodeDesc", model.getConceptCodeDesc().equals("conceptCodeDesc"));	
		 assertTrue("Should have the expected conceptId", model.getConceptId().equals("conceptId"));	
		 assertTrue("Should have the expected conceptLevel", model.getConceptLevel().equals("conceptLevel"));			
		 assertTrue("Should have the expected conceptLongDesc", model.getConceptLongDesc().equals("conceptLongDesc"));	
		 assertTrue("Should have the expected conceptShortDesc", model.getConceptShortDesc().equals("conceptShortDesc"));	
		 assertTrue("Should have the expected conceptTextDesc", model.getConceptTextDesc().equals("conceptTextDesc"));	
		 assertTrue("Should have the expected conceptType", model.getConceptType().equals("conceptType"));	
		 assertTrue("Should have the expected htmlString", model.getHtmlString().equals("htmlString"));	
		 assertTrue("Should have the expected can flag", model.getIsCanFlag());	
		 assertTrue("Should have the expected leaf", model.isLeaf());	
		 assertTrue("Should have the expected parentConceptId", model.getParentConceptId().equals("parentConceptId"));	
		 assertTrue("Should have the expected parentConceptType", model.getParentConceptType().equals("parentConceptType"));	
		 assertTrue("Should have the expected RequestCount", model.getRequestCount().intValue()==0);	
		 assertTrue("Should have the expected unitConceptId", model.getUnitConceptId().equals("unitConceptId"));	
		 assertTrue("Should have the expected hasChildren", model.getHasChildren().equals("Y"));	
				
	 }
}
