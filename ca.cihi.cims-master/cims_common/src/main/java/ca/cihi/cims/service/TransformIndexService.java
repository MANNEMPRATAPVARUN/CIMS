package ca.cihi.cims.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.IndexXmlGenerator;

/**
 * A service class for index transformation.
 * 
 * @author wxing
 * 
 */
public interface TransformIndexService {

	/**
	 * Check the run status for the given index book
	 * 
	 * @param fiscalYear
	 *            String the given fiscal year
	 * @param classification
	 *            String the given classification
	 * @param bookIndexType
	 *            String the given bookIndexType
	 * @param language
	 *            String the given language
	 * @return boolean
	 */
	boolean checkRunStatus(String fiscalYear, String classification, String bookIndexType, String language);

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
	List<TransformationError> getAllErrors(String fiscalYear, String classification, String bookIndexType,
			String language);

	/**
	 * Get the BookIndex object with the given info
	 * 
	 * @param context
	 *            ContextAccess the given context
	 * @param bookIndexType
	 *            String the given book type
	 * @param language
	 *            String the given language
	 * @return BookIndex
	 */
	BookIndex getBookIndex(ContextAccess context, String bookIndexType, String language);

	ContextProvider getContextProvider();

	/**
	 * Get all descendant indices for the given BookIndex or LetterIndex
	 * 
	 * @param classification
	 *            String the given classification
	 * @param parentIndex
	 *            Index the given BookIndex or LetterIndex
	 * @param bookIndexType
	 *            String the given book index type
	 * @param context
	 *            ContextAccess the given context
	 * @return Iterator<? extends Index>
	 */
	Iterator<? extends Index> getIndexTermDescendants(String classification, Index parentIndex, String bookIndexType,
			ContextAccess context);

	/**
	 * Transform the given index book.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param bookIndex
	 *            BookIndex the given book index object
	 * @param language
	 *            String the given language
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 */
	void transformIndexBook(String classification, String version, BookIndex bookIndex, String language, Long runId,
			ContextAccess context);

	/**
	 * Transform the given index concept data to presentionHtml.
	 * 
	 * @param tabularConcept
	 *            TabularConcept the given tabular data.
	 * @param language
	 *            String the given language
	 * @param contextTransaction
	 *            ContextTransaction
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformIndexConcept(Index indexConcept, String language, ContextAccess context, boolean batchMode);

	/**
	 * Transform the given index data to presentionHtml.
	 * 
	 * @param classification
	 *            String the given classification.
	 * @param version
	 *            String the given version.
	 * @param indexConcept
	 *            Index the given index data.
	 * @param runId
	 *            Long the given run id for the transformation process.
	 * @param language
	 *            String the language in which the concept presents
	 * @param context
	 *            ContextAccess
	 * @param indexXmlGenerator
	 *            IndexXmlGenerator the given index xml generator
	 * @param batchMode
	 *            boolean the flag to state if it is a batch process
	 */
	void transformIndexConcept(String classification, String version, Index indexConcept, Long runId, String language,
			ContextAccess context, IndexXmlGenerator indexXmlGenerator, boolean batchMode);

	/**
	 * Transform the given index concept list.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param bookIndexType
	 *            String the given book index type
	 * @param language
	 *            String the given language
	 * @param indexList
	 *            Collection<Index> the given index concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 * @param indexXmlGenerator
	 *            IndexXmlGenerator the given index xml generator
	 */
	void transformIndexData(String classification, String version, String bookIndexType, String language,
			Collection<Index> indexList, Long runId, ContextAccess context, IndexXmlGenerator indexXmlGenerator);

	/**
	 * Transform the given index concept list.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given version
	 * @param bookIndexType
	 *            String the given book index type
	 * @param language
	 *            String the given language
	 * @param indexList
	 *            Iterator<? extends Index> the given index concept list
	 * @param runId
	 *            Long the run id
	 * @param context
	 *            ContextAccess the given context
	 * @param indexXmlGenerator
	 *            IndexXmlGenerator the given index xml generator
	 */
	void transformIndexData(String classification, String version, String bookIndexType, String language,
			Iterator<? extends Index> indexList, Long runId, ContextAccess context, IndexXmlGenerator indexXmlGenerator);

}