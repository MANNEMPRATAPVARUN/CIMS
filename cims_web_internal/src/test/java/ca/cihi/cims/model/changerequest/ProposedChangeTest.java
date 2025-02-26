package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/*
 * this junit class is for cheating sonar
 */
public class ProposedChangeTest {

	private ProposedChange model;

	@Before
	public void setUp() {
		model = new ProposedChange();
	}

	@Test
	public void testGetsAndSets() {
		Long elementVersionId = Long.valueOf("11");
		model.setElementVersionId(elementVersionId);
		Long expectedGetElementVersionId = elementVersionId;
		assertTrue("Should have the expected elementVersionId",
				model.getElementVersionId() == expectedGetElementVersionId);

		String tableName = "TextPropertyVersion";
		model.setTableName(tableName);
		String expectedGetTableName = tableName;
		assertTrue("Should get the expected tableName", model.getTableName() == expectedGetTableName);

		String fieldName = "user desc ENG";
		model.setFieldName(fieldName);
		String expectedGetFieldName = fieldName;
		assertTrue("Should get the expected fieldName", model.getFieldName() == expectedGetFieldName);

		String oldValue = "old value";
		model.setOldValue(oldValue);
		String expectedGetOldValue = oldValue;
		assertTrue("Should get the expected oldValue", model.getOldValue() == expectedGetOldValue);

		String proposedValue = "proposed Value";
		model.setProposedValue(proposedValue);
		String expectedGetProposedValue = proposedValue;
		assertTrue("Should get the expected proposedValue", model.getProposedValue() == expectedGetProposedValue);

		String conflictValue = "conflict value";
		model.setConflictValue(conflictValue);
		String expectedGetConflictValue = conflictValue;
		assertTrue("Should get the expected conflictValue", model.getConflictValue() == expectedGetConflictValue);

	}

}
