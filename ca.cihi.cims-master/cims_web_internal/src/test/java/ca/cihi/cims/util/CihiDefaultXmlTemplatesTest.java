package ca.cihi.cims.util;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;

import ca.cihi.cims.util.CihiDefaultXmlTemplates.TemplateType;

public class CihiDefaultXmlTemplatesTest {

	@Test
	public void test() throws Exception {
		CihiDefaultXmlTemplates templates = new CihiDefaultXmlTemplates();
		for (TemplateType type : TemplateType.values()) {
			assertNotNull(templates.get(type));
		}
	}

}
