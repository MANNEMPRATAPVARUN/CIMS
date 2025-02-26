package ca.cihi.cims.model.changerequest;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;



public class ChangeRequestTest {
	private  ChangeRequest cr;
	@Before
	public void setUp()	    {
		cr = new ChangeRequest();
	}

	@Test
	public void testGetsAndSets(){
		cr.setAssigneeDLId(null);
		assertTrue("Should have the expected AssigneeDLId", cr.getAssigneeDLId()==null);
		cr.setAssigneeUserId(5L);
		assertTrue("Should have the expected AssigneeUserId", cr.getAssigneeUserId().longValue()==5l);
		cr.setAssignorId(5L);
		assertTrue("Should have the expected AssignorId", cr.getAssignorId().longValue()==5l);
		cr.setBaseClassification("ICD-10-CA");
		assertTrue("Should have the expected BaseClassification", cr.getBaseClassification().equals("ICD-10-CA"));
		cr.setBaseContextId(1L);
		assertTrue("Should have the expected BaseContextId", cr.getBaseContextId().longValue()==1l);
		cr.setBaseVersionCode("2016");
		assertTrue("Should have the expected BaseVersionCode", cr.getBaseVersionCode().equals("2016"));
		cr.setCategory(ChangeRequestCategory.T); // tabular
		cr.getCategory().getCode();
		cr.setChangeNatureId(16L);
		cr.getChangeNatureId();
		cr.setChangeRationalTxt("rational");
		cr.getChangeRationalTxt();
		cr.setChangeRequestId(1L);
		cr.getChangeRequestId();
		ChangeSummary cs = new ChangeSummary();
		cr.setChangeSummary(cs);
		cr.getChangeSummary();
		cr.setChangeTypeId(1L);
		cr.getChangeTypeId();
		cr.setConversionRequired(true);
		cr.isConversionRequired();
		cr.setCreatedByUserId(5L);
		cr.getCreatedByUserId();
		cr.setCreationDate(new Date());
		cr.getCreationDate();
		cr.setDeferredChangeRequestId(2L);
		cr.getDeferredChangeRequestId();
		cr.setDeferredToBaseContextId(2L);
		cr.getDeferredToBaseContextId();
		cr.setEvolutionRequired(true);
		cr.isEvolutionRequired();
		cr.setIndexRequired(true);
		cr.isIndexRequired();
		cr.setLanguageCode("ENG");
		cr.getLanguageCode();





	}

}
