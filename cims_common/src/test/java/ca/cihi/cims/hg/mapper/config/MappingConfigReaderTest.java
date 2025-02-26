package ca.cihi.cims.hg.mapper.config;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGClassDeterminant;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;

public class MappingConfigReaderTest {

	@Test
	public void testReadBaseClassificationFromPackage() {

		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(MyEntity.class);

		WrapperConfig squid = reader.getConfig().getEntity(MyEntity.class);

		Assert.assertEquals("Oceanic", squid.getBaseClassification().getName());

	}

	@Test
	public void readClassFromEntityAnnotation() {
		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(MyEntity.class);

		WrapperConfig entity = reader.getConfig().getEntity(MyEntity.class);

		ClassNameFixed fixedClassname = (ClassNameFixed) entity
				.getClassNameStrategy();
		String className = fixedClassname.getClassName(new MyEntity());

		Assert.assertEquals("juliet", className);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoClassStrategy() {
		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(NoClassStrategy.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testTwoStrategiesAreIllegal() {
		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(TwoClassNameStrategies.class);
	}

	@Test
	public void testCollectionProperties() {
		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(Collector.class);

		WrapperConfig ownerConfig = reader.getConfig()
				.getEntity(Collector.class);

		DataPropertyConfig property = (DataPropertyConfig) ownerConfig
				.getProperty("names");
		Assert.assertTrue(property.isCollection());

		ConceptPropertyConfig friendConfig = (ConceptPropertyConfig) ownerConfig
				.getProperty("friends");

		//Assert.assertEquals(Collector.class, friendConfig.getConceptType());
		Assert.assertTrue(friendConfig.isCollection());
		Assert.assertTrue(friendConfig.isInverse());
	}

	@HGWrapper("juliet")
	@HGBaseClassification("Oceanic")
	public static class MyEntity {

	}

	@HGWrapper
	@HGBaseClassification("Oceanic")
	public static class NoClassStrategy {
		// This class has no information telling the mapper how to figure out
		// what HG class to use

	}

	@HGWrapper("duplicate")
	@HGBaseClassification("Oceanic")
	private abstract static class TwoClassNameStrategies {

		@HGClassDeterminant(classes = { "A", "B" })
		@HGProperty(className = "duplicate", elementClass = TextPropertyVersion.class)
		public abstract String getType();

		public abstract void setType(String string);
	}

	@HGWrapper("hasCollection")
	@HGBaseClassification("Oceanic")
	private abstract static class Collector {
		@HGProperty(className = "folly", elementClass = TextPropertyVersion.class)
		public abstract Collection<String> getNames();

		@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
		public abstract Collection<Collector> getFriends();
	}

}
