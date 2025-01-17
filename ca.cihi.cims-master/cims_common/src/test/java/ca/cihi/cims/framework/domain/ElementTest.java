package ca.cihi.cims.framework.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ElementTest {

	@Test
	public void test() {
		String bk = Element.generateBusinessKey();
		Assert.assertNotNull(bk);

		Boolean exists = Element.existsInContext(bk, 1l);

		Assert.assertFalse(exists);
	}
}
