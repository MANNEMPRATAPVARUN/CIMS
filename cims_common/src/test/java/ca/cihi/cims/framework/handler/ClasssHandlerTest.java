package ca.cihi.cims.framework.handler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.ClasssDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ClasssHandlerTest {

	@Autowired
	private ClasssHandler classsHandler;

	@Test
	public void testClasssHandler() {
		ClasssDTO newClasss = new ClasssDTO("BaseRefset", "CDEX-TEST", "CDEX-TEST", "CDEX-TEST Friendly");

		classsHandler.createClasss(newClasss);

		Long classsId = newClasss.getClasssId();

		ClasssDTO savedClasss = classsHandler.getClasss(classsId);

		Assert.assertNotNull(savedClasss);

		Assert.assertEquals(newClasss, savedClasss);

		Assert.assertNotEquals(newClasss, null);

		ClasssDTO savedClasssByName = classsHandler.getClasss("CDEX-TEST", "CDEX-TEST");

		Assert.assertEquals(newClasss, savedClasssByName);

		List<String> classsNames = new ArrayList<>();
		classsNames.add("CDEX-TEST");
		List<ClasssDTO> classses = classsHandler.getClassses(classsNames, "CDEX-TEST");
		Assert.assertNotNull(classses);
		Assert.assertEquals(1, classses.size());

	}
}
