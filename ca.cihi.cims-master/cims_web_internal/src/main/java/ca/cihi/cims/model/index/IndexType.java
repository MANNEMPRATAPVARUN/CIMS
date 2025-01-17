package ca.cihi.cims.model.index;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.content.cci.index.CciIndexAlphabetical;
import ca.cihi.cims.content.icd.IcdIndexDrugsAndChemicalsXml;
import ca.cihi.cims.content.icd.IcdIndexNeoplasmXml;
import ca.cihi.cims.content.icd.IndexXml;
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexDrugsAndChemicals;
import ca.cihi.cims.content.icd.index.IcdIndexExternalInjury;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;
import ca.cihi.cims.model.Classification;

public enum IndexType {

	ICD_BOOK_INDEX(Classification.ICD, BookIndex.class, null, "BookIndex"), //
	ICD_LETTER_INDEX(Classification.ICD, LetterIndex.class, IndexXml.class, "LetterIndex"), //
	ICD_ALPHABETIC_INDEX(Classification.ICD, IcdIndexAlphabetical.class, IndexXml.class, "AlphabeticIndex"), //
	ICD_NEOPLASM_INDEX(Classification.ICD, IcdIndexNeoplasm.class, IcdIndexNeoplasmXml.class, "NeoplasmIndex"), //
	ICD_EXTERNAL_INJURY_INDEX(Classification.ICD, IcdIndexExternalInjury.class, IndexXml.class, "ExternalInjuryIndex"), //
	ICD_DRUGS_AND_CHEMICALS_INDEX(Classification.ICD, IcdIndexDrugsAndChemicals.class,
			IcdIndexDrugsAndChemicalsXml.class, "DrugsAndChemicalsIndex"), //
	CCI_BOOK_INDEX(Classification.CCI, BookIndex.class, null, "BookIndex"), //
	CCI_LETTER_INDEX(Classification.CCI, LetterIndex.class, null, "LetterIndex"), //
	CCI_ALPHABETIC_INDEX(Classification.CCI, CciIndexAlphabetical.class, IndexXml.class, "AlphabeticIndex"); //

	public static IndexType fromInstance(Index idx, Classification cls) {
		if (idx instanceof IcdIndexAlphabetical) {
			return ICD_ALPHABETIC_INDEX;
		} else if (idx instanceof IcdIndexNeoplasm) {
			return ICD_NEOPLASM_INDEX;
		} else if (idx instanceof IcdIndexDrugsAndChemicals) {
			return ICD_DRUGS_AND_CHEMICALS_INDEX;
		} else if (idx instanceof IcdIndexExternalInjury) {
			return ICD_EXTERNAL_INJURY_INDEX;
		} else if (idx instanceof CciIndexAlphabetical) {
			return CCI_ALPHABETIC_INDEX;
		} else if (idx instanceof LetterIndex) {
			return cls == Classification.ICD ? ICD_LETTER_INDEX : CCI_LETTER_INDEX;
		} else if (idx instanceof BookIndex) {
			return cls == Classification.ICD ? ICD_BOOK_INDEX : CCI_BOOK_INDEX;
		} else {
			throw new CIMSException("Unknown index type: " + idx);
		}
	}

	private final Classification classification;
	private final String code;
	private final Class<?> clazz;
	private final Class<?> xmlClass;
	private final String label;

	// ----------------------------------------------------------

	private IndexType(Classification classification, Class<?> clazz, Class<?> xmlClass, String label) {
		this.classification = classification;
		this.code = clazz.getAnnotation(HGWrapper.class).value();
		this.clazz = clazz;
		this.xmlClass = xmlClass;
		this.label = label;
	}

	public Classification getClassification() {
		return classification;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public Class<?> getXmlClass() {
		return xmlClass;
	}

	public boolean isBook() {
		return this == ICD_BOOK_INDEX || this == CCI_BOOK_INDEX;
	}

	public boolean isTerm() {
		return !(this == ICD_BOOK_INDEX || this == CCI_BOOK_INDEX || this == ICD_LETTER_INDEX || this == CCI_LETTER_INDEX);
	}

}
