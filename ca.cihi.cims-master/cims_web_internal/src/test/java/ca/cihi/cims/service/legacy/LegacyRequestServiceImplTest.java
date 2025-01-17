package ca.cihi.cims.service.legacy;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.legacy.LegacyRequestMapper;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class LegacyRequestServiceImplTest {
	@Autowired
	LegacyRequestService legacySerivce;
	@Mock
	LegacyRequestMapper legacyRequestMapper;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		legacySerivce.setLegacyRequestMapper(legacyRequestMapper);
		when(legacyRequestMapper.findLegacyChangeRequestsBySearchModel((nullable(LegacyRequestSearchModel.class)))).thenReturn(mockLegacyRequestResults());
	}



	private List<LegacyRequestResultsModel> mockLegacyRequestResults() {
        List<LegacyRequestResultsModel> list  = new ArrayList<LegacyRequestResultsModel>();
        LegacyRequestResultsModel legacyRequestResultsModel1 = new LegacyRequestResultsModel();
        LegacyRequestResultsModel legacyRequestResultsModel2 = new LegacyRequestResultsModel();
        LegacyRequestResultsModel legacyRequestResultsModel3 = new LegacyRequestResultsModel();
        list.add(legacyRequestResultsModel1);
        list.add(legacyRequestResultsModel2);
        list.add(legacyRequestResultsModel3);
        return list;
    }


	@Test
	public void testfindLegacyChangeRequestsBySearchModel() {
		List<LegacyRequestResultsModel> list = legacySerivce.findLegacyChangeRequestsBySearchModel(nullable(LegacyRequestSearchModel.class));
		assertEquals(3, list.size());
	}

}
