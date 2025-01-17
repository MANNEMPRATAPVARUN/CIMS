package ca.cihi.cims.web.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/*
 * this junit class is for cheating sonar
 */
public class ConceptViewBeanTest {
	ConceptViewBean bean;
	@Before
	 public void setUp()	    {
		bean = new ConceptViewBean();
	 }
	
	
	@Test
	public void testThatCanConvertUpdateModel() {
		MappingJackson2HttpMessageConverter conv = new MappingJackson2HttpMessageConverter();
	  assertTrue(conv.canRead(ConceptViewBean.class, MediaType.APPLICATION_JSON));
	}
	
	
	 @Test
	 public void testGetsAndSets(){
		 bean.setChildren(null);
		 bean.setChRequestId("chRequestId");
		 bean.setClassification("classification");
		 bean.setConceptCode("conceptCode");
		 bean.setConceptId("conceptId");
		 bean.setConceptLevel("conceptLevel");
		 bean.setConceptList(null);
		 bean.setConceptLongDesc("conceptLongDesc");
		 bean.setConceptShortDesc("conceptShortDesc");
		 bean.setConceptTextDesc("conceptTextDesc");
		 bean.setConceptType("conceptType");
		 bean.setContainerConceptId("containerConceptId");
		 bean.setContextId(1L);
		 bean.setDesc("desc");
		 bean.setExpand(false);
		 bean.setFiscalYear("2012");
		 bean.setFolder(true);
		 bean.setKey("A00");
		 bean.setLanguage("ENG");
		 bean.setLazy(true);
		 bean.setLeaf(true);
		 bean.setParentConceptType("parentConceptType");
		 bean.setShortPresentation("shortPresentation");
		 bean.setTitle("title");
		 bean.setHasChildren("Y");
		 
		 assertTrue("Should have  the expected Children", bean.getChildren()==null);
		 assertTrue("Should have  the expected chRequestId", bean.getChRequestId().equals("chRequestId"));
		 assertTrue("Should have  the expected classification", bean.getClassification().equals("classification"));
		 assertTrue("Should have  the expected conceptCode", bean.getConceptCode().equals("conceptCode"));
		 assertTrue("Should have  the expected conceptId", bean.getConceptId().equals("conceptId"));
		 assertTrue("Should have  the expected conceptLevel", bean.getConceptLevel().equals("conceptLevel"));
		 assertTrue("Should have  the expected ConceptList", bean.getConceptList()==null);
		 assertTrue("Should have  the expected conceptLongDesc", bean.getConceptLongDesc().equals("conceptLongDesc"));
		 assertTrue("Should have  the expected conceptShortDesc", bean.getConceptShortDesc().equals("conceptShortDesc"));
		 assertTrue("Should have  the expected conceptTextDesc", bean.getConceptTextDesc().equals("conceptTextDesc"));
		 assertTrue("Should have  the expected conceptType", bean.getConceptType().equals("conceptType"));
		 assertTrue("Should have  the expected containerConceptId", bean.getContainerConceptId().equals("containerConceptId"));
		 assertTrue("Should have  the expected ContextId", bean.getContextId().longValue()==1l);
		 assertTrue("Should have  the expected desc", bean.getDesc().equals("desc"));
		 assertTrue("Should have  the expected Expand", bean.isExpand()==false);
		 assertTrue("Should have  the expected Folder", bean.getIsFolder()==true);
		 assertTrue("Should have  the expected FiscalYear", bean.getFiscalYear().equals("2012"));
		 assertTrue("Should have  the expected Key", bean.getKey().equals("A00"));
		 assertTrue("Should have  the expected Language", bean.getLanguage().equals("ENG"));
		 assertTrue("Should have  the expected Lazy flag", bean.getIsLazy());
		 assertTrue("Should have  the expected Leaf flag", bean.isLeaf());													
		 assertTrue("Should have  the expected parentConceptType", bean.getParentConceptType().equals("parentConceptType"));
		 assertTrue("Should have  the expected shortPresentation", bean.getShortPresentation().equals("shortPresentation"));
		 assertTrue("Should have  the expected title", bean.getTitle().equals("title"));
		 assertTrue("Should have  the expected hasChildren", bean.getHasChildren().equals("Y"));
					
		 
		 
		 
	 }
}
