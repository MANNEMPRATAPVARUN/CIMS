package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SupplementContentGeneratorFactoryTest {

	@Autowired
	SupplementContentGeneratorFactory factory;
	
	@Test
	public void testCreateGenerator(){
		SupplementContentRequest icdNewCodes = new SupplementContentRequest(SupplementContentRequest.SRC.ICDNEWCODE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator icdNewCodesGenerator = factory.createGenerator(icdNewCodes);
		assertNotNull(icdNewCodesGenerator);
		assertTrue(icdNewCodesGenerator instanceof ICDNewCodeContent);
		
		SupplementContentRequest icdDisabledCodes = new SupplementContentRequest(SupplementContentRequest.SRC.ICDDISABLEDCODE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator icdDisabledCodesGenerator = factory.createGenerator(icdDisabledCodes);
		assertNotNull(icdDisabledCodesGenerator);
		assertTrue(icdDisabledCodesGenerator instanceof ICDDisabledCodeContent);
		
		SupplementContentRequest cciGroup = new SupplementContentRequest(SupplementContentRequest.SRC.CCIGROUP.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciGroupGenerator = factory.createGenerator(cciGroup);
		assertNotNull(cciGroupGenerator);
		assertTrue(cciGroupGenerator instanceof CCIGroupContent);
		
		SupplementContentRequest cciIntervention = new SupplementContentRequest(SupplementContentRequest.SRC.CCIINTERVENTION.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciInterventionGenerator = factory.createGenerator(cciIntervention);
		assertNotNull(cciInterventionGenerator);
		assertTrue(cciInterventionGenerator instanceof CCIInterventionContent);
		
		SupplementContentRequest cciQualifier = new SupplementContentRequest(SupplementContentRequest.SRC.CCIQUALIFIER.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciQualifierGenerator = factory.createGenerator(cciQualifier);
		assertNotNull(cciQualifierGenerator);
		assertTrue(cciQualifierGenerator instanceof CCIQualifierContent);
		
		SupplementContentRequest cciNewCode = new SupplementContentRequest(SupplementContentRequest.SRC.CCINEWCODE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciNewCodeGenerator = factory.createGenerator(cciNewCode);
		assertNotNull(cciNewCodeGenerator);
		assertTrue(cciNewCodeGenerator instanceof CCINewCodeContent);
		
		SupplementContentRequest cciDisabledCode = new SupplementContentRequest(SupplementContentRequest.SRC.CCIDISABLEDCODE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciDisabledCodeGenerator = factory.createGenerator(cciDisabledCode);
		assertNotNull(cciDisabledCodeGenerator);
		assertTrue(cciDisabledCodeGenerator instanceof CCIDisabledCodeContent);
		
		SupplementContentRequest cciRubricFinder = new SupplementContentRequest(SupplementContentRequest.SRC.CCIRUBRICFINDER.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciRubricFinderGenerator = factory.createGenerator(cciRubricFinder);
		assertNotNull(cciRubricFinderGenerator);
		assertTrue(cciRubricFinderGenerator instanceof CCIRubricFinderContent);
		
		SupplementContentRequest cciRubricFinder8 = new SupplementContentRequest(SupplementContentRequest.SRC.CCIRUBRICFINDER8.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciRubricFinderGenerator8 = factory.createGenerator(cciRubricFinder8);
		assertNotNull(cciRubricFinderGenerator8);
		assertTrue(cciRubricFinderGenerator8 instanceof CCIRubricFinder8Content);
		
		SupplementContentRequest cciReferenceValue = new SupplementContentRequest(SupplementContentRequest.SRC.CCIREFVALUE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciReferenceValueGenerator = factory.createGenerator(cciReferenceValue);
		assertNotNull(cciReferenceValueGenerator);
		assertTrue(cciReferenceValueGenerator instanceof CCIReferenceValueContent);
		
		SupplementContentRequest cciGenericAttribute = new SupplementContentRequest(SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciGenericAttributeGenerator = factory.createGenerator(cciGenericAttribute);
		assertNotNull(cciGenericAttributeGenerator);
		assertTrue(cciGenericAttributeGenerator instanceof CCIGenericAttributeContent);
		
		SupplementContentRequest cciAgentATCCode = new SupplementContentRequest(SupplementContentRequest.SRC.CCIAGENTATC.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciAgentATCCodeGenerator = factory.createGenerator(cciAgentATCCode);
		assertNotNull(cciAgentATCCodeGenerator);
		assertTrue(cciAgentATCCodeGenerator instanceof AgentATCCodeContent);
		
		SupplementContentRequest cciNewMandatoryRef = new SupplementContentRequest(SupplementContentRequest.SRC.CCINEWREFVALUE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciNewMandatoryRefGenerator = factory.createGenerator(cciNewMandatoryRef);
		assertNotNull(cciNewMandatoryRefGenerator);
		assertTrue(cciNewMandatoryRefGenerator instanceof CCINewMandatoryReferenceCodesContent);
		
		SupplementContentRequest cciDisabledMandatoryRef = new SupplementContentRequest(SupplementContentRequest.SRC.CCIDISABLEDREFVALUE.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 2l, 1l);
		SupplementContentGenerator cciDisabledMandatoryRefGenerator = factory.createGenerator(cciDisabledMandatoryRef);
		assertNotNull(cciDisabledMandatoryRefGenerator);
		assertTrue(cciDisabledMandatoryRefGenerator instanceof CCIDisabledMandatoryReferenceCodesContent);
	}
}
