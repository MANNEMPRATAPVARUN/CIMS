package ca.cihi.cims.validator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ca.cihi.cims.util.DtdValidator;

public class DtdValidatorTest {

	private static final String CIHI_CIMS_DTD = "cihi_cims.dtd";
	private final DtdValidator validator = new DtdValidator();

	// ----------------------------------------------------

	@Test
	public void testInvalid() throws Exception {
		assertNotNull(validate("<concept></concept>"));
	}

	@Test
	public void testValid() throws Exception {
		String xml = "<concept>" + //
				"<language>ENG</language>" + //
				"<classification>ICD-10-CA</classification>" + //
				"<CODE>A15.4</CODE>" + //
				"<PRESENTATION_CODE>A15.4</PRESENTATION_CODE>" + //
				"<TYPE_CODE>CATEGORY</TYPE_CODE>" + //
				"<PRESENTATION_TYPE_CODE>CATEGORY2</PRESENTATION_TYPE_CODE>" + //
				"<USER_DESC>null</USER_DESC>" + //
				"<CONCEPT_DETAIL>" + //
				"	<CLOB><qualifierlist type='also'><also><label>1</label></also></qualifierlist></CLOB>" + //
				"</CONCEPT_DETAIL>" + //
				"</concept>";
		assertNull(validate(xml));
	}

	@Test
	public void testValidSeeAlso() throws Exception {
		String xml = "<qualifierlist type='also'><also><label>Use additional code to identify the underlying disease.</label></also></qualifierlist>";
		assertNull(validator.validateSegment("qualifierlist", CIHI_CIMS_DTD, xml));
	}

	private String validate(String xml) {
		return validator.validateSegment("concept", CIHI_CIMS_DTD, xml);
	}

}
