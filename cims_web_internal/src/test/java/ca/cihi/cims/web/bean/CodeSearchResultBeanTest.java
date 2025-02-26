package ca.cihi.cims.web.bean;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



/*
 * cheating sonar
 */
public class CodeSearchResultBeanTest {
	CodeSearchResultBean bean;
	@Before
	public void setUp()	    {
		bean = new CodeSearchResultBean("label","value","path");
	}
	@Test
	public void testGets(){
		assertTrue("Should have the expected lable", bean.getLabel().equals("label"));
		assertTrue("Should have the expected value", bean.getValue().equals("value"));
		assertTrue("Should have the expected path", bean.getConceptId().equals("path"));

	}

}
