package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class PropertyServiceImplTest {
	
	@Autowired
	private PropertyServiceImpl propertyService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetSnomedFileDirectory(){
		assertTrue(propertyService.getSnomedFileDirectory().length()>0);
	}
	
	@Test	
	public void testGetSnomedBatchSize(){
		assertTrue(propertyService.getSnomedBatchSize()>=0);
	}
}
