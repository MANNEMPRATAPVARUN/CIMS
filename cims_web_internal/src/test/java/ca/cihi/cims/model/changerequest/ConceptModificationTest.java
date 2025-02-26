package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.IndexBookType;

/*
 * this junit class is for cheating sonar
 */
public class ConceptModificationTest {

	private ConceptModification model;

	@Before
	public void setUp() {
		model = new ConceptModification();
	}

	@Test
	public void testGetsAndSets() {

		String breadCrumbs = "Section III -- Table of Drugs and Chemicals Index > A";
		model.setBreadCrumbs(breadCrumbs);
		String expectedBreadCrumbs = breadCrumbs;
		assertTrue("Should get the expected breadCrumbs", model.getBreadCrumbs() == expectedBreadCrumbs);

		Long changeRequestId = Long.valueOf("11");
		model.setChangeRequestId(changeRequestId);
		Long expectedChangeRequestId = changeRequestId;
		assertTrue("Should have the expected changeRequestId", model.getChangeRequestId() == expectedChangeRequestId);

		String code = "A00";
		model.setCode(code);
		String expectedCode = code;
		assertTrue("Should get the expected code", model.getCode() == expectedCode);
		code = "It's test";
		model.setCode(code);
		expectedCode = "It&rsquo;s test";
		assertTrue("Should get the expected code", model.getCode().endsWith(expectedCode));

		String conceptClassName = "Index";
		model.setConceptClassName(conceptClassName);
		String expectedConceptClassName = conceptClassName;
		assertTrue("Should get the expected conceptClassName", model.getConceptClassName() == expectedConceptClassName);

		Long elementId = Long.valueOf("372543");
		model.setElementId(elementId);
		Long expectedElementId = elementId;
		assertTrue("Should have the expected elementId", model.getElementId() == expectedElementId);

		String indexPath = "Section III -- Table of Drugs and Chemicals Index > B";
		model.setIndexPath(indexPath);
		String expectedIndexPath = indexPath;
		assertTrue("Should have the expected indexPath", model.getIndexPath() == expectedIndexPath);

		String indexTerm = "baby";
		model.setIndexTerm(indexTerm);
		String expectedIndexTerm = indexTerm;
		assertTrue("Should have the expected indexTerm", model.getIndexTerm() == expectedIndexTerm);

		List<ProposedChange> proposedAndConflictIndexChanges = new ArrayList<ProposedChange>();
		model.setConceptClassName(IndexBookType.A.getCode());
		model.setProposedAndConflictIndexChanges(proposedAndConflictIndexChanges);
		List<ProposedChange> expectedProposedAndConflictIndexChanges = proposedAndConflictIndexChanges;
		assertTrue("Should have the expected proposedAndConflictIndexChanges",
				model.getProposedAndConflictIndexChanges() == expectedProposedAndConflictIndexChanges);

		List<ProposedChange> proposedAndConflictSupplementChanges = new ArrayList<ProposedChange>();
		model.setProposedAndConflictSupplementChanges(proposedAndConflictSupplementChanges);
		List<ProposedChange> expectedProposedAndConflictSupplementChanges = proposedAndConflictSupplementChanges;
		assertTrue("Should have the expected proposedAndConflictSupplementChanges",
				model.getProposedAndConflictSupplementChanges() == expectedProposedAndConflictSupplementChanges);

		List<ProposedChange> proposedAndConflictTabularChanges = new ArrayList<ProposedChange>();
		model.setProposedAndConflictTabularChanges(proposedAndConflictTabularChanges);
		List<ProposedChange> expectedProposedAndConflictTabularChanges = proposedAndConflictTabularChanges;
		assertTrue("Should have the expected proposedAndConflictTabularChanges",
				model.getProposedAndConflictTabularChanges() == expectedProposedAndConflictTabularChanges);

		List<ProposedChange> proposedAndConflictValidationChanges = new ArrayList<ProposedChange>();
		model.setProposedAndConflictValidationChanges(proposedAndConflictValidationChanges);
		List<ProposedChange> expectedProposedAndConflictValidationChanges = proposedAndConflictValidationChanges;
		assertTrue("Should have the expected proposedAndConflictValidationChanges",
				model.getProposedAndConflictValidationChanges() == expectedProposedAndConflictValidationChanges);

		ArrayList<ProposedChange> proposedIndexChanges = new ArrayList<ProposedChange>();
		model.setConceptClassName(IndexBookType.A.getCode());
		model.setProposedIndexChanges(proposedIndexChanges);
		ArrayList<ProposedChange> expectedProposedIndexChanges = proposedIndexChanges;
		assertTrue("Should have the expected proposed index changes",
				model.getProposedIndexChanges().equals(expectedProposedIndexChanges));

		ProposedChange proposedIndexRefChange = new ProposedChange();
		model.setProposedIndexRefChange(proposedIndexRefChange);
		ProposedChange expectedGetProposedIndexRefChange = proposedIndexRefChange;
		assertTrue("Should have the expected proposed index ref change",
				model.getProposedIndexRefChange() == expectedGetProposedIndexRefChange);

		ArrayList<ProposedChange> proposedSupplementChanges = new ArrayList<ProposedChange>();
		model.setProposedSupplementChanges(proposedSupplementChanges);
		ArrayList<ProposedChange> expectedProposedSupplementChanges = proposedSupplementChanges;
		assertTrue("Should have the expected proposed supplement changes",
				model.getProposedSupplementChanges().equals(expectedProposedSupplementChanges));

		ArrayList<ProposedChange> proposedTabularChanges = new ArrayList<ProposedChange>();
		model.setProposedTabularChanges(proposedTabularChanges);
		ArrayList<ProposedChange> expectedProposedTabularChanges = proposedTabularChanges;
		assertTrue("Should have the expected proposed tabular changes",
				model.getProposedTabularChanges() == expectedProposedTabularChanges);

		ArrayList<ValidationChange> proposedValidationChanges = new ArrayList<ValidationChange>();
		model.setProposedValidationChanges(proposedValidationChanges);
		ArrayList<ValidationChange> expectedProposedChanges = proposedValidationChanges;
		assertTrue("Should have the expected proposed Validation changes",
				model.getProposedValidationChanges() == expectedProposedChanges);

		ArrayList<RealizedChange> realizedIndexChanges = new ArrayList<RealizedChange>();
		model.setConceptClassName(IndexBookType.E.getCode());
		model.setRealizedIndexChanges(realizedIndexChanges);
		ArrayList<RealizedChange> expectedRealizedIndexChanges = realizedIndexChanges;
		assertTrue("Should have the expected Realized index changes",
				model.getRealizedIndexChanges().equals(expectedRealizedIndexChanges));

		RealizedChange realizedIndexRefChange = new RealizedChange();
		model.setRealizedIndexRefChange(realizedIndexRefChange);
		RealizedChange expectedRealizedIndexRefChange = realizedIndexRefChange;
		assertTrue("Should have the expected Realized index ref change",
				model.getRealizedIndexRefChange().equals(expectedRealizedIndexRefChange));

		ArrayList<RealizedChange> realizedSupplementChanges = new ArrayList<RealizedChange>();
		model.setRealizedSupplementChanges(realizedSupplementChanges);
		ArrayList<RealizedChange> expectedRealizedSupplementChanges = realizedSupplementChanges;
		assertTrue("Should have the expected realized supplement changes",
				model.getRealizedSupplementChanges() == expectedRealizedSupplementChanges);

		ArrayList<RealizedChange> realizedTabularChanges = new ArrayList<RealizedChange>();
		model.setRealizedTabularChanges(realizedTabularChanges);
		ArrayList<RealizedChange> expectedRealizedTabularChanges = realizedTabularChanges;
		assertTrue("Should have the expected realized tabular changes",
				model.getRealizedTabularChanges() == expectedRealizedTabularChanges);

		ArrayList<ValidationChange> realizedValidationChanges = new ArrayList<ValidationChange>();
		model.setRealizedValidationChanges(realizedValidationChanges);
		ArrayList<ValidationChange> expectedRealizedValidationChanges = realizedValidationChanges;
		assertTrue("Should have the expected realized Validation changes",
				model.getRealizedValidationChanges() == expectedRealizedValidationChanges);

		Long structureId = Long.valueOf("143536");
		model.setStructureId(structureId);
		Long expectedStructureId = structureId;
		assertTrue("Should have the expected structureId", model.getStructureId() == expectedStructureId);

		Long validationId = Long.valueOf("1435678");
		model.setValidationId(validationId);
		Long expectedValidationId = validationId;
		assertTrue("Should have the expected validationId", model.getValidationId() == expectedValidationId);

		String versionCode = "2015";
		model.setVersionCode(versionCode);
		String expectedVersionCode = versionCode;
		assertTrue("Should have the expected versionCode", model.getVersionCode() == expectedVersionCode);

	}

	@Test
	public void testSetGetRealizedValidationChanges() {
		assertTrue(model.getRealizedValidationChanges() == null);

		List<RealizedChange> aRawRealizedValChanges = new ArrayList<RealizedChange>();
		RealizedChange aChange = new RealizedChange();
		aChange.setFieldName("DAD");
		aChange.setNewValue("newValue");
		aChange.setOldValue("oldValue");
		aChange.setTableName("tableName");
		aRawRealizedValChanges.add(aChange);

		model.setRawRealizedValidationChanges(aRawRealizedValChanges);
		assertFalse(model.getRawRealizedValidationChanges() == null);
		assertTrue(model.getRawRealizedValidationChanges().size() == 1);

		List<RealizedChange> bRawRealizedValChanges = new ArrayList<RealizedChange>();
		RealizedChange bChange = new RealizedChange();
		bChange.setFieldName("ACTIVE");
		bRawRealizedValChanges.add(bChange);

		RealizedChange cChange = new RealizedChange();
		cChange.setFieldName("DAD");
		cChange.setOldValue("newValue");
		cChange.setTableName("tableName");
		cChange.setNewValue("newValue2");
		bRawRealizedValChanges.add(cChange);

		model.setRawRealizedValidationChanges(bRawRealizedValChanges);
		assertTrue(model.getRawRealizedValidationChanges().size() == 2);

	}

}
