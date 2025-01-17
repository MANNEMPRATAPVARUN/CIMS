package ca.cihi.cims.framework.dao;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.mapper.ClasssMapper;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Transactional
@Rollback
public class ClasssMapperTest {

	@Autowired
	private ClasssMapper classsMapper;

	@Test
	public void testClasssMapper() {
		ClasssDTO newClasss = new ClasssDTO("BaseRefset", "CDEX-TEST", "CDEX-TEST", "CDEX-TEST Friendly");

		Map<String, Object> params = new HashMap<>();
		params.put("classsId", 0l);
		params.put("baseClassificationName", newClasss.getBaseClassificationName());
		params.put("classsName", newClasss.getClasssName());
		params.put("tableName", newClasss.getTableName());
		params.put("friendlyName", newClasss.getFriendlyName());
		classsMapper.createClasss(params);

		Long classsId = (Long) params.get("classsId");

		ClasssDTO data = classsMapper.getClasss(classsId);

		System.out.println(classsId);

		Assert.assertEquals(classsId, data.getClasssId());

		System.out.println(data.getClasssName());

		Map<String, Object> params1 = new HashMap<>();
		params1.put("baseClassificationName", newClasss.getBaseClassificationName());
		params1.put("classsName", newClasss.getClasssName());

		ClasssDTO cdex = classsMapper.getClasssByClasssNameAndBaseClassificationName(params1);

		Assert.assertNotNull(cdex);
		Assert.assertEquals("CDEX-TEST Friendly", cdex.getFriendlyName());
	}
}
