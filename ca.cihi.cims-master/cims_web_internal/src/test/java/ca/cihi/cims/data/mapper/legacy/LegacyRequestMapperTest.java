package ca.cihi.cims.data.mapper.legacy;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.changerequest.legacy.ChangeNature;
import ca.cihi.cims.model.changerequest.legacy.ChangeType;
import ca.cihi.cims.model.changerequest.legacy.Language;
import ca.cihi.cims.model.changerequest.legacy.RequestStatus;
import ca.cihi.cims.model.changerequest.legacy.Section;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class LegacyRequestMapperTest {
	@Autowired
	private LegacyRequestMapper legacyRequestMapper;

	@Test
	public void testFindVersionCodes() {
		List<String> versionCodes = legacyRequestMapper.findVersionCodes();
		assertTrue("versionCodes is not empty", versionCodes.size() > 0);

	}

	@Test
	public void testFindClassificationTitleCodes() {
		List<String> classifications = legacyRequestMapper.findClassificationTitleCodes();
		assertTrue("classifications is not empty", classifications.size() > 0);

	}

	@Test
	public void testFindLanguages() {
		List<Language> languages = legacyRequestMapper.findLanguages();
		assertTrue("languages is not empty", languages.size() > 0);

	}

	@Test
	public void testFindDispositions() {
		List<RequestStatus> dispositions = legacyRequestMapper.findDispositions();
		assertTrue("dispositions is not empty", dispositions.size() > 0);

	}
	
	@Test
	public void testFindSections() {
		List<Section> sections = legacyRequestMapper.findSections();
		assertTrue("sections is not empty", sections.size() > 0);

	}
	
	@Test
	public void testFindChangeNatures() {
		List<ChangeNature> changeNatures = legacyRequestMapper.findChangeNatures();
		assertTrue("changeNatures is not empty", changeNatures.size() > 0);

	}

	@Test
	public void testFindChangeTypes() {
		List<ChangeType> changeTypes = legacyRequestMapper.findChangeTypes();
		assertTrue("changeTypes is not empty", changeTypes.size() > 0);

	}

}
