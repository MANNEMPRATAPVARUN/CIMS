package ca.cihi.cims.bll.hg;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-test.xml" })
public class HgContextProviderTest {
	@Autowired
	private ContextProvider provider;
	
	@Test
	public void testFindBaseContextIdentifier(){
		String test_baseClassification ="ICD-10-CA";
		Collection<ContextIdentifier> contextIdentifiers =provider.findBaseContextIdentifiers(test_baseClassification);
		contextIdentifiers.size();
	
	}
	
	
}
