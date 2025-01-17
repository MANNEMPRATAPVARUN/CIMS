package ca.cihi.cims.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.TransformationError;

public interface TransformationErrorMapper {

	/**
	 * Check the run status for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @param conceptCode
	 *            String the given concept code
	 */
	Long checkCCIComponentRunStatus(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification, @Param("conceptCode") String conceptCode);

	/**
	 * Check the run status for the given index book.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @param bookIndexType
	 *            String the given bookIndex type
	 * @param language
	 *            String the given language
	 * @return Long
	 */
	Long checkIndexRunStatus(@Param("fiscalYear") String fiscalYear, @Param("classification") String classification,
			@Param("bookIndexType") String bookIndexType, @Param("language") String language);

	/**
	 * Check the run status for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @return Long
	 */
	Long checkRunStatus(@Param("fiscalYear") String fiscalYear, @Param("classification") String classification);

	/**
	 * Check the run status for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @param conceptCode
	 *            String the given concept code
	 */
	Long checkSupplementRunStatus(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification, @Param("conceptCode") String conceptCode);

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @param conceptCode
	 *            String the given concept code
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllCCIComponentErrors(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification, @Param("conceptCode") String conceptCode);

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllErrors(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification);

	/**
	 * Get all transformation errors for the specific run.
	 * 
	 * @param runId
	 *            Long the given run id
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllErrorsByRunId(final Long runId);

	/**
	 * Get the latest transformation errors for the given index book.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @param bookIndexType
	 *            String the given bookIndexType
	 * @param language
	 *            String the given language
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllIndexErrors(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification, @Param("bookIndexType") String bookIndexType,
			@Param("language") String language);

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @param conceptCode
	 *            String the given concept code
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllSupplementErrors(@Param("fiscalYear") String fiscalYear,
			@Param("classification") String classification, @Param("conceptCode") String conceptCode);

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
	void insertError(final TransformationError error);
}
