package ca.cihi.cims.refset.handler;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.RefsetDTO;
import ca.cihi.cims.refset.service.concept.RefsetVersion;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class RefsetControlHandlerTest {

	@Autowired
	private RefsetControlHandler refsetControlHandler;

	@Test
	public void testDisableRefset() {
		refsetControlHandler.disableRefset(new Long(0));
	}

	@Test
	public void testEnableRefset() {
		refsetControlHandler.disableRefset(new Long(0));
	}

	@Test
	public void testGetRefsetDTO() {
		RefsetDTO refsetVersion = refsetControlHandler.getRefsetDTO(new Long(1));
		assertTrue(refsetVersion == null);
	}

	@Test
	public void testListRefsetVersions() {
		List<RefsetVersion> versions = refsetControlHandler.listRefsetVersions(new Long(1), "ACTIVE", "OPEN");
		assertTrue(versions.isEmpty());
	}

	@Test
	public void testSearchCommonTerm() {
		List<String> terms = refsetControlHandler.searchCommonTerm("A00", "CIMS ICD-10-CA Code", 37L, 20);
		assertTrue(terms.isEmpty());
	}
	
	@Test
	public void testGetPicklistColumnOutputConfig(){
		List<PicklistColumnOutputDTO> testList = refsetControlHandler.getPicklistColumnOutputConfig(0l, 0l);
		assertTrue(testList.size()==0);
	}
}
