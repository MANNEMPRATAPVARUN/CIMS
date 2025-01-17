package ca.cihi.cims.service.sct;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.cihi.cims.data.mapper.sct.SnomedSCTMapper;
import ca.cihi.cims.model.sct.SCTVersion;

/**
 *
 * @author LZhu
 *
 */

@Service
public class SnomedSCTServiceImpl implements SnomedSCTService {

	private static final Log LOGGER = LogFactory.getLog(SnomedSCTServiceImpl.class);

	@Autowired
	private SnomedSCTMapper snomedSCTMapper;

	public SnomedSCTMapper getSnomedSCTMapper() {
		return snomedSCTMapper;
	}

	public void setSnomedSCTMapper(SnomedSCTMapper snomedSCTMapper) {
		this.snomedSCTMapper = snomedSCTMapper;
	}

	@Override
	public List<SCTVersion> getVersionsByStatus(String statusCode) {
		return snomedSCTMapper.getVersionsByStatus(statusCode);
	}

	@Override
	public String getVersionDescByCode(String code) {
		return snomedSCTMapper.getVersionDescByCode(code);
	}

}
