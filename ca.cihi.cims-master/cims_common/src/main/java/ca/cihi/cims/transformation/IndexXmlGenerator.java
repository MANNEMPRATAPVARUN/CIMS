package ca.cihi.cims.transformation;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.IndexTerm;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.util.ClassPathResolver;

public class IndexXmlGenerator {

	private static final Log LOGGER = LogFactory.getLog(IndexXmlGenerator.class);

	// Index data
	public static final int INDEX_TERM_DESC_INDEX = 4;
	public static final String INDEX = "index";
	public static final String BOOK_INDEX_TYPE = "BOOK_INDEX_TYPE";
	public static final String ELEMENT_ID = "ELEMENT_ID";
	public static final String INDEX_TYPE = "INDEX_TYPE";
	public static final String LEVEL_NUM = "LEVEL_NUM";
	public static final String INDEX_TERM_DESC = "INDEX_TERM_DESC";
	public static final String SEE_ALSO_FLAG = "SEE_ALSO_FLAG";
	public static final String SITE_INDICATOR = "SITE_INDICATOR";
	public static final String REFERENCE_LIST = "REFERENCE_LIST";
	public static final String CAT_REF_LIST = "CATEGORY_REFERENCE_LIST";
	public static final String CAT_REFERENCE = "CATEGORY_REFERENCE";
	public static final String PAIRED_FLAG = "PAIRED_FLAG";
	public static final String SORT_STRING = "SORT_STRING";
	public static final String CAT_REF_MAIN_CODE_P = "MAIN_CODE_PRESENTATION";
	public static final String CAT_REF_MAIN_CC_ID = "MAIN_CONTAINER_CONCEPT_ID";
	public static final String CAT_REF_MAIN_CODE = "MAIN_CODE";
	public static final String CAT_REF_MAIN_DA = "MAIN_DAGGER_ASTERISK";
	public static final String CAT_REF_PAIRED_CODE_P = "PAIRED_CODE_PRESENTATION";
	public static final String CAT_REF_PAIRED_CC_ID = "PAIRED_CONTAINER_CONCEPT_ID";
	public static final String CAT_REF_PAIRED_CODE = "PAIRED_CODE";
	public static final String CAT_REF_PAIRED_DA = "PAIRED_DAGGER_ASTERISK";

	public static final String INDEX_REF_LIST = "INDEX_REF_LIST";
	public static final String INDEX_REF = "INDEX_REF";
	public static final String REF_DESC = "REF_DESC";
	public static final String REFERENCE_LINK_DESC = "REFERENCE_LINK_DESC";
	public static final String CONTAINER_INDEX_ID = "CONTAINER_INDEX_ID";
	public static final String NOTE_DESC = "NOTE_DESC";

	public static final String INDEX_TYPE_BOOK = "BOOK_INDEX";
	public static final String INDEX_TYPE_LETTER = "LETTER_INDEX";
	public static final String INDEX_TYPE_INDEX_TERM = "INDEX_TERM";
	public static final String BOOK_INDEX_TYPE_ALPHABETICAL = "A";
	public static final String BOOK_INDEX_TYPE_DRUGS = "D";
	public static final String BOOK_INDEX_TYPE_EXTERNAL_CAUSES = "E";
	public static final String BOOK_INDEX_TYPE_NEOPLASM = "N";
	public static final String YES_FLAG = "Y";
	public static final String NO_FLAG = "X";

	public static final String TABULAR_REF = "TABULAR_REF";
	public static final String TYPE = "type";
	public static final String TF_CONTAINER_CONCEPT_ID = "TF_CONTAINER_CONCEPT_ID";
	public static final String CODE_PRESENTATION = "CODE_PRESENTATION";

	public static final String DRUGS_DETAIL = "DRUGS_DETAIL";
	public static final String CHAPTER_XIX = "CHAPTER_XIX";
	public static final String ACCIDENTAL = "ACCIDENTAL";
	public static final String INT_SELF_HARM = "INT_SELF_HARM";
	public static final String UNDE_INTENT = "UNDE_INTENT";
	public static final String AETU = "AETU";

	public static final String NEOPLASM_DETAIL = "NEOPLASM_DETAIL";
	public static final String MALIGNANT_PRIMARY = "MALIGNANT_PRIMARY";
	public static final String MALIGNANT_SECONDARY = "MALIGNANT_SECONDARY";
	public static final String IN_SITU = "IN_SITU";
	public static final String BENIGN = "BENIGN";
	public static final String UU_BEHAVIOUR = "UU_BEHAVIOUR";

	public static final String SORT_STRING_A = "aaa-sort-string-aaa###";
	public static final String SORT_STRING_B = "aaa-sort-string-bbb###";
	public static final String SORT_STRING_C = "aaa-sort-string-ccc###";
	public static final String SORT_STRING_Z = "aaa-sort-string-zzz###";
	public static final String DAGGER = "&#134;";
	public static final String DIAMOND = "&diams;";
	public static final String DOLLAR_SIGN = "\\$";

	static {
		// Set system property jdk.xml.entityExpansionLimit to 0
		System.setProperty("jdk.xml.entityExpansionLimit", "0");
	}

	private final XmlGeneratorHelper xgHelper = new XmlGeneratorHelper();

	// Turn on validation
	// private final IcdSearches icdHelper = new IcdSearchesImpl();
	private final SAXBuilder builder = new SAXBuilder(true);

	private final InputSource inSource = new InputSource();

	// private void addCategoryRef(final Element eleCatRefList, final CategoryReference catRef,
	// final String classification, final ContextAccess ctxx) {
	// final TabularConcept mainCategory = catRef.getMainCodeConcept();
	//
	// final Element eleCatRef = new Element(CAT_REFERENCE);
	// eleCatRefList.addContent(eleCatRef);
	//
	// if (CIMSConstants.CCI.equalsIgnoreCase(classification)) {
	// final String code = mainCategory.getCode();
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CODE_P).setText(code));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CC_ID).setText(ctxx.determineContainingIdPath(code)));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CODE).setText(code));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_DA).setText(NO_FLAG));
	// eleCatRef.addContent(new Element(PAIRED_FLAG).setText(NO_FLAG));
	// eleCatRef.addContent(new Element(SORT_STRING).setText(mainCategory.getCode()));
	// } else {
	// addIcdCatRef(eleCatRef, catRef, ctxx);
	// }
	// }

	// private void addIcdCatRef(final Element eleCatRef, final CategoryReference catRef, final ContextAccess ctxx) {
	// LOGGER.info("enter addIcdCatRef(...) ");
	// LOGGER.info("get mainCodeConcept for " + catRef.getMainCodeConcept().getCode());
	// final TabularConcept mainCategory = catRef.getMainCodeConcept();
	//
	// LOGGER.info("get pairedCodeConcept for " + catRef.getPairedCodeConcept());
	// final TabularConcept pairedCategory = catRef.getPairedCodeConcept();
	//
	// String mainDaggerAsterisk = "";
	// if (catRef.getMainCodeDaggerAsterisk() != null) {
	// mainDaggerAsterisk = catRef.getMainCodeDaggerAsterisk().getCode();
	// }
	//
	// String pairedDaggerAsterisk = "";
	// if (catRef.getPairedCodeDaggerAsterisk() != null) {
	// pairedDaggerAsterisk = catRef.getPairedCodeDaggerAsterisk().getCode();
	// }
	//
	// String mainDaggerString;
	// if (mainDaggerAsterisk == null) {
	// mainDaggerString = "";
	// } else if ("+".equals(mainDaggerAsterisk)) {
	// mainDaggerString = DAGGER;
	// } else {
	// mainDaggerString = mainDaggerAsterisk;
	// }
	//
	// String pairedDaggerString;
	// if (pairedDaggerAsterisk == null) {
	// pairedDaggerString = "";
	// } else if ("+".equals(pairedDaggerAsterisk)) {
	// pairedDaggerString = DAGGER;
	// } else {
	// pairedDaggerString = pairedDaggerAsterisk;
	// }
	//
	// final String mainCode = mainCategory.getCode();
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CODE_P).setText(createCodePresentation(mainCategory,
	// mainDaggerString)));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CC_ID).setText(ctxx.determineContainingIdPath(mainCode)));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_CODE).setText(mainCode));
	// eleCatRef.addContent(new Element(CAT_REF_MAIN_DA).setText(mainDaggerAsterisk));
	//
	// // Add sorting string for sorting all category reference later on in
	// // XSLT.
	// // multiple CATEGORY_REFERENCE_DESC should be sorted in this code order:
	// // [morphology | dagger | regular | asterisk]
	// final String sortingString = getSortingString(mainCategory, pairedCategory, mainDaggerString);
	// if (pairedCategory == null) {
	// eleCatRef.addContent(new Element(PAIRED_FLAG).setText(NO_FLAG));
	// eleCatRef.addContent(new Element(SORT_STRING).setText(sortingString));
	// } else {
	// final String pairedCode = pairedCategory.getCode();
	// eleCatRef.addContent(new Element(PAIRED_FLAG).setText(YES_FLAG));
	// eleCatRef.addContent(new Element(SORT_STRING).setText(sortingString));
	// eleCatRef.addContent(new Element(CAT_REF_PAIRED_CODE_P).setText(createCodePresentation(pairedCategory,
	// pairedDaggerString)));
	// eleCatRef.addContent(new Element(CAT_REF_PAIRED_CC_ID).setText(ctxx.determineContainingIdPath(pairedCode)));
	// eleCatRef.addContent(new Element(CAT_REF_PAIRED_CODE).setText(pairedCode));
	// eleCatRef.addContent(new Element(CAT_REF_PAIRED_DA).setText(pairedDaggerAsterisk));
	// }
	// }
	//
	// private void addIndexRef(final Element eleIndexRefList, final IndexReference indexRef, final ContextAccess ctxx)
	// {
	// final Element eleIndexRef = new Element(INDEX_REF);
	// eleIndexRefList.addContent(eleIndexRef);
	//
	// eleIndexRef.addContent(new Element(REF_DESC).setText(indexRef.getIndexReferredTo().getDescription()));
	// eleIndexRef.addContent(new Element(REFERENCE_LINK_DESC).setText(indexRef.getReferenceLinkDescription()));
	// eleIndexRef.addContent(new Element(CONTAINER_INDEX_ID).setText(ctxx.determineContainingIdPath(indexRef
	// .getIndexReferredTo().getElementId())));
	// }
	//
	// private void addRefList(final Element parentElem, final IndexTerm indexTerm, final String classification,
	// final String bookIndexType, final ContextAccess ctxx) {
	//
	// // Add the index reference list if there is any
	// final Collection<IndexReference> indexRefList = indexTerm.getIndexReferences();
	// if (!indexRefList.isEmpty()) {
	// final Element eleIndexRefList = new Element(INDEX_REF_LIST);
	// parentElem.addContent(eleIndexRefList);
	//
	// for (IndexReference catRef : indexRefList) {
	// addIndexRef(eleIndexRefList, catRef, ctxx);
	// }
	// }
	//
	// if (BOOK_INDEX_TYPE_ALPHABETICAL.equalsIgnoreCase(bookIndexType)
	// || BOOK_INDEX_TYPE_EXTERNAL_CAUSES.equalsIgnoreCase(bookIndexType)) {
	// final Collection<CategoryReference> categoryRefList = indexTerm.getCategoryReferences();
	//
	// // Add the category reference list if there is any
	// if (!categoryRefList.isEmpty()) {
	// final Element eleCatRefList = new Element(CAT_REF_LIST);
	// parentElem.addContent(eleCatRefList);
	//
	// for (CategoryReference catRef : categoryRefList) {
	// LOGGER.info("addCategoryRef for " + indexTerm.getDescription() + ", catRef size: "
	// + categoryRefList.size());
	// addCategoryRef(eleCatRefList, catRef, classification, ctxx);
	// }
	// }
	// }
	// }

	// private void appendDrugDetail(final String classification, final String version, final String language,
	// final Element parentElem, final IndexTerm indexTerm, final List<TransformationError> errors,
	// final ContextAccess ctxx) {
	// if (indexTerm instanceof IcdIndexDrugsAndChemicals) {
	// final IcdIndexDrugsAndChemicals drugIndex = (IcdIndexDrugsAndChemicals) indexTerm;
	//
	// final Element eleDrugDetail = new Element(DRUGS_DETAIL);
	// parentElem.addContent(eleDrugDetail);
	//
	// appendTabularRef(eleDrugDetail, drugIndex.getPoisoningXIXConcept(), CHAPTER_XIX, ctxx);
	// appendTabularRef(eleDrugDetail, drugIndex.getPoisoningAccidentalConcept(), ACCIDENTAL, ctxx);
	// appendTabularRef(eleDrugDetail, drugIndex.getPoisoningIntentionalConcept(), INT_SELF_HARM, ctxx);
	// appendTabularRef(eleDrugDetail, drugIndex.getPoisoningUndeterminedConcept(), UNDE_INTENT, ctxx);
	// appendTabularRef(eleDrugDetail, drugIndex.getAdverseEffectConcept(), AETU, ctxx);
	// } else {
	// LOGGER.error("Drug index book has a non drug index term : " + indexTerm.getDescription());
	// final TransformationError error = new TransformationError(classification, version, "", language,
	// "Drug index book has a non drug index term : " + indexTerm.getDescription(), "");
	// errors.add(error);
	// }
	// }

	// private void appendNeoplasmDetail(final String classification, final String version, final String language,
	// final Element parentElem, final IndexTerm indexTerm, final List<TransformationError> errors,
	// final ContextAccess ctxx) {
	// if (indexTerm instanceof IcdIndexNeoplasm) {
	// final IcdIndexNeoplasm neoIndex = (IcdIndexNeoplasm) indexTerm;
	//
	// final Element eleNeoDetail = new Element(NEOPLASM_DETAIL);
	// parentElem.addContent(eleNeoDetail);
	//
	// appendTabularRef(eleNeoDetail, neoIndex.getMalignantPrimaryConcept(), MALIGNANT_PRIMARY, ctxx);
	// appendTabularRef(eleNeoDetail, neoIndex.getMalignantSecondaryConcept(), MALIGNANT_SECONDARY, ctxx);
	// appendTabularRef(eleNeoDetail, neoIndex.getInSituConcept(), IN_SITU, ctxx);
	// appendTabularRef(eleNeoDetail, neoIndex.getBenignConcept(), BENIGN, ctxx);
	// appendTabularRef(eleNeoDetail, neoIndex.getUnknownBehaviourConcept(), UU_BEHAVIOUR, ctxx);
	// } else {
	// LOGGER.error("Neoplasm index book has a non neoplasm index term :" + indexTerm.getDescription());
	// final TransformationError error = new TransformationError(classification, version, "", language,
	// "Neoplasm index book has a non neoplasm index term : " + indexTerm.getDescription(), "");
	// errors.add(error);
	// }
	// }

	private void appendNoteString(final String classification, final String version, final String indexDesc,
			final String indexType, final Element parentElem, final String noteXmlString,
			final List<TransformationError> errors) {
		if (noteXmlString != null && !noteXmlString.isEmpty()) {
			final Element eleNoteDesc = new Element(NOTE_DESC);
			parentElem.addContent(eleNoteDesc);
			xgHelper.appendXmlContent(classification, version, indexDesc, indexType, eleNoteDesc, noteXmlString, true,
					errors);
		}
	}

	// private void appendSiteIndicator(final IndexTerm indexTerm, final String classification, final Element
	// parentElem) {
	// if (CIMSConstants.ICD_10_CA.equals(classification)) {
	// final SiteIndicator siteIndicator = indexTerm.getSiteIndicator();
	//
	// String siteIndicatorStr;
	// if (siteIndicator == null) {
	// siteIndicatorStr = "";
	// } else {
	// final String siCode = siteIndicator.getCode();
	// siteIndicatorStr = (siCode == null) ? "" : siCode.equalsIgnoreCase("$") ? DIAMOND : siCode;
	// }
	//
	// parentElem.addContent(new Element(SITE_INDICATOR).setText(siteIndicatorStr));
	// }
	// }

	/*
	 * public void appendTabularRef(final Element parentElem, final IcdTabular tabularRef, final String type, final
	 * ContextAccess ctxx) {
	 * 
	 * final Element elemTabularRef = new Element(TABULAR_REF).setAttribute(TYPE, type);
	 * parentElem.addContent(elemTabularRef); if (tabularRef != null) { elemTabularRef.addContent(new
	 * Element(TF_CONTAINER_CONCEPT_ID).setText(ctxx .determineContainingIdPath(tabularRef.getCode())));
	 * elemTabularRef.addContent(new Element(CODE_PRESENTATION).setText(createCodePresentation(tabularRef,
	 * tabularRef.getDaggerAsterisk()))); } }
	 */

	/*
	 * private String createCodePresentation(final TabularConcept tabularConcept, final String daggerAsteriskString) {
	 * 
	 * final StringBuffer conceptCodePresentation = new StringBuffer();
	 * 
	 * final String code = tabularConcept.getCode(); conceptCodePresentation.append(code);
	 * 
	 * if (daggerAsteriskString != null) { conceptCodePresentation.append(daggerAsteriskString); }
	 * 
	 * return conceptCodePresentation.toString(); }
	 */

	// /**
	// * Create a short Index xml string that doesn't include index description and note string.
	// *
	// * @param classification
	// * String the given classification
	// * @param version
	// * String the given version
	// * @param index
	// * Index the given index
	// * @param errors
	// * List<TransformationError> the given error list
	// * @param dtdFile
	// * String the given dtd file
	// * @param language
	// * String the given language
	// * @param indexType
	// * String the given index type
	// * @param ctxx
	// * ContextAccess the given context
	// * @return String
	// */
	// private String createIndexRefXml(final String classification, final String version, final Index index,
	// final List<TransformationError> errors, final String dtdFile, final String language,
	// final String indexType, final ContextAccess ctxx) {
	//
	// String indexStr = "";
	//
	// final Element root = new Element(INDEX);
	// final DocType type = new DocType(INDEX, dtdFile);
	// final Document doc = new Document(root, type);
	//
	// root.setAttribute(XmlGeneratorHelper.LANGUAGE, language);
	// root.setAttribute(XmlGeneratorHelper.CLASSIFICATION, classification);
	//
	// final Long elementId = index.getElementId();
	//
	// final BookIndex bookIndex = index.getContainingBook();
	// final String bookIndexType = bookIndex.getCode(language);
	// root.addContent(new Element(BOOK_INDEX_TYPE).setText(bookIndexType));
	// root.addContent(new Element(ELEMENT_ID).setText(Long.toString(elementId)));
	// root.addContent(new Element(INDEX_TYPE).setText(indexType));
	//
	// if (INDEX_TYPE_BOOK.equalsIgnoreCase(indexType)) {
	// root.addContent(new Element(LEVEL_NUM).setText(""));
	// root.addContent(new Element(SEE_ALSO_FLAG).setText(NO_FLAG));
	// } else if (INDEX_TYPE_LETTER.equalsIgnoreCase(indexType)) {
	// root.addContent(new Element(LEVEL_NUM).setText(""));
	// root.addContent(new Element(SEE_ALSO_FLAG).setText(NO_FLAG));
	// } else {
	// final IndexTerm indexTerm = (IndexTerm) index;
	// root.addContent(new Element(LEVEL_NUM).setText(indexTerm.getNestingLevel() + ""));
	//
	// final SeeAlso seeAlso = indexTerm.getSeeAlsoFlag();
	// if (seeAlso == null) {
	// root.addContent(new Element(SEE_ALSO_FLAG).setText(NO_FLAG));
	// } else {
	// root.addContent(new Element(SEE_ALSO_FLAG).setText(seeAlso.getCode()));
	// }
	//
	// appendSiteIndicator(indexTerm, classification, root);
	//
	// final Element eleRefList = new Element(REFERENCE_LIST);
	// root.addContent(eleRefList);
	// addRefList(eleRefList, indexTerm, classification, bookIndexType, ctxx);
	//
	// if (BOOK_INDEX_TYPE_DRUGS.equalsIgnoreCase(bookIndexType)) {
	// appendDrugDetail(classification, version, language, root, indexTerm, errors, ctxx);
	// }
	//
	// if (BOOK_INDEX_TYPE_NEOPLASM.equalsIgnoreCase(bookIndexType)) {
	// appendNeoplasmDetail(classification, version, language, root, indexTerm, errors, ctxx);
	// }
	// }
	//
	// indexStr = new XMLOutputter().outputString(doc);
	//
	// return indexStr;
	// }

	private String createIndexStr(final String shortIndexStr, final String classification, final String version,
			final Index index, final List<TransformationError> errors, final String language, final String indexType) {

		String indexStr = "";

		// Get the doc and root element from shortIndexString
		Document document = null;
		try {
			// Set entityResolver
			builder.setEntityResolver(new ClassPathResolver());
			inSource.setCharacterStream(new StringReader(shortIndexStr));

			document = builder.build(inSource);
		} catch (IOException exception) {
			// Error on reading the xml block
			LOGGER.error("\n** reading xml error on " + shortIndexStr);
			LOGGER.error("   " + exception.getMessage());

			errors.add(new TransformationError(classification, version, index.getDescription(), indexType, exception
					.getMessage(), shortIndexStr));
		} catch (JDOMException exception) {
			// Error on parsing the xml block
			LOGGER.error("\n** parsing xml error on " + shortIndexStr);
			LOGGER.error("   " + exception.getMessage());

			errors.add(new TransformationError(classification, version, index.getDescription(), indexType, exception
					.getMessage(), shortIndexStr));
		}

		if (document != null) {
			final Element root = document.getRootElement();

			if (INDEX_TYPE_BOOK.equalsIgnoreCase(indexType)) {
				root.addContent(INDEX_TERM_DESC_INDEX, new Element(INDEX_TERM_DESC).setText(index.getDescription()));

				// Add note
				final String noteXmlString = ((BookIndex) index).getNoteDescription(language);
				appendNoteString(classification, version, index.getDescription(), indexType, root, noteXmlString,
						errors);
			} else if (INDEX_TYPE_LETTER.equalsIgnoreCase(indexType)) {
				root.addContent(INDEX_TERM_DESC_INDEX, new Element(INDEX_TERM_DESC).setText(index.getDescription()));
			} else {
				final IndexTerm indexTerm = (IndexTerm) index;

				root.addContent(INDEX_TERM_DESC_INDEX, new Element(INDEX_TERM_DESC).setText(indexTerm.getDescription()));

				// Add note
				String noteXmlString = indexTerm.getNoteDescription(language);

				if (noteXmlString != null) {
					noteXmlString = noteXmlString.replaceAll(DOLLAR_SIGN, DIAMOND);
				}

				appendNoteString(classification, version, indexTerm.getDescription(), indexType, root, noteXmlString,
						errors);

			}

			indexStr = new XMLOutputter().outputString(document);
		}

		return indexStr;
	}

	// /**
	// * Generate index reference Xml String for the given index concept.
	// *
	// * @param classification
	// * String the given classification
	// * @param version
	// * String the given verion
	// * @param index
	// * Index the given Index object
	// * @param errors
	// * List<TransformationError> the given error list
	// * @param dtdFile
	// * String the given DTD file
	// * @param language
	// * String the given language
	// * @param ctxx
	// * ContextAccess
	// * @return
	// */
	// @Transactional
	// public String generateIndexRefXml(final String classification, final String version, final Index index,
	// final List<TransformationError> errors, final String dtdFile, final String language,
	// final ContextAccess ctxx) {
	//
	// String resultString = "";
	//
	// if (index instanceof BookIndex) {
	// resultString = createIndexRefXml(classification, version, index, errors, dtdFile, language,
	// INDEX_TYPE_BOOK, ctxx);
	// } else if (index instanceof LetterIndex) {
	// resultString = createIndexRefXml(classification, version, index, errors, dtdFile, language,
	// INDEX_TYPE_LETTER, ctxx);
	// } else if (index instanceof IndexTerm) {
	// resultString = createIndexRefXml(classification, version, index, errors, dtdFile, language,
	// INDEX_TYPE_INDEX_TERM, ctxx);
	// } else {
	// LOGGER.error(index.getClass() + " is an invalid index!");
	// final TransformationError error = new TransformationError(classification, version, "", language,
	// index.getDescription() + " has Invalid index type.", "");
	// errors.add(error);
	// }
	//
	// return resultString;
	// }

	/**
	 * Generate Xml String for the given index concept.
	 * 
	 * @param classification
	 *            String the given classification
	 * @param version
	 *            String the given verion
	 * @param index
	 *            Index the given Index object
	 * @param errors
	 *            List<TransformationError> the given error list
	 * @param indexRefXmlString
	 *            String the given indexRefXmlString
	 * @param language
	 *            String the given language
	 * @param ctxx
	 *            ContextAccess
	 * @return
	 */
	@Transactional
	public String generateXml(final String classification, final String version, final Index index,
			final List<TransformationError> errors, final String indexRefXmlString, final String language,
			final ContextAccess ctxx) {

		String resultString = "";

		if (index instanceof BookIndex) {
			resultString = createIndexStr(indexRefXmlString, classification, version, index, errors, language,
					INDEX_TYPE_BOOK);
		} else if (index instanceof LetterIndex) {
			resultString = createIndexStr(indexRefXmlString, classification, version, index, errors, language,
					INDEX_TYPE_LETTER);
		} else if (index instanceof IndexTerm) {
			resultString = createIndexStr(indexRefXmlString, classification, version, index, errors, language,
					INDEX_TYPE_INDEX_TERM);
		} else {
			LOGGER.error(index.getClass() + " is an invalid index!");
			final TransformationError error = new TransformationError(classification, version, "", language,
					index.getDescription() + " has Invalid index type.", "");
			errors.add(error);
		}

		return resultString;
	}

	// private String getSortingString(final TabularConcept mainCategory, final TabularConcept pairedCategory,
	// final String mainDaggerString) {
	//
	// String sortingString;
	//
	// // Add sorting string for sorting all category reference later on in
	// // XSLT.
	// // multiple CATEGORY_REFERENCE_DESC should be sorted in this code order:
	// // [morphology | dagger | regular | asterisk]
	// if (pairedCategory == null) {
	// if (mainDaggerString.equalsIgnoreCase("")) {
	// sortingString = SORT_STRING_C + mainCategory.getCode();
	// } else if (mainDaggerString.equalsIgnoreCase(DAGGER)) {
	// sortingString = SORT_STRING_B + mainCategory.getCode();
	// } else {
	// sortingString = SORT_STRING_Z + mainCategory.getCode();
	// }
	// } else {
	// sortingString = SORT_STRING_A + mainCategory.getCode();
	// }
	//
	// return sortingString;
	// }

}
