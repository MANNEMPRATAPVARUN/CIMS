package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.model.TransformationError;

public interface BaseTransformationService {

	/**
	 * Check the run status for the given classification
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	boolean checkRunStatus(String fiscalYear, String classification);

	/**
	 * Get all transformation errors for the specific run.
	 * 
	 * @param runId
	 *            Long the given run id
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllErrors(Long runId);

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllErrors(String fiscalYear, String classification);

	/**
	 * Get run id for the transformation process.
	 * 
	 * @return
	 */
	Long getRunId();

	/**
	 * Insert a new record in table Transformation_Error.
	 * 
	 * @param error
	 *            TransformationError the given transformation error object.
	 */
	void insertTransformationError(TransformationError error);

}
