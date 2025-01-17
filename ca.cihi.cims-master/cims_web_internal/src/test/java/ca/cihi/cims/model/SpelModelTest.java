package ca.cihi.cims.model;

import org.junit.Assert;
import org.junit.Test;

public class SpelModelTest {

	@Test
	public void testAccess() {

		ExampleSpelModel model = new ExampleSpelModel(new UnderlyingThing());
		Assert.assertEquals("Geronimooo!", model.getCode());
	}

	public class UnderlyingThing {
		public String getGeronimo() {
			return "Geronimooo!";
		}
	}

	public class ExampleSpelModel extends SpelModel {

		public ExampleSpelModel(UnderlyingThing thing) {
			super(thing);
		}

		public String getCode() {
			return read("geronimo");
		}
	}
}
