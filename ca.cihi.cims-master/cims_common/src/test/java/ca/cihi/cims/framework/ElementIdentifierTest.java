package ca.cihi.cims.framework;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class ElementIdentifierTest {

	@Test
	public void testElementIdentifier() {
		ElementIdentifier id1 = new ElementIdentifier(0l, 1l);
		ElementIdentifier id2 = new ElementIdentifier(1l, 1l);
		ElementIdentifier id3 = new ElementIdentifier(1l, 2l);
		ElementIdentifier idNull = new ElementIdentifier(null, null);
		ElementIdentifier idNull1 = new ElementIdentifier(1l, null);
		ElementIdentifier idNull2 = null;
		ElementIdentifier idNull3 = new ElementIdentifier(null, null);
		assertFalse(idNull.equals(id1));
		assertFalse(!idNull.equals(idNull3));
		assertFalse(idNull1.equals(id1));
		assertFalse(id1.equals(idNull2));
		// assertFalse(idNull.equals(id1));
		String otherClass = "";
		assertFalse(id1.equals(otherClass));
		assertFalse(id1.equals(id2));
		assertFalse(id1.equals(id3));

	}

}
