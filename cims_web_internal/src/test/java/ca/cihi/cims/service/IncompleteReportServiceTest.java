package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.IncompleteReportMapper;
import ca.cihi.cims.model.changerequest.IncompleteProperty;
import ca.cihi.cims.util.PropertyManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class IncompleteReportServiceTest {

	private IncompleteReportServiceImpl incompleteReportService;

	@Mock
	private IncompleteReportMapper incompleteReportMapper;

	@Mock
	private PropertyManager propertyManager;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);

		incompleteReportService = new IncompleteReportServiceImpl();
		incompleteReportService.setIncompleteReportMapper(incompleteReportMapper);
		incompleteReportService.setPropertyManager(propertyManager);
	}

	@Test
	public void testCheckIndexConcept() {

		Long contextId = Long.valueOf(6653893);
		Long conceptId = Long.valueOf(3541561);
		String indexDesc = "Baby";

		String RU061Message = "Index term descriptions should be unique among sibling index terms.";
		String RU110Message = "A concept should not be disabled if it: (1) has active children or (2) is used/referenced by other active concepts.";

		when(incompleteReportMapper.checkIndexConcept(contextId, conceptId)).thenReturn("RU061, RU110,");

		when(propertyManager.getMessage("RU061")).thenReturn(RU061Message);
		when(propertyManager.getMessage("RU110")).thenReturn(RU110Message);

		List<IncompleteProperty> incompleteProperties = incompleteReportService.checkIndexConcept(contextId, conceptId,
				indexDesc);

		Assert.assertTrue(incompleteProperties.size() == 2);

		IncompleteProperty firstIncomplete = incompleteProperties.get(0);
		Assert.assertNotNull(firstIncomplete);
		Assert.assertEquals(indexDesc, firstIncomplete.getCodeValue());
		Assert.assertEquals(RU061Message, firstIncomplete.getIncompleteRatoinale());

		IncompleteProperty secondIncomplete = incompleteProperties.get(1);
		Assert.assertNotNull(secondIncomplete);
		Assert.assertEquals(indexDesc, secondIncomplete.getCodeValue());
		Assert.assertEquals(RU110Message, secondIncomplete.getIncompleteRatoinale());
	}

	@Test
	public void testCheckSupplementConcept() {

		Long contextId = Long.valueOf(6653893);
		Long conceptId = Long.valueOf(3541561);
		String supplementDesc = "New Supplement";

		String RU110Message = "A concept should not be disabled if it: (1) has active children or (2) is used/referenced by other active concepts.";

		when(incompleteReportMapper.checkSupplementConcept(contextId, conceptId)).thenReturn("RU110,");

		when(propertyManager.getMessage("RU110")).thenReturn(RU110Message);

		List<IncompleteProperty> incompleteProperties = incompleteReportService.checkSupplementConcept(contextId,
				conceptId, supplementDesc);

		Assert.assertTrue(incompleteProperties.size() == 1);

		IncompleteProperty firstIncomplete = incompleteProperties.get(0);
		Assert.assertNotNull(firstIncomplete);
		Assert.assertEquals(supplementDesc, firstIncomplete.getCodeValue());
		Assert.assertEquals(RU110Message, firstIncomplete.getIncompleteRatoinale());
	}

	@Test
	public void testCheckTabularConcept() {

		Long contextId = Long.valueOf(6653893);
		Long conceptId = Long.valueOf(3541561);
		boolean isVersionYear = true;
		String code = "A10-A19";

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contextId", contextId);
		parameters.put("conceptId", conceptId);
		parameters.put("isVersionYear", isVersionYear ? 'Y' : 'N');

		String RU020Message = "Block long titles should be unique.";
		String RU025Message = "Block code value ranges should: (1) nest within its parent, (2) contain values of all its children, and (3) not overlap with its siblings";

		when(incompleteReportMapper.checkTabularConcept(parameters)).thenReturn("RU020, RU025,");

		when(propertyManager.getMessage("RU020")).thenReturn(RU020Message);
		when(propertyManager.getMessage("RU025")).thenReturn(RU025Message);

		List<IncompleteProperty> incompleteProperties = incompleteReportService.checkTabularConcept(contextId,
				conceptId, isVersionYear, code);

		Assert.assertTrue(incompleteProperties.size() == 2);

		IncompleteProperty firstIncomplete = incompleteProperties.get(0);
		Assert.assertNotNull(firstIncomplete);
		Assert.assertEquals(code, firstIncomplete.getCodeValue());
		Assert.assertEquals(RU020Message, firstIncomplete.getIncompleteRatoinale());

		IncompleteProperty secondIncomplete = incompleteProperties.get(1);
		Assert.assertNotNull(secondIncomplete);
		Assert.assertEquals(code, secondIncomplete.getCodeValue());
		Assert.assertEquals(RU025Message, secondIncomplete.getIncompleteRatoinale());
	}

	@Test
	public void testGetSet() {
		assertTrue(incompleteReportService.getIncompleteReportMapper().equals(incompleteReportMapper));
		assertTrue(incompleteReportService.getPropertyManager().equals(propertyManager));
	}
}
