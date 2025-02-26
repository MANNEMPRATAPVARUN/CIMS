package ca.cihi.cims.validator.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.refset.service.concept.Refset;

public class RefsetValidatorTest {

	@Mock
	private Refset refset = null;

	private RefsetValidator refsetValidator = new RefsetValidator();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(refset.getVersionStatus()).thenReturn(ContextStatus.OPEN);
	}

	@Test
	public void testValidateDisabledStatus() {
		Errors result1 = new MapBindingResult(new HashMap<String, String>(), "");
		Errors result2 = new MapBindingResult(new HashMap<String, String>(), "");
		
		refsetValidator.validateDisabledStatus(refset, Status.DISABLED, result1);
		assertTrue(result1.hasErrors());

		refsetValidator.validateDisabledStatus(refset, Status.ACTIVE, result2);
		assertEquals(0, result2.getErrorCount());
	}

}
