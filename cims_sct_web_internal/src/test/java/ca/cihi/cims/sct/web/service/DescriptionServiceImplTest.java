package ca.cihi.cims.sct.web.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.sct.web.domain.Term;
import ca.cihi.cims.sct.web.mapper.DescriptionMapper;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-sct-test.xml" })
public class DescriptionServiceImplTest {
	
	private DescriptionServiceImpl descriptionService;
	
	@Autowired
	private DescriptionMapper descriptionMapper;
	
	@Mock
	private DescriptionMapper descriptionMapper2;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		descriptionService = new DescriptionServiceImpl();
		descriptionService.setDescriptionMapper(descriptionMapper);
	}

	@Test
    public void testGetDesciptionById(){
	    assertTrue(descriptionService.getDesciptionById(0, "IE20160101")==null);
    }
	
	@Test
    public void testGetDesciptionByConceptId1(){
	    assertTrue(descriptionService.getDesciptionByConceptId(0L, "IE20160101").size()==0);
    }
	
	@Test
    public void testGetDesciptionByConceptId2(){
		List<Term> termList = new ArrayList<Term>();
		Term term1 = new Term();
		term1.setConceptFsn("conceptFsnTest1");
		term1.setConceptFsnId(1l);
		term1.setConceptId(2l);
		term1.setConceptPreferred("conceptPreferredTest1");
		term1.setConceptPreferredId(3l);
		term1.setConceptType("conceptTypeTest1");
		LocalDate testDate = LocalDate.of(2016, Month.NOVEMBER, 1);
		term1.setEffectiveDate(Date.from(testDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		term1.setSelectedTerm("selectedTermTest1");
		term1.setSelectedTermAcceptability("selectedTermAcceptabilityTest1");
		term1.setSelectedTermId(4l);
		term1.setSelectedTermType("selectedTermTypeTest1");
		term1.setSynonym("synonymTest1");
		term1.setSynonymId(5l);
		term1.setVersion("IE20160101");
		termList.add(term1);
		descriptionService.setDescriptionMapper(descriptionMapper2);
		when(descriptionMapper2.getDespByConceptId(0l,"IE20160101")).thenReturn(termList);
		List<Term> resultList = descriptionService.getDesciptionByConceptId(0L, "IE20160101");
		assertTrue(resultList.size()==1);
		Term resultTerm = resultList.get(0);
		assertEquals(resultTerm.getConceptFsn(),"conceptFsnTest1");
		assertEquals(resultTerm.getConceptFsnId(),1l);
		assertEquals(resultTerm.getConceptId(),2l);
		assertEquals(resultTerm.getConceptPreferred(),"conceptPreferredTest1");
		assertEquals(resultTerm.getConceptPreferredId(),3l);
		assertEquals(resultTerm.getConceptType(),"conceptTypeTest1");
		LocalDate testDate2 = LocalDate.of(2016, Month.NOVEMBER, 2);
		assertTrue(resultTerm.getEffectiveDate().before(Date.from(testDate2.atStartOfDay(ZoneId.systemDefault()).toInstant())));				
		assertEquals(resultTerm.getSelectedTerm(),"selectedTermTest1");
		assertEquals(resultTerm.getSelectedTermAcceptability(),"selectedTermAcceptabilityTest1");
		assertEquals(resultTerm.getSelectedTermId(),4l);
		assertEquals(resultTerm.getSelectedTermType(),"selectedTermTypeTest1");
		assertEquals(resultTerm.getSynonym(),"synonymTest1");
		assertEquals(resultTerm.getSynonymId(),5l);
		assertEquals(resultTerm.getVersion(),"IE20160101");		
    }
	
	@Test
	public void testGetDesciptionByTerm(){
		List<String> terms = new ArrayList<String>();
		terms.add("test1");
		assertTrue(descriptionService.getDesciptionByTerm(terms,"IE20160101","clinic").size()==0);
	}
	
	@Test
	public void testGetDesciptionByConceptIdOrDespId(){
		assertTrue(descriptionService.getDesciptionByConceptIdOrDespId(0L,"IE20160101","clinic").size()==0);
	}
	
	@Test
	public void testGetAllVersons(){
		assertTrue(descriptionService.getAllVersons().size()>=0);
	}
	
	@Test
	public void testGetSCTVersionList() throws Exception{
		assertTrue(descriptionService.getSCTVersionList("ACTIVE").size()>=0);
	}
	
	@Test
	public void testGetConceptTypes() throws Exception{
		assertTrue(descriptionService.getConceptTypes().size()==4);
	}
	
	
}
