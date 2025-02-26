package ca.cihi.cims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.ProcessInProgressException;
import ca.cihi.cims.dao.bean.AsotETLLog;
import ca.cihi.cims.dao.mapper.ASOTMapper;

@Service
public class ASOTServiceImpl implements ASOTService {

	private static final Log LOGGER = LogFactory.getLog(ASOTServiceImpl.class);

	private final AtomicBoolean processInProgress = new AtomicBoolean(false);

	@Autowired
	ASOTMapper asotMapper;

	@Override
	public List<String> findVersionYears() {
		return asotMapper.findVersionYears();
	}

	@Override
	@Transactional
	public void generateASOT(String fiscalYear, Long releaseId, String email) {

		if (processInProgress.compareAndSet(false, true)) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Start generating ASOT for " + fiscalYear);
			}

			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("fiscalYear", fiscalYear);
				params.put("releaseId", releaseId);
				params.put("email", email);
				asotMapper.generateASOT(params);

			} finally {
				processInProgress.set(false);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Finish generating ASOT for " + fiscalYear);
			}
		} else {
			throw new ProcessInProgressException("The generation of " + fiscalYear
					+ " ASOT tables is currently in progress. ");
		}

	}

	@Override
	@Transactional
	public void generateASOT(String fiscalYear, String email) {
		Long releaseId = asotMapper.getReleaseId(fiscalYear);
		generateASOT(fiscalYear, releaseId, email);

	}

	@Override
	public AsotETLLog getLatestETLLog(String fiscalYear) {
		Long releaseId = asotMapper.getReleaseId(fiscalYear);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fiscalYear", fiscalYear);
		params.put("releaseId", releaseId);
		return asotMapper.getLatestETLLog(params);
	}

}
