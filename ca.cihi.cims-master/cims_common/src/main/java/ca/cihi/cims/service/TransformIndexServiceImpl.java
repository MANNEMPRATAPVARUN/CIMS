package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.CIMSException;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.index.CciIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexDrugsAndChemicals;
import ca.cihi.cims.content.icd.index.IcdIndexExternalInjury;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.dao.mapper.TransformationErrorMapper;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.IndexXmlGenerator;
import ca.cihi.cims.transformation.XmlGeneratorHelper;
import ca.cihi.cims.transformation.XslTransformer;

/**
 * A service class for index transformation.
 *
 * @author wxing
 */
public class TransformIndexServiceImpl implements TransformIndexService {

	// KEYS
	public static final String CCI_CODE = "CCICODE";

	private static final String ELEMENT_ID = "elementId";
	private static final String END_TRANSFORM = "END INDEX TRANSFORMATION";
	private static final Log LOGGER = LogFactory.getLog(TransformIndexServiceImpl.class);
	private static final String PARENT = "parent";
	private static final String START_TRANSFORM = "START INDEX TRANSFORMATION";

	private static XmlGeneratorHelper xmlGeneratorHelper = new XmlGeneratorHelper();
	// ------------------------------------------------------------------------------
	@Autowired
	private BaseTransformationService baseTransformService;
	@Autowired
	private ConceptService conceptService;
	private ContextProvider contextProvider;
	private String dtdFile;
	private TransformationErrorMapper transformErrorMapper;
	private TransformIndexRefServiceImpl transformIndexRefService;
	private XslTransformer xslTransformer;

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
	@Override
	public boolean checkRunStatus(final String fiscalYear, final String classification, final String bookIndexType,
			final String language) {
		final Long errorCount = transformErrorMapper.checkIndexRunStatus(fiscalYear, classification, bookIndexType,
				language);

		return (errorCount == null) || (errorCount == 2) || (errorCount == 0);
	}

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
	@Override
	@Transactional
	public List<TransformationError> getAllErrors(final String fiscalYear, final String classification,
			final String bookIndexType, final String language) {
		List<TransformationError> errorList = transformErrorMapper.getAllIndexErrors(fiscalYear, classification,
				bookIndexType, language);

		return errorList == null ? new ArrayList<TransformationError>() : errorList;
	}

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
	@Override
	public BookIndex getBookIndex(final ContextAccess context, final String bookIndexType, final String language) {
		BookIndex bookIndex = null;

		final Ref<BookIndex> bIndex = ref(BookIndex.class);

		// This will return two BookIndex: one for English and one for French
		final Iterator<BookIndex> bIterator = context.find(bIndex, bIndex.eq("code", bookIndexType));
		while (bIterator.hasNext()) {
			final BookIndex aBookIndex = bIterator.next();

			if (language.equalsIgnoreCase(aBookIndex.getLanguage())) {
				bookIndex = aBookIndex;
				break;
			}
		}

		return bookIndex;
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public String getDtdFile() {
		return dtdFile;
	}

	/**
	 * Get all descendant indices for the given LetterIndex or IcdIndexNeoplasm
	 *
	 * @param classification
	 *            String the given classification
	 * @param parentIndex
	 *            Index the given LetterIndex or IcdIndexNeoplasm
	 * @param bookIndexType
	 *            String the given book index type
	 * @param context
	 *            ContextAccess the given context
	 * @return Iterator<Index>
	 */
	@Override
	public Iterator<? extends Index> getIndexTermDescendants(final String classification, final Index parentIndex,
			final String bookIndexType, final ContextAccess context) {
		Iterator<? extends Index> descendants;

		if (CIMSConstants.CCI.equalsIgnoreCase(classification)
				&& IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL.equalsIgnoreCase(bookIndexType)) {
			final Ref<LetterIndex> parent = ref(LetterIndex.class);
			final Ref<CciIndexAlphabetical> descendant = ref(CciIndexAlphabetical.class);

			descendants = context.find(descendant, parent.eq(ELEMENT_ID, parentIndex.getElementId()),
					descendant.linkTrans(PARENT, parent));
		} else {
			if (IndexXmlGenerator.BOOK_INDEX_TYPE_DRUGS.equalsIgnoreCase(bookIndexType)) {
				final Ref<LetterIndex> parent = ref(LetterIndex.class);
				final Ref<IcdIndexDrugsAndChemicals> descendant = ref(IcdIndexDrugsAndChemicals.class);
				descendants = context.find(descendant, parent.eq(ELEMENT_ID, parentIndex.getElementId()),
						descendant.linkTrans(PARENT, parent));
			} else if (IndexXmlGenerator.BOOK_INDEX_TYPE_EXTERNAL_CAUSES.equalsIgnoreCase(bookIndexType)) {
				final Ref<LetterIndex> parent = ref(LetterIndex.class);
				final Ref<IcdIndexExternalInjury> descendant = ref(IcdIndexExternalInjury.class);
				descendants = context.find(descendant, parent.eq(ELEMENT_ID, parentIndex.getElementId()),
						descendant.linkTrans(PARENT, parent));
			} else if (IndexXmlGenerator.BOOK_INDEX_TYPE_NEOPLASM.equalsIgnoreCase(bookIndexType)) {
				final Ref<IcdIndexNeoplasm> parent = ref(IcdIndexNeoplasm.class);
				final Ref<IcdIndexNeoplasm> descendant = ref(IcdIndexNeoplasm.class);
				descendants = context.find(descendant, parent.eq(ELEMENT_ID, parentIndex.getElementId()),
						descendant.linkTrans(PARENT, parent));
			} else {
				final Ref<LetterIndex> parent = ref(LetterIndex.class);
				final Ref<IcdIndexAlphabetical> descendant = ref(IcdIndexAlphabetical.class);
				descendants = context.find(descendant, parent.eq(ELEMENT_ID, parentIndex.getElementId()),
						descendant.linkTrans(PARENT, parent));
			}
		}

		return descendants;
	}

	public TransformationErrorMapper getTransformErrorMapper() {
		return transformErrorMapper;
	}

	public TransformIndexRefServiceImpl getTransformIndexRefService() {
		return transformIndexRefService;
	}

	// ------------------------------------------------------------------------------

	public XslTransformer getXslTransformer() {
		return xslTransformer;
	}

	private void savePresenationHtml(final String classification, final String htmlString, final Index indexConcept,
			final String presentationType, final String language, final ContextAccess context, final boolean batchMode)
					throws CIMSException {

		if (htmlString != null) {
			if (XmlGeneratorHelper.SHORT_PRESENTATION.equalsIgnoreCase(presentationType)) {
				indexConcept.setShortPresentationHtml(language, htmlString);
				context.persist();
				LOGGER.debug(">>>> set shortPresentation for " + indexConcept.getDescription() + ": length="
						+ htmlString.length());

				if (!batchMode) {
					if (!conceptService.reBaseChangedFromVersionId(indexConcept.getElementId(),
							context.getContextId().getContextId(),
							conceptService.getClassId(classification, "HTMLPropertyVersion", "ShortPresentation"),
							language)) {
						LOGGER.error("reBaseChangedFromVersionId failed after setting short presentation for index "
								+ indexConcept.getDescription());
						throw new CIMSException(
								"reBaseChangedFromVersionId failed after setting short presentation for index "
										+ indexConcept.getDescription());
					}
				}
			} else {
				indexConcept.setPresentationHtml(language, htmlString);
				context.persist();
				LOGGER.debug(">>>> set long Presentation for " + indexConcept.getDescription() + ": length="
						+ htmlString.length());
				if (!batchMode) {
					if (!conceptService.reBaseChangedFromVersionId(indexConcept.getElementId(),
							context.getContextId().getContextId(),
							conceptService.getClassId(classification, "HTMLPropertyVersion", "LongPresentation"),
							language)) {
						LOGGER.error("reBaseChangedFromVersionId failed after setting long presentation for index "
								+ indexConcept.getDescription());
						throw new CIMSException(
								"reBaseChangedFromVersionId failed after setting long presentation for index "
										+ indexConcept.getDescription());
					}
				}
			}
		}
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setDtdFile(String dtdFile) {
		this.dtdFile = dtdFile;
	}

	@Autowired
	public void setTransformErrorMapper(TransformationErrorMapper transformErrorMapper) {
		this.transformErrorMapper = transformErrorMapper;
	}

	@Autowired
	public void setTransformIndexRefService(TransformIndexRefServiceImpl transformIndexRefService) {
		this.transformIndexRefService = transformIndexRefService;
	}

	public void setXslTransformer(XslTransformer xslTransformer) {
		this.xslTransformer = xslTransformer;
	}

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
	@Override
	public void transformIndexBook(final String classification, final String version, final BookIndex bookIndex,
			final String language, final Long runId, final ContextAccess context) {
		LOGGER.debug("**************Start tranforming index Data! ****************");
		final String bookIndexType = bookIndex.getCode(language);
		// Add the start message for this transformation process
		final TransformationError startMessage = new TransformationError(classification, version, bookIndexType,
				language, START_TRANSFORM, "");
		startMessage.setRunId(runId);
		baseTransformService.insertTransformationError(startMessage);
		try {
			final IndexXmlGenerator indexXmlGenerator = new IndexXmlGenerator();
			// Transform BookIndex
			LOGGER.info("Just for log info: transform BookIndex for " + bookIndex.getDescription());
			transformIndexConcept(classification, version, bookIndex, runId, language, context, indexXmlGenerator,
					true);
			// Transform the letter level and Index Term levels
			if (IndexXmlGenerator.BOOK_INDEX_TYPE_NEOPLASM.equalsIgnoreCase(bookIndexType)) {
				// Get the lead term at the letter level
				final Collection<Index> letterLevelIndices = bookIndex.getChildren();

				for (Index index : letterLevelIndices) {
					LOGGER.info("Just for log info: transform letter level Index for " + index.getDescription());
					final TransformationError indexLogger = new TransformationError(classification, version,
							bookIndexType, language, "transform letter " + index.getDescription(), "");
					indexLogger.setRunId(runId);
					baseTransformService.insertTransformationError(indexLogger);
					transformIndexConcept(classification, version, index, runId, language, context, indexXmlGenerator,
							true);
					// Get all descends of the lead term
					final Iterator<? extends Index> indexTermList = getIndexTermDescendants(classification, index,
							bookIndexType, context);
					transformIndexData(classification, version, bookIndexType, language, indexTermList, runId, context,
							indexXmlGenerator);
				}
			} else {
				final Collection<Index> letterIndexList = bookIndex.getSortedChildren();

				for (Index index : letterIndexList) {
					final LetterIndex letterIndex = (LetterIndex) index;

					LOGGER.info("Just for log info: transform letterIndex for " + letterIndex.getDescription());
					final TransformationError letterLogger = new TransformationError(classification, version,
							bookIndexType, language, "transform letter " + letterIndex.getDescription(), "");
					letterLogger.setRunId(runId);
					baseTransformService.insertTransformationError(letterLogger);
					transformIndexConcept(classification, version, letterIndex, runId, language, context,
							indexXmlGenerator, true);

					final Iterator<? extends Index> indexTermList = getIndexTermDescendants(classification, letterIndex,
							bookIndexType, context);
					transformIndexData(classification, version, bookIndexType, language, indexTermList, runId, context,
							indexXmlGenerator);
				}
			}
		} catch (Exception exception) {
			// Add the exception message for this transformation process
			final TransformationError error = new TransformationError(classification, version, bookIndexType, language,
					exception.getMessage(), "");
			error.setRunId(runId);
			baseTransformService.insertTransformationError(error);
			LOGGER.error("Exception: ", exception);
		} finally {
			// Add the end message for this transformation process
			final TransformationError endMessage = new TransformationError(classification, version, bookIndexType,
					language, END_TRANSFORM, "");
			endMessage.setRunId(runId);
			baseTransformService.insertTransformationError(endMessage);
			LOGGER.debug("********************* Finish transforming Index data **************************");
		}
	}

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
	@Override
	public void transformIndexConcept(final Index indexConcept, final String language, final ContextAccess context,
			final boolean batchMode) throws CIMSException {
		final Long runId = baseTransformService.getRunId();
		final String classification = context.getContextId().getBaseClassification();
		final String versionCode = context.getContextId().getVersionCode();
		final IndexXmlGenerator xmlGenerator = new IndexXmlGenerator();
		transformIndexConcept(classification, versionCode, indexConcept, runId, language, context, xmlGenerator,
				batchMode);
	}

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
	@Override
	public void transformIndexConcept(final String classification, final String version, final Index indexConcept,
			final Long runId, final String language, final ContextAccess context,
			final IndexXmlGenerator indexXmlGenerator, final boolean batchMode) throws CIMSException {

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		LOGGER.info("Just for log info: transform index term for " + indexConcept.getDescription());
		try {
			transformIndexConceptInternal(classification, version, indexConcept, language, context, indexXmlGenerator,
					batchMode, errors);
		} catch (Exception exception) {
			LOGGER.error("Exception: ", exception);
			// Add the exception message for this transformation process
			TransformationError error = new TransformationError(classification, version,
					Long.toString(indexConcept.getElementId()), language, exception.getMessage(), "");
			errors.add(error);
		}
		if (!errors.isEmpty()) {
			for (TransformationError error : errors) {
				error.setClassification(classification);
				error.setVersion(version);
				error.setRunId(runId);
			}
			if (batchMode) {
				for (TransformationError error : errors) {
					baseTransformService.insertTransformationError(error);
				}
			} else {
				StringBuilder b = new StringBuilder();
				for (TransformationError error : errors) {
					b.append(error.getErrorMessage()).append("\n");
				}
				throw new CIMSException("Error transforming: " + b);
			}
		}
	}

	private void transformIndexConceptInternal(final String classification, final String version,
			final Index indexConcept, final String language, final ContextAccess context,
			final IndexXmlGenerator indexXmlGenerator, final boolean batchMode,
			final List<TransformationError> errors) {
		// Get index reference xml of the given index
		String xmlString = indexConcept.getIndexRefDefinition(language);
		LOGGER.info("indexRefDefinition for index " + indexConcept.getDescription() + ":" + xmlString);
		if (StringUtils.isEmpty(xmlString)) {
			String errorMessage = "XML for index " + indexConcept.getDescription() + " is required";
			// Add the exception message for this transformation process
			TransformationError error = new TransformationError(classification, version,
					Long.toString(indexConcept.getElementId()), language, errorMessage, "");
			errors.add(error);
			throw new CIMSException(errorMessage);
		}

		// Transform the index reference xml to html and save it to
		// shortPresentationHtml
		String indexRefHtml = transformIndexRefService.transformShortPresentation(xmlString, errors);
		/*
		 * Decode &amp;#134; to &#134; Decode &amp;diams; to &diams;
		 */
		indexRefHtml = xmlGeneratorHelper.decodeHtmlString(indexRefHtml, errors, indexConcept.getDescription(),
				classification, language, context);
		savePresenationHtml(classification, indexRefHtml, indexConcept, XmlGeneratorHelper.SHORT_PRESENTATION, language,
				context, batchMode);

		// Transform the long presentation and save it to LongPresentationHtml
		String htmlString = getXslTransformer().transform(indexXmlGenerator.generateXml(classification, version,
				indexConcept, errors, xmlString, language, context), errors);
		/*
		 * Decode &amp;#134; to &#134; Decode &amp;diams; to &diams;
		 */
		htmlString = xmlGeneratorHelper.decodeHtmlString(htmlString, errors, indexConcept.getDescription(),
				classification, language, context);
		savePresenationHtml(classification, htmlString, indexConcept, XmlGeneratorHelper.LONG_PRESENTATION, language,
				context, batchMode);
	}

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
	@Override
	public void transformIndexData(final String classification, final String version, final String bookIndexType,
			final String language, final Collection<Index> indexList, final Long runId, final ContextAccess context,
			final IndexXmlGenerator indexXmlGenerator) {

		for (Index index : indexList) {
			// skip the disabled concepts
			if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(index.getStatus())) {
				transformIndexConcept(classification, version, index, runId, language, context, indexXmlGenerator,
						true);
			}
		}
	}

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
	@Override
	public void transformIndexData(final String classification, final String version, final String bookIndexType,
			final String language, final Iterator<? extends Index> indexList, final Long runId,
			final ContextAccess context, final IndexXmlGenerator indexXmlGenerator) {

		while (indexList.hasNext()) {
			final Index index = indexList.next();
			// skip the disabled concepts
			if (XmlGeneratorHelper.STATUS_ACTIVE.equalsIgnoreCase(index.getStatus())) {
				transformIndexConcept(classification, version, index, runId, language, context, indexXmlGenerator,
						true);
			}
		}
	}

}