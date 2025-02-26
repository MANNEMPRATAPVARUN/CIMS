package ca.cihi.cims.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.ClasssDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ClasssTest {

	@Test
	public void test() {
		ClasssDTO data = new ClasssDTO();
		data.setBaseClassificationName("CDEX");
		data.setClasssName("CDEX");
		data.setTableName("BaseRefset");
		data.setFriendlyName("CDEX-Friendly");
		Classs classs = Classs.create(data);

		Assert.assertNotNull(classs);
		Assert.assertNotNull(classs.getClassId());

		Assert.assertEquals("CDEX", classs.getBaseClassificationName());

		Classs classsQuery = Classs.findById(classs.getClassId());
		Assert.assertNotNull(classsQuery);

		Classs classsByName = Classs.findByName("CDEX", "CDEX");
		Assert.assertNotNull(classsByName);

		List<String> classsNames = new ArrayList<>();
		classsNames.add("CDEX");
		List<Classs> classses = Classs.findByNames(classsNames, "CDEX");
		Assert.assertNotNull(classses);
		Assert.assertEquals(1, classses.size());

	}
}
