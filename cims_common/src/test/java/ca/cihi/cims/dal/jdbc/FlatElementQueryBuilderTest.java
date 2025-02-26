package ca.cihi.cims.dal.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.dal.ContextIdentifier;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class FlatElementQueryBuilderTest {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	private FlatElementQueryBuilder builder;

	@Autowired
	ContextFinder finder;

	@Test
	public void testElementQuery() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ParamNamer params = new ParamNamer();
		SelectBits select = builder.buildElementQuery(contextId, params);

		int propCount = jdbc.queryForObject("select count(*) from (" + select + ") e", params.getParamMap(),
				Integer.class);

		Assert.assertTrue("We should have found at least a few elements.", propCount > 0);
	}

	@Test
	public void testElementQueryIncludesStatus() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		String query = builder.buildElementQuery(contextId, new ParamNamer()).toString();
		Assert.assertTrue("The query must contain the status.", query.toUpperCase().contains("STATUS"));
	}

}
