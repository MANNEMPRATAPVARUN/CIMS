package ca.cihi.cims.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.dao.bean.AsotETLLog;
import ca.cihi.cims.dao.mapper.ASOTMapper;

public class ASOTServiceTest {

	private ASOTServiceImpl asotServiceImpl;

	@Mock
	ASOTMapper asotMapper;

	private AsotETLLog mockAsotETLLog() {
		AsotETLLog log = new AsotETLLog();
		log.setAsotETLLogId(1l);
		log.setAsotETLLogStatusCode("E");
		log.setAsotETLLogTypeCode("CCI");
		log.setPublicationReleaseId(null);
		log.setStartDate("2015/05/15 11:14:12");
		log.setAsotETLLog("This is test.");
		return log;
	}

	private List<String> mockVersionYears() {
		List<String> versionYears = new ArrayList<String>();
		versionYears.add("2015");
		versionYears.add("2016");
		versionYears.add("2017");
		return versionYears;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		asotServiceImpl = new ASOTServiceImpl();
		asotServiceImpl.asotMapper = asotMapper;
	}

	@Test
	public void testFindVersionYears() {
		when(asotMapper.findVersionYears()).thenReturn(mockVersionYears());
		assertEquals(3, asotServiceImpl.findVersionYears().size());
	}

	@Test
	public void testGenerateASOTWithoutReleaseId() {
		when(asotMapper.getReleaseId("2015")).thenReturn(123l);
		asotServiceImpl.generateASOT("2015", "tyang@cihi.ca");
		verify(asotMapper, times(1)).generateASOT(anyMap());
	}

	@Test
	public void testGenerateASOTWithReleaseId() {
		asotServiceImpl.generateASOT("2015", 123l, "tyang@cihi.ca");
		verify(asotMapper, times(1)).generateASOT(anyMap());
	}

	@Test
	public void testGetAsotETLLog() {
		when(asotMapper.getReleaseId("2015")).thenReturn(123l);
		when(asotMapper.getLatestETLLog(anyMap())).thenReturn(mockAsotETLLog());
		AsotETLLog log = asotServiceImpl.getLatestETLLog("2015");
		assertNotNull(log);
		assertEquals("This is test.", log.getAsotETLLog());
		assertEquals(new Long(1l), log.getAsotETLLogId());
		assertNull(log.getPublicationReleaseId());
		assertEquals("E", log.getAsotETLLogStatusCode());
		assertEquals("CCI", log.getAsotETLLogTypeCode());
		assertEquals("2015/05/15 11:14:12", log.getStartDate());

	}

}
