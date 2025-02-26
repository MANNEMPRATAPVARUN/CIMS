package ca.cihi.cims.hg.mapper.config;

import org.junit.Assert;
import org.junit.Test;

public class MethodFinderTest {

	@Test
	public void testMethodsFoundCorrectly() {
		MethodFinder finder = new MethodFinder(MyEntity.class);

		Assert.assertEquals(
				"The methodfinder should find the correct number of method bundles.",
				2, finder.getProperties().size());

		PropertyMethods foo = finder.getProperty("foo");
		Assert.assertEquals(2, foo.getSetterMethods().size());
		Assert.assertEquals(3, foo.getGetterMethods().size());

		PropertyMethods chocolate = finder.getProperty("chocolate");
		Assert.assertEquals(0, chocolate.getSetterMethods().size());
		Assert.assertEquals(1, chocolate.getGetterMethods().size());

	}

	private abstract static class MyEntity {

		public abstract void setFoo(String value);

		public abstract void setFoo(String incidental, String misleading,
				String value);

		public abstract String getFoo();

		public abstract String getFoo(String language);

		public abstract String getFoo(String language, String misleading,
				String alsoMisleading);

		public abstract String getChocolate();

	}
}
