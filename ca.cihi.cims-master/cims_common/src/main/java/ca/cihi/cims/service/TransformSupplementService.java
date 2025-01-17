package ca.cihi.cims.service;

import java.util.Iterator;
import java.util.List;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.Supplement;
import ca.cihi.cims.model.TransformationError;

/**
 * A service class for transform Supplement Concepts.
 * 
 * @author wxing
 * 
 */
public interface TransformSupplementService {

	/**
	 * Check the run status for the given classification
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @return boolean
	 */
	boolean checkRunStatus(final String fiscalYear, final String classification);

	/**
	 * Get the latest transformation errors for the given classification.
	 * 
	 * @param fiscalYear
	 *            String the given version
	 * @param classification
	 *            String the given classification
	 * @return List<TransformationError>
	 */
	List<TransformationError> getAllErrors(final String fiscalYear, final String classification);

	/**
	 * Get all Supplement concepts for the given classification
	 * 
	 * @param context
	 *            ContextAccess the given context
	 * @return <T> Iterator<T>
	 */
	Iterator<Supplement> getAllSupplements(ContextAccess context);

	ContextProvider getContextProvider();

	/**
	 * Transform the given supplement to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplement
	 *            Supplement the given supplement data.
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformSupplement(String classification, String version, Supplement supplement, ContextAccess context,
			boolean batchMode);

	/**
	 * Transform the given supplement to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplement
	 *            Supplement the given supplement data.
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformSupplement(String classification, String version, Supplement supplement, Long runId,
			ContextAccess context, boolean batchMode);

	/**
	 * Transform the given supplement concept list.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param supplementList
	 *            Iterator<Supplement> the given supplement concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 */
	void transformSupplements(String classification, String version, Iterator<Supplement> supplementList, Long runId,
			ContextAccess context);

}