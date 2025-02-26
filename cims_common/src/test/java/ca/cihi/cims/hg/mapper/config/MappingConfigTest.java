package ca.cihi.cims.hg.mapper.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.shared.index.LetterIndex;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-test.xml" })
public class MappingConfigTest {
	@Autowired
	private MappingConfig config;

	@Test
	@SuppressWarnings("rawtypes")
	public void testFindWrapperByClassname() {

		String baseClassification = "ICD-10-CA";
		String elementClassName = "Block";
		Class expected = IcdTabular.class;

		assertTrue(config.isDefined(IcdTabular.class));

		assertEquals("We should get the correct wrapper class.", expected,
						config.forElementClassName(baseClassification, elementClassName).getWrapperClass());

	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testFindWrapperByClassname1() {

		String baseClassification = "ICD-10-CA";
		String elementClassName = "LetterIndex";
		Class expected = LetterIndex.class;

		assertTrue(config.isDefined(LetterIndex.class));

		assertEquals("We should get the correct wrapper class.", expected,
						config.forElementClassName(baseClassification, elementClassName).getWrapperClass());

	}
}
