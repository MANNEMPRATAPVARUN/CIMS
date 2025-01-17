package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.model.TransformationError;

/**
 * Service used for transform CCI Component Definition
 * 
 * @author TYang
 * 
 */
public interface TransformCCIComponentService {

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
	 * Get all CciComponent concepts for the given classification
	 * 
	 * @param context
	 *            ContextAccess the given context
	 * @return <T> Iterator<T>
	 */
	List<CciComponent> getAllCciComponents(ContextAccess context);

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

	ContextProvider getContextProvider();

	/**
	 * Transform the given cciComponent to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param cciComponent
	 *            CciComponent the given cciComponent data.
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformCciComponent(String classification, String version, CciComponent cciComponent, ContextAccess context,
			boolean batchMode);

	/**
	 * Transform the given cciComponent to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param cciComponent
	 *            CciComponent the given cciComponent data.
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformCciComponent(String classification, String version, CciComponent cciComponent, Long runId,
			ContextAccess context, boolean batchMode);

	/**
	 * Transform the given cciComponent concept list.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param cciComponentList
	 *            Iterator<CciComponent> the given cciComponent concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 */
	void transformCciComponents(String classification, String version, List<CciComponent> cciComponentList, Long runId,
			ContextAccess context);
}
