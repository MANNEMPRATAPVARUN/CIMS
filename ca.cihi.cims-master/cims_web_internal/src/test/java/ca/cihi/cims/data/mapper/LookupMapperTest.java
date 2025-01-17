package ca.cihi.cims.data.mapper;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.meta.ChangeRequestAssignment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class LookupMapperTest {
	@Autowired
	private LookupMapper lookupMapper;

	@Test
	public void testFindAllChangeRequestAssignmentMetaData() {
		List<ChangeRequestAssignment> allChangeRequestAssignmentMetaData = lookupMapper
				.findAllChangeRequestAssignmentMetaData();
		assertTrue("allChangeRequestAssignmentMetaData  is not empty", allChangeRequestAssignmentMetaData.size() > 0);

	}

}
