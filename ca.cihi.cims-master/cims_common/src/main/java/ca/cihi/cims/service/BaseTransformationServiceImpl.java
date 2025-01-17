package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;

public class BaseTransformationServiceImpl implements BaseTransformationService {

	private static final Log LOGGER = LogFactory.getLog(BaseTransformationServiceImpl.class);

	private TransformationErrorMapper transformErrorMapper;

	/**
	 * Check the run status for the given classification
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	@Override
	@Transactional
	public boolean checkRunStatus(final String fiscalYear, final String classification) {
		LOGGER.debug("enter BaseTransformationServiceImpl.checkRunStatus(fiscalYear, classification)..");
		final Long errorCount = transformErrorMapper.checkRunStatus(fiscalYear, classification);

		return errorCount == null || errorCount == 2 || errorCount == 0;
	}

	/**
	 * Get all transformation errors for the specific run.
	 * 
	 * @param runId
	 *            Long the given run id
	 * @return List<TransformationError>
	 */
	@Override
	@Transactional
	public List<TransformationError> getAllErrors(final Long runId) {
		LOGGER.debug("enter BaseTransformationServiceImpl.getAllErrors(runId)..");
		final List<TransformationError> errorList = transformErrorMapper.getAllErrorsByRunId(runId);

		return errorList == null ? new ArrayList<TransformationError>() : errorList;
	}

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @return List<TransformationError>
	 */
	@Override
	@Transactional
	public List<TransformationError> getAllErrors(final String fiscalYear, final String classification) {
		LOGGER.debug("enter BaseTransformationServiceImpl.getAllErrors(fiscalYear, classification)..");
		List<TransformationError> errorList = transformErrorMapper.getAllErrors(fiscalYear, classification);

		return errorList == null ? new ArrayList<TransformationError>() : errorList;
	}

	/**
	 * Get run id for the transformation process.
	 * 
	 * @return Long
	 */
	@Override
	@Transactional
	public Long getRunId() {
		LOGGER.debug("enter BaseTransformationServiceImpl.getRunId()..");
		return transformErrorMapper.getRunId();
	}

	public TransformationErrorMapper getTransformErrorMapper() {
		return transformErrorMapper;
	}

	/**
	 * Insert a new record in table Transformation_Error.
	 * 
	 * @param error
	 *            TransformationError the given transformation error object.
	 */
	@Override
	@Transactional
	public void insertTransformationError(TransformationError error) {
		LOGGER.debug("enter BaseTransformationServiceImpl.insertTransformationError(error)..");
		transformErrorMapper.insertError(error);
	}

	@Autowired
	public void setTransformErrorMapper(TransformationErrorMapper transformErrorMapper) {
		this.transformErrorMapper = transformErrorMapper;
	}

}
