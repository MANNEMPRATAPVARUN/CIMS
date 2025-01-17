package ca.cihi.cims.hg.mapper.config;

import org.junit.Assert;
import org.junit.Test;

import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

public class MappingConfigReaderInheritanceTest {

	@Test
	public void testReadBaseClassificationFromPackage() {

		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(MyEntity.class);

		WrapperConfig entity = reader.getConfig().getEntity(MyEntity.class);

		PropertyConfig property = entity.getProperty("newsReport");

		Assert.assertNotNull(property);
	}

	public abstract static class MyEntity extends MyParent {

	}

	@HGWrapper("juliet")
	@HGBaseClassification("Oceanic")
	public abstract static class MyParent {
		@HGProperty(className = "Inherited", elementClass = TextPropertyVersion.class)
		public abstract String getNewsReport();
	}

}
