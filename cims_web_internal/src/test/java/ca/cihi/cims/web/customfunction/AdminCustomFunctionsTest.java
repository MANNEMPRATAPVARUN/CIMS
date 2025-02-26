package ca.cihi.cims.web.customfunction;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AdminCustomFunctionsTest {

	@Test
	public void testStatusConvertFromLetter() {
		assertEquals("Active", AdminCustomFunctions.statusConvertFromLetter("A"));
		assertEquals("Disabled", AdminCustomFunctions.statusConvertFromLetter("D"));
		assertEquals("UNK", AdminCustomFunctions.statusConvertFromLetter("other"));
	}

	@Test
	public void testYesNoConvertFromLetter() {
		assertEquals("Yes", AdminCustomFunctions.yesNoConvertFromLetter("Y"));
		assertEquals("No", AdminCustomFunctions.yesNoConvertFromLetter("N"));
		assertEquals("UNK", AdminCustomFunctions.yesNoConvertFromLetter("other"));
	}
}
