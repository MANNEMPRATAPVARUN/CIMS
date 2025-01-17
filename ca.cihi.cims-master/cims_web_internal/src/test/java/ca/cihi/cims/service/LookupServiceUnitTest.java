package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.LookupMapper;

public class LookupServiceUnitTest {
	@Mock
	LookupService lookupSerivce;
	@Mock
	LookupMapper lookupMapper;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		lookupSerivce.setLookupMapper(lookupMapper);
		Mockito.doReturn(mockContextIdentifier()).when(lookupSerivce).findOpenContextByChangeRquestId(nullable(Long.class));
	}

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier mockContextIdentifier = new ContextIdentifier();
		mockContextIdentifier.setBaseClassification("ICD-10-CA");
		mockContextIdentifier.setVersionCode("2018");
		return mockContextIdentifier;
	}

	@Test
	public void testFindOpenContextByChangeRquestId() {
		ContextIdentifier contextIdentifier = lookupSerivce.findOpenContextByChangeRquestId(1L);
		assertTrue(contextIdentifier.getBaseClassification().equalsIgnoreCase("ICD-10-CA"));
	}

}
