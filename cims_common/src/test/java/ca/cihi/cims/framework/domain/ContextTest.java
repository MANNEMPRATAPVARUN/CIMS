package ca.cihi.cims.framework.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.enums.ContextStatus;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ContextTest {

	@Test
	public void test() {
		ClasssDTO classBase = new ClasssDTO("BaseRefset", "CDEX-Test", "CDEX-Test", "CDEX-Test Friendly");

		Classs classs = Classs.create(classBase);

		Context context = Context.createInceptionVersion(classs, "CDEX-Test", "V1.0");

		Assert.assertNotNull(context);

		Context contextV1 = Context.findById(context.getContextId());

		Assert.assertNotNull(contextV1);
		Assert.assertEquals(context.getVersionCode(), contextV1.getVersionCode());
		Assert.assertEquals(context.getContext().getContextStatus(), contextV1.getContextStatus());

		Context contextV2 = contextV1.createSubsequentVersion("V1.1");
		Assert.assertNotNull(contextV2);
		Assert.assertEquals(contextV1.getElementIdentifier().getElementId(),
				contextV2.getElementIdentifier().getElementId());
		Assert.assertNotEquals(contextV1.getContextId(), contextV2.getContextId());

		contextV2.remove();

		Context v2Query = Context.findById(contextV2.getContextId());
		Assert.assertEquals(ContextStatus.DELETED, v2Query.getContextStatus());

		contextV1.closeContext();
		Assert.assertEquals(contextV1.getLatestClosedVersion(),context.getContextId());
		contextV1.remove();

	}
}
