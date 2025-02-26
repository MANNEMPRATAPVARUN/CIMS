package ca.cihi.cims.validator;

import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_SECTION;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CHAPTER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

//validateCciBlock(String, String)
//validateIcdBlock(String, boolean)
//validateIcdCategory(String, int, boolean)
public class CodeValidatorTest {

	@Test
	public void test() {
		CodeValidator validator = new CodeValidator();

		// ICD_CHAPTER
		assertNull(validator.validateOthers(ICD_CHAPTER, "99"));
		assertNotNull(validator.validateOthers(ICD_CHAPTER, "1"));
		assertNotNull(validator.validateOthers(ICD_CHAPTER, "9X"));

		// ICD_BLOCK, morphology=true
		assertNull(validator.validateIcdBlock("999", true));
		assertNull(validator.validateIcdBlock("999-999", true));
		assertNotNull(validator.validateIcdBlock("999-99B", true));
		assertNotNull(validator.validateIcdBlock("99A-99B", true));
		assertNotNull(validator.validateIcdBlock("99-99", true));

		// ICD_BLOCK, morphology=false
		assertNull(validator.validateIcdBlock("A79-A99", false));
		assertNull(validator.validateIcdBlock("A99-A99", false));
		assertEquals(CodeValidator.INVALID_BLOCK_RANGE, validator.validateIcdBlock("A79-A69", false));
		assertNotNull(validator.validateIcdBlock("A99", false));
		assertNotNull(validator.validateIcdBlock("AA99", false));
		assertNotNull(validator.validateIcdBlock("899", false));
		assertNotNull(validator.validateIcdBlock("A79-899", false));

		// ICD_CATEGORY, morphology=true
		assertNull(validator.validateIcdCategory("1234/5", 1, true, null));
		assertNotNull(validator.validateIcdCategory("1234.5", 1, true, null));
		assertNotNull(validator.validateIcdCategory("1234", 1, true, null));

		// ICD_CATEGORY, morphology=true, level=1
		for (int level = 1; level < 5; level++) {
			assertNull(validator.validateIcdCategory("1234/5", level, true, null));
			assertNotNull(validator.validateIcdCategory("1234.5", level, true, null));
			assertNotNull(validator.validateIcdCategory("1234", level, true, null));
		}

		// ICD_CATEGORY, morphology=false, level=1
		assertNull(validator.validateIcdCategory("A12", 1, false, null));
		assertNotNull(validator.validateIcdCategory("a12", 1, false, null));
		assertNotNull(validator.validateIcdCategory("AA1", 1, false, null));
		assertNotNull(validator.validateIcdCategory("A", 1, false, null));

		// ICD_CATEGORY, morphology=false, level=2
		assertNull(validator.validateIcdCategory("A12.3", 2, false, "A12"));
		assertNull(validator.validateIcdCategory("A12.45", 2, false, "A12"));
		assertNotNull(validator.validateIcdCategory("A12.3", 2, false, "B12"));
		assertNotNull(validator.validateIcdCategory("A12.45", 2, false, "A13"));
		assertNotNull(validator.validateIcdCategory("A1.456", 2, false, "A1"));
		assertNotNull(validator.validateIcdCategory("A12.456", 2, false, "A12"));

		// ICD_CATEGORY, morphology=false, level=3
		assertNull(validator.validateIcdCategory("A12.45", 3, false, "A12.4"));
		assertNull(validator.validateIcdCategory("A12.456", 3, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("a12.456", 3, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("A12.45", 3, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("A1.456", 3, false, null));
		assertNotNull(validator.validateIcdCategory("A12.4567", 3, false, null));
		assertNotNull(validator.validateIcdCategory("A1-456", 3, false, null));
		assertNotNull(validator.validateIcdCategory("A124567", 3, false, null));

		// ICD_CATEGORY, morphology=false, level=4
		assertNull(validator.validateIcdCategory("A12.454", 4, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("a12.456", 4, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("A12.45z", 4, false, "A12.45"));
		assertNotNull(validator.validateIcdCategory("A1.456", 4, false, null));
		assertNotNull(validator.validateIcdCategory("A12.4567", 4, false, null));
		assertNotNull(validator.validateIcdCategory("A1-456", 4, false, null));
		assertNotNull(validator.validateIcdCategory("A124567", 4, false, null));

		// CCI_SECTION
		assertNull(validator.validateOthers(CCI_SECTION, "1"));
		assertNotNull(validator.validateOthers(CCI_SECTION, "A"));
		assertNotNull(validator.validateOthers(CCI_SECTION, "11"));

		// CCI_BLOCK
		assertNull(validator.validateCciBlock("1AB-1BB", "1"));
		assertNull(validator.validateCciBlock("1BB-1BB", "1"));
		assertEquals(CodeValidator.INVALID_BLOCK_RANGE, validator.validateCciBlock("1AB-1AA", "1"));
		assertNotNull(validator.validateCciBlock("1.AB-1.BB", "1"));
		assertNotNull(validator.validateCciBlock("1ab-1bb", "1"));
		assertNotNull(validator.validateCciBlock("1BB", "1"));
		assertNotNull(validator.validateCciBlock("1AB-2BB", "1"));
		assertNotNull(validator.validateCciBlock("2BB", "1"));
		assertNotNull(validator.validateCciBlock("111", "1"));

	}
}
