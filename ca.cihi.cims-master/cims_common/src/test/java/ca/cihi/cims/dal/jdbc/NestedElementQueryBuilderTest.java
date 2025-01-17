package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.query.ClassIn;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.FieldEq;
import ca.cihi.cims.dal.query.MightBeA;
import ca.cihi.cims.dal.query.Restriction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class NestedElementQueryBuilderTest {

	@Autowired
	private ORConfig config;
	@Autowired
	private ContextRestrictionQueryBuilder queryBuilder;

	// ---------------------------------------------------------------------

	@Test
	public void testQuery() {
		ParamNamer namer = new ParamNamer();
		ContextIdentifier context = new ContextIdentifier(1L, CIMSTestConstants.TEST_VERSION, "CCI", null, "OPEN",
				null, true, null, null);
		NestedElementQueryBuilder builder = new NestedElementQueryBuilder(context, config, queryBuilder, namer);

		ElementRef prop = new ElementRef(PropertyVersion.class);
		ElementRef textProp = new ElementRef(TextPropertyVersion.class);

		List<Restriction> restrictions = new ArrayList<Restriction>();
		restrictions.add(new MightBeA(prop, textProp));
		restrictions.add(new FieldEq(prop, "elementId", 5L));
		restrictions.add(new ClassIn(textProp, Arrays.asList("Code", "ShortDescription")));
		restrictions.add(new FieldEq(textProp, "languageCode", "ENG"));

		SelectBits query = builder.buildQuery(restrictions);
		System.err.println(new SqlFormatter().format(query.toString(), namer.getParamMap()));
	}

}
