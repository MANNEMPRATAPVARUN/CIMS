package ca.cihi.cims.model.search;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SearchValidationResponseTest {
	
	private SearchValidationResponse bean;
	
	@Before
	public void setUp() {
		bean = new SearchValidationResponse();		
	}

	@Test
	public void testGetsAndSets(){
		bean.setToken("123");
		bean.setStatus("fail");
		List<String> errorList = new ArrayList<String>();
		errorList.add("error1");
		bean.setErrors(errorList);
		assertTrue(bean.getToken().equals("123"));
		assertTrue(bean.getStatus().equals("fail"));
		assertTrue(bean.getErrors().get(0).equals("error1"));		
	}
}
