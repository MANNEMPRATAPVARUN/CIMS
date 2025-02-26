package ca.cihi.cims.hg.mapper;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.hg.ContextElementAccess;
import ca.cihi.cims.bll.hg.HGWrapperFactory;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.config.MappingConfigReader;

public class HGWrapperFactoryTest {
	@Test
	public void test() {

		MappingConfigReader reader = new MappingConfigReader();
		reader.addClass(Block.class);

		HGWrapperFactory fact = new HGWrapperFactory();
		fact.setMappingConfig(reader.getConfig());

		ContextElementAccess ops = mock(ContextElementAccess.class);
		when(ops.findProperty("description", 200L, "ENG")).thenReturn(null);

		Block block = fact.newInstance(Block.class, ops);
		((Identified) block).setElementId(200L);

		Assert.assertEquals("Title", block.getTitle());

		block.getDescription();

	}

	@HGWrapper("BLK")
	@HGBaseClassification("ICD-10-CA")
	public abstract static class Block {

		@HGProperty(className = "description", elementClass = TextPropertyVersion.class)
		public abstract String getDescription();

		public String getTitle() {
			return "Title";
		}
	}
}
