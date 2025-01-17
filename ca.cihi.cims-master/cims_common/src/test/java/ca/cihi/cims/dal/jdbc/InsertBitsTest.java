package ca.cihi.cims.dal.jdbc;

import org.junit.Assert;
import org.junit.Test;

public class InsertBitsTest {

	@Test
	public void testSimpleInsert() {
		InsertBits bit = new InsertBits("TABLE");

		bit.addColumn("ID");
		bit.addColumn("SIZE", "size");

		Assert.assertEquals("INSERT INTO TABLE (ID, SIZE) VALUES ( ?, :size )",
				bit.toString());

	}

}
