package ca.cihi.cims.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformQualifierlistServiceImplTest {

	@Autowired
	private TransformQualifierlistService xslTransformerQualifierlist;

	@Test
	public void testTransformQualifierlistString() {
		String space = "\\s+";
		String sourceXmlString = "<qualifierlist type=\"note\"><note><label>Disregard intermittent attempts at weaning from ventilation support. This is expected and these periods should be included in the total hours.  Calculate from time intubated to finally extubated.</label></note></qualifierlist>";
		String resultHtml = xslTransformerQualifierlist.transformQualifierlistString(sourceXmlString);
		String expectedHtml = "Disregard intermittent attempts at weaning from ventilation support. This is expected and these periods should be included in the total hours.  Calculate from time intubated to finally extubated.<br/>";

		assertEquals("Should have the same string", resultHtml.replaceAll(space, ""),
				expectedHtml.replaceAll(space, ""));

	}

}
