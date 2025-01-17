package ca.cihi.cims.service.search;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.search.Search;

public class SearchTokenServiceTest {
	
	private SearchTokenService bean;
	
	@Before
	public void setUp() {
		bean = new SearchTokenService();		
	}

	@Test
	public void testCheck(){
		assertFalse(bean.check("123"));
	}
	
	@Test
	public void testGenerate(){
		assertTrue(bean.generate(new Search())!=null);
	}
	
	@Test
	public void testRemove(){
		Search search = new Search();
		String token = bean.generate(search);
		assertTrue(bean.check(token));
		bean.remove(token);
		assertFalse(bean.check(token));
	}
	
	
}
