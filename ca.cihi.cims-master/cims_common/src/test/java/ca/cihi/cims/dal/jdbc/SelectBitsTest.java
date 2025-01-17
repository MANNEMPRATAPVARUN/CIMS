package ca.cihi.cims.dal.jdbc;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;

public class SelectBitsTest {

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void noWhere() {
		SelectBits bits = new SelectBits();
		bits.addColumn("*");
		bits.addTable("Foo");

		Assert.assertEquals("SELECT * FROM Foo", bits.toString());
	}

	@Test
	public void subQuery() {

		SelectBits elementIds = new SelectBits();

		elementIds.addColumn("ElementId");
		elementIds.addTable("Element");

		SelectBits outer = new SelectBits();
		outer.addColumn("elementversionid");
		outer.addTable("ElementVersion");
		outer.addWhere("ElementVersion.elementId in (" + elementIds + ")");

		LOGGER.debug(outer);

		SelectBits inner = new SelectBits();

		inner.addColumn("ElementId");
		inner.addTable("Element");
		inner.addColumn("classid");

		SelectBits outer2 = new SelectBits();
		outer2.addColumn("*");
		outer2.addTable("(" + inner + ") e");
		outer2.addTable("class c");
		outer2.addWhere("e.classId=c.classId");
		LOGGER.debug(outer2);

	}

	@Test
	public void testAliases() {

		ClassORMapping west = new ClassORMapping(null, Object.class, "West", "WestId");
		ColumnMapping westlang = west.column("language", "Language", new StringTranslator());

		ClassORMapping east = new ClassORMapping(null, Object.class, "East", "EastId");
		ColumnMapping eastlang = east.column("language", "Language", new StringTranslator());

		SelectBits bits = new SelectBits();
		bits.addTable("West");
		bits.addTable("East");

		String alias1 = bits.addColumn(westlang);
		String alias2 = bits.addColumn(eastlang);

		LOGGER.debug(alias1);
		LOGGER.debug(alias2);

		Assert.assertFalse(alias1.equals(alias2));

	}
}
