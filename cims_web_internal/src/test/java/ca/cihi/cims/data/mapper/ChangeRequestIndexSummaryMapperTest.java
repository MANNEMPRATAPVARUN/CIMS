package ca.cihi.cims.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.changerequest.ConceptModification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestIndexSummaryMapperTest {

	@Autowired
	private ChangeRequestIndexSummaryMapper changeRequestIndexSummaryMapper;

	@Test
	public void testFindModifiedIndexConceptElementCodes() {
/*
		// Change request 213: should have only one index concept returned
		Long maxStructureId = Long.valueOf("5413159");
		long changeRequestId = 213;

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("maxStructureId", maxStructureId);

		List<ConceptModification> modifiedIndexConcepts2 = changeRequestIndexSummaryMapper
				.findModifiedIndexConceptElementCodes(parameters);
		assertNotNull(modifiedIndexConcepts2);
		assertEquals(1, modifiedIndexConcepts2.size());
		ConceptModification conceptModification2 = modifiedIndexConcepts2.get(0);
		assertEquals(maxStructureId, conceptModification2.getStructureId());
		assertEquals("AlphabeticIndex", conceptModification2.getConceptClassName());
		assertEquals(Long.valueOf("459168"), conceptModification2.getElementId());
		assertEquals("piercing test", conceptModification2.getCode());

		// Change request 212: should have only one index concept returned
		maxStructureId = Long.valueOf("5413153");
		changeRequestId = 212;

		parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("maxStructureId", maxStructureId);

		List<ConceptModification> modifiedIndexConcepts = changeRequestIndexSummaryMapper
				.findModifiedIndexConceptElementCodes(parameters);
		assertNotNull(modifiedIndexConcepts);
		assertEquals(1, modifiedIndexConcepts.size());

		ConceptModification conceptModification = modifiedIndexConcepts.get(0);
		assertEquals(maxStructureId, conceptModification.getStructureId());
		assertEquals("AlphabeticIndex", conceptModification.getConceptClassName());
		assertEquals(Long.valueOf("396508"), conceptModification.getElementId());
		assertEquals("crying constantly test", conceptModification.getCode());

		// Change request 184f: should have 4 index concepts returned
		maxStructureId = Long.valueOf("5412820");
		changeRequestId = 184;

		parameters = new HashMap<String, Object>();
		parameters.put("changeRequestId", changeRequestId);
		parameters.put("maxStructureId", maxStructureId);

		List<ConceptModification> modifiedIndexConcepts3 = changeRequestIndexSummaryMapper
				.findModifiedIndexConceptElementCodes(parameters);
		assertNotNull(modifiedIndexConcepts3);
		assertEquals(5, modifiedIndexConcepts3.size());
		ConceptModification conceptModification3 = modifiedIndexConcepts3.get(0);
		assertEquals(maxStructureId, conceptModification3.getStructureId());
		assertEquals("DrugsAndChemicalsIndex", conceptModification3.getConceptClassName());
		assertEquals(Long.valueOf("623173"), conceptModification3.getElementId());
		assertEquals("2,4,5-T", conceptModification3.getCode());
		*/
	}

}
