package ca.cihi.cims.service;

import java.util.Collection;
import java.util.Iterator;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.transformation.XmlGenerator;

/**
 * A service class for transformation.
 * 
 * @author wxing
 * 
 */
public interface TransformationService {

	/**
	 * Get all concepts for the given classification
	 * 
	 * @param classification
	 *            String the given classification
	 * @param context
	 *            ContextAccess the given context
	 * @return <T> Iterator<T>
	 */
	Iterator<? extends TabularConcept> getAllConcepts(String classification, ContextAccess context);

	ContextProvider getContextProvider();

	/**
	 * Transform the given concept data to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification.
	 * @param version
	 *            String the given version.
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param runId
	 *            Long the given run id for the transformation process.
	 * @param languageList
	 *            Collection<String> the languages in which the concept presents
	 * @param xmlGenerator
	 *            XmlGenerator the xmlGenerator used to present the concept as an xml string
	 * @param context
	 *            ContextTransaction
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformConcept(final String classification, final String version, final TabularConcept tabularConcept,
			final Long runId, final Collection<String> languageList, final XmlGenerator xmlGenerator,
			final ContextAccess context, final boolean batchMode);

	/**
	 * Transform the given concept data to presentionHtml.
	 * 
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param contextTransaction
	 *            ContextTransaction
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformConcept(TabularConcept tabularConcept, ContextAccess context, boolean batchMode);

	/**
	 * Transform the given tabular concept list.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version.
	 * @param tabularConceptList
	 *            Iterator<? extends TabularConcept> the given tabular concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 */
	void transformTabularData(String classification, String version,
			Iterator<? extends TabularConcept> tabularConceptList, Long runId, ContextAccess context);

}