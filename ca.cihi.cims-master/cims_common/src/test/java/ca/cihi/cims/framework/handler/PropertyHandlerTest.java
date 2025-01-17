package ca.cihi.cims.framework.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.mapper.PropertyMapper;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class PropertyHandlerTest {

	private ConceptPropertyHandler conceptPropertyHandler;
	private TextPropertyHandler handler;
	@Autowired
	private PropertyMapper propertyMapper;

	@Before
	public void setup() {
		handler = new TextPropertyHandler();
		conceptPropertyHandler = new ConceptPropertyHandler();
		handler.setPropertyMapper(propertyMapper);
		conceptPropertyHandler.setPropertyMapper(propertyMapper);
	}

	@Test
	public void testHandler() {
		PropertyDTO longTitle = handler.findPropertyElementInContext(3103461l, 45l, "LongTitle", Language.ENG);
		Assert.assertEquals("Certain infectious and parasitic diseases (A00-B99)", longTitle.getValue().toString());

		PropertyDTO narrower = conceptPropertyHandler.findPropertyElementInContext(3103461l, 45l, "Narrower",
				Language.NOLANGUAGE);
		Assert.assertEquals("38", narrower.getValue().toString());
	}
}
