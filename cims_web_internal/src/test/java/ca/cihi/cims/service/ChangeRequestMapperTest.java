package ca.cihi.cims.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.model.changerequest.Advice;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestMapperTest {

	@Autowired
	private ChangeRequestMapper changeRequestMapper;

	// FIXME: hard-coded IDs
	@Test
	public void testInsertAdvice() {
		Advice advice = new Advice();
		advice.setChangeRequestId(103L);
		advice.setUserProfileId(14L);
		advice.setSenderId(6L);
		advice.setDistributionListId(null);
		advice.setMessage("it is a test");
		changeRequestMapper.insertAdvice(advice);
	}

}
