package ca.cihi.cims.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })

public class AdminServiceIntigrationTest {
	@Autowired
	AdminService adminService;




	@Test
	public void testGetAuxTableValues(){

		List<AuxTableValue> tableValues =adminService.getAuxTableValues("REQUESTOR");
		System.out.println(tableValues.size());
	}

	@Test
	public void testGetAuxTableValueByID(){
		AuxTableValue tableValue =adminService.getAuxTableValueByID(1L);
		System.out.println(tableValue.getAuxValueCode());
		assertNotNull(tableValue);
		assertNotNull(tableValue.getAuxValueCode());
	}


	@Test
	public void testFindDistinctUsersInDistributionListIds(){

		List<Long> dls = new ArrayList<Long>();
		dls.add(Distribution.DL_ID_ADMINISTRATOR);
		dls.add(Distribution.DL_ID_ENContentDeveloper);
		List<User> users =adminService.findDistinctUsersInDistributionListIds(dls);

		System.out.println(users.size());
	}
}
