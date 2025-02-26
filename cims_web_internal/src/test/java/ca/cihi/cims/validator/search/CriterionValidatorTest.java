package ca.cihi.cims.validator.search;

import java.util.HashMap;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;

/**
 * Test coverage for {@link CriterionValidator}
 * 
 * @author rshnaper
 * 
 */
public class CriterionValidatorTest {
	private Criterion criterion;
	private Errors errors;
	private Validator validator;

	@Before
	public void init() {
		CriterionType type = new CriterionType(1);
		type.setCardinalityMax(1);
		type.setCardinalityMin(0);
		type.setClassName("java.lang.Number");
		type.setDisplayName("displayName");
		type.setModelName("modelName");

		criterion = new Criterion(1, type);
		criterion.setValue(12345);

		errors = new MapBindingResult(new HashMap(), "map");
		validator = new CriterionValidator();
	}

	@Test
	public void testCriterionValidation() {
		validator.validate(criterion, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testCriterionValidationError() {
		criterion.setValue("some text");

		validator.validate(criterion, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(Matchers.not(0))));
	}

	@Test
	public void testCriterionValidationNullValue() {
		criterion.setValue(null);

		validator.validate(criterion, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testCriterionValidationUnknownClassType() {
		criterion.getType().setClassName("unknown.class");

		validator.validate(criterion, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(Matchers.not(0))));
	}

	@Test
	public void testCriterionValidatorSupport() {
		MatcherAssert.assertThat(validator.supports(Criterion.class), Matchers.is(true));
	}
}
