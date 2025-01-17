package ca.cihi.cims.hg.mapper.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGClassDeterminant;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;

public class MappingConfigReaderPropertyTest {

	private MappingConfigReader reader;

	@Before
	public void setup() {
		reader = new MappingConfigReader();
	}

	@Test
	public void testReadDataProperty() {
		reader.addClass(MyEntity.class);

		WrapperConfig entity = reader.getConfig().getEntity(MyEntity.class);

		DataPropertyConfig desc = (DataPropertyConfig) entity.getProperties()
				.get("description");

		Assert.assertEquals("description", desc.getPropertyElementClassName());
	}

	@Test
	public void testParameterizedProperties() {
		reader.addClass(MyEntity2.class);
		WrapperConfig entity = reader.getConfig().getEntity(MyEntity2.class);

		Assert.assertTrue("Language-parameterized properties must show up.",
				entity.getProperties().containsKey("description"));
	}

	@Test
	public void testIcdTabularBuilds() {
		reader.addClass(IcdTabular.class);
	}

	@Test
	public void testConceptProperty() {
		reader.addClass(MyConcept.class);
		WrapperConfig entity = reader.getConfig().getEntity(MyConcept.class);

		ConceptPropertyConfig conceptPropertyConfig = (ConceptPropertyConfig) entity
				.getProperties().values().iterator().next();

		Assert.assertNotNull(conceptPropertyConfig);
	}

	@Test(expected = IllegalStateException.class)
	public void testTooManyAnnotationsOnGetter() {
		reader.addClass(TooManyAnnotations.class);
	}

	@HGWrapper("mine")
	@HGBaseClassification("testing")
	private abstract static class MyEntity {

		@HGProperty(className = "description", elementClass = TextPropertyVersion.class)
		public abstract String getDescription();

		public abstract void setDescription(String desc);

	}

	@HGWrapper("mine")
	@HGBaseClassification("testing")
	private abstract static class MyEntity2 {
		@HGProperty(className = "ehrData", elementClass = TextPropertyVersion.class)
		public abstract String getDescription(@HGLang String language);

		public abstract void setDescription(@HGLang String language,
				String value);
	}

	@HGWrapper("mine")
	@HGBaseClassification("testing")
	private abstract static class TooManyAnnotations {
		@HGProperty(className = "ehrData", elementClass = TextPropertyVersion.class)
		@HGConceptProperty(relationshipClass = "ehrData")
		@HGClassDeterminant(classes = { "mine" })
		public abstract String getDescription();
	}

	@HGWrapper("mine")
	@HGBaseClassification("testing")
	private abstract static class MyConcept {

		@HGConceptProperty(relationshipClass = "friend")
		public abstract MyConcept getFriend();

		public abstract void setFriend(MyConcept friend);

	}
}
