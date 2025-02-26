package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.query.Restriction;
import ca.cihi.cims.hg.mapper.config.MappingConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-test.xml" })
public class ElementQueryAssemblerTest {

	@Autowired
	private MappingConfig mappingConfig;

	@Test
	public void testWithoutCriteria() {

		Ref<CciTabular> wrapRef = ref(CciTabular.class);

		ArrayList<FindCriterion> criteria = new ArrayList<FindCriterion>();

		criteria.add(wrapRef.eq("code", "A00"));

		ElementQueryAssembler assembler = new ElementQueryAssembler(wrapRef, criteria, mappingConfig);

		List<Restriction> restrictions = assembler.getRestrictions();

		for (Restriction r : restrictions) {
			System.err.println(r);
		}

	}

	@Test
	public void exampleTest() {

		Ref parent = ref(IcdTabular.class);

		Ref child = ref(IcdTabular.class);

		ArrayList<FindCriterion> criteria = new ArrayList<FindCriterion>();

		criteria.add(child.link("parent", parent));
		criteria.add(child.eq("code", "A00"));

		ElementQueryAssembler assembler = new ElementQueryAssembler(parent, criteria, mappingConfig);

		for (Restriction r : assembler.getRestrictions())
			System.err.println(r);

	}
}
