package ca.cihi.cims.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.ReleaseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class PublicationMapperTest {
	@Autowired
	private PublicationMapper publicationMapper;

	@Test
	public void testFindAllReleases() {

		List<PublicationRelease> allReleases = publicationMapper.findAllReleases();
		allReleases.size();
	}

	@Test
	public void testFindVersionCodeNumber() {
		String versionCode = "2016";
		ReleaseType releaseType = ReleaseType.PRELIMINARY_INTERNAL_QA;
		publicationMapper.findVersionCodeNumber(versionCode, releaseType);

	}
}
