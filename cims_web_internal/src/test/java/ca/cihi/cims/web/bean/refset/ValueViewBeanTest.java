package ca.cihi.cims.web.bean.refset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ValueViewBeanTest {

	@Test
	public void testValueViewBean() {
		ValueViewBean viewBean1 = new ValueViewBean();
		viewBean1.setColumnElementId(1L);
		ValueViewBean viewBean2 = null;
		ValueViewBean viewBean3 = new ValueViewBean();
		viewBean3.setColumnElementId(3l);
		ValueViewBean viewBean4 = new ValueViewBean();
		assertTrue(viewBean1.equals(viewBean1));
		assertFalse(viewBean1.equals(viewBean2));
		assertFalse(viewBean1.equals(new Object()));
		assertFalse(viewBean1.equals(viewBean3));
		assertFalse(viewBean4.equals(viewBean3));
		viewBean2 = new ValueViewBean();
		viewBean2.setColumnElementId(1L);
		viewBean2.setColumnElementVersionId(1L);
		assertFalse(viewBean1.equals(viewBean2));
		viewBean1.setColumnElementVersionId(1L);
		assertTrue(viewBean1.equals(viewBean2));
		viewBean1.setIdValue(1L);
		assertFalse(viewBean1.equals(viewBean2));
		viewBean2.setIdValue(1L);
		assertTrue(viewBean1.equals(viewBean2));
		viewBean1.setTextValue("Test");
		assertFalse(viewBean2.equals(viewBean1));
		viewBean2.setTextValue("Test");
		assertTrue(viewBean1.equals(viewBean2));
	}
}
