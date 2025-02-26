package ca.cihi.cims.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class SearchTest {

	@Test
	public void testSearch() {
		List<Long> propertyClasssIds = new ArrayList<>();
		propertyClasssIds.add(6l);
		propertyClasssIds.add(7l);
		List<PropertyHierarchyDTO> dtos = Search.searchHierarchyForProperties(250823l, 3103461l, 10l, propertyClasssIds,
				3);
		Assert.assertNotNull(dtos);

	}
}
