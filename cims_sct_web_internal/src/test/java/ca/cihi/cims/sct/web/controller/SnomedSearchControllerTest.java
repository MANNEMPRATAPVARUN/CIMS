package ca.cihi.cims.sct.web.controller;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.sct.web.domain.ConceptType;
import ca.cihi.cims.sct.web.domain.SCTVersion;
import ca.cihi.cims.sct.web.domain.Term;
import ca.cihi.cims.sct.web.service.DescriptionService;

public class SnomedSearchControllerTest {
	
	private SnomedSearchController snomedSearchController;
	
	@Mock
	private DescriptionService descriptionService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		snomedSearchController = new SnomedSearchController();
		snomedSearchController.setDescriptionService(descriptionService);
	}

	@Test
	public void testGetPage(){
		ModelAndView mav = snomedSearchController.getPage("IE20160331");
		when(descriptionService.getConceptTypes()).thenReturn(new ArrayList<ConceptType>());
		assertSame(mav.getViewName(), "snomed");
		assertSame(mav.getModelMap().get("sctVersion"),"IE20160331");
	}
	
	@Test
	public void testGetData() throws Exception{
		when(descriptionService.getSCTVersionList("ACTIVE")).thenReturn(new ArrayList<SCTVersion>());
		ModelAndView mav = snomedSearchController.getData();
		assertSame(mav.getViewName(), "test");
	}
	
	@Test
	public void testGetTermDescriptions1(){
		when(descriptionService.getDesciptionByConceptIdOrDespId(0l, "IE20160131", "clinic")).thenReturn(new ArrayList<Term>());
        assertTrue(snomedSearchController.getTermDescriptions("0l", "IE20160131", "clinic").size()==0);		
	}
	
	@Test
	public void testGetTermDescriptions2(){
		when(descriptionService.getDesciptionByTerm(nullable(List.class), nullable(String.class), nullable(String.class))).thenReturn(new ArrayList<Term>());
        assertTrue(snomedSearchController.getTermDescriptions("test", "IE20160131", "clinic").size()==0);		
	}
	
	@Test
	public void testGetConceptDescriptions(){
		when(descriptionService.getDesciptionByConceptIdOrDespId(0l, "IE20160131", "clinic")).thenReturn(new ArrayList<Term>());
	    assertTrue(snomedSearchController.getConceptDescriptions(0l, "IE20160131", "clinic").size()==0);		
	}
	
}
