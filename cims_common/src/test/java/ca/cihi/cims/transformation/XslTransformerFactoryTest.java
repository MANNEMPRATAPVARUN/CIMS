package ca.cihi.cims.transformation;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * Test class of XslTransformerFactory.
 * 
 * @author wxing
 *
 */
public class XslTransformerFactoryTest {
	
	@Test
	public void test() {

		final Resource doNothingXSL = new ClassPathResource("copy.xsl",
				this.getClass());

		Assert.assertTrue("The test xsl file must exist.",
				doNothingXSL.exists());		
				
		final XslTransformer transformer = new XslTransformerFactory()
		.create(doNothingXSL);
		
		Assert.assertNotNull(transformer.getTransformer());		 
	}
}