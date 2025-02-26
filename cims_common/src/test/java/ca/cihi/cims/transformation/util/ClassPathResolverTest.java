package ca.cihi.cims.transformation.util;


import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class of ClassPathResolver.
 * 
 * @author wxing
 *
 */
public class ClassPathResolverTest{
	private static final Log LOGGER = LogFactory.getLog(ClassPathResolverTest.class);
	private ClassPathResolver classPathResolver;
	
	@Before
	public void setUp()
	{
		classPathResolver = new ClassPathResolver();
	}
	
	@Test
	public void testResolveEntity(){
		
		try{
			Assert.assertTrue(classPathResolver.resolveEntity("", "/dtd/cihi_cims.dtd") != null);
		
			Assert.assertTrue(classPathResolver.resolveEntity("", "/src/main/dtd/cihi_cims.dtd") == null);
		}catch(IOException ex){			
			LOGGER.error("   " + ex.getMessage());
		}
	}

}