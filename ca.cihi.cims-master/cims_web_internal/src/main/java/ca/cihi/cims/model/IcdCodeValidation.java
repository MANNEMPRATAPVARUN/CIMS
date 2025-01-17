package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;

/**
 * 
 * @author wxing
 * 
 */
public class IcdCodeValidation extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 1L;

	// user selected language
	private String language;
	private String code;
	private String dataHolding;
	private String ageRange;
	private String gender;
	private String mrdxMain;
	private String dxType1;
	private String dxType2;
	private String dxType3;
	private String dxType4;
	private String dxType6;
	private String dxType9;
	private String dxTypeW;
	private String dxTypeX;
	private String dxTypeY;
	private String newBorn;
	private String validationXml;

	public String getAgeRange() {
		return ageRange;
	}

	public String getCode() {
		return code;
	}

	public String getDataHolding() {
		return dataHolding;
	}

	public String getDecoratedDxType1() {
		return getDecoratedValue(getDxType1());
	}

	public String getDecoratedDxType2() {
		return getDecoratedValue(getDxType2());
	}

	public String getDecoratedDxType3() {
		return getDecoratedValue(getDxType3());
	}

	public String getDecoratedDxType4() {
		return getDecoratedValue(getDxType4());
	}

	public String getDecoratedDxType6() {
		return getDecoratedValue(getDxType6());
	}

	public String getDecoratedDxType9() {
		return getDecoratedValue(getDxType9());
	}

	public String getDecoratedDxTypeW() {
		return getDecoratedValue(getDxTypeW());
	}

	public String getDecoratedDxTypeX() {
		return getDecoratedValue(getDxTypeX());
	}

	public String getDecoratedDxTypeY() {
		return getDecoratedValue(getDxTypeY());
	}

	public String getDecoratedMrdxMain() {
		return getDecoratedValue(getMrdxMain());
	}

	public String getDecoratedNewBorn() {
		return getDecoratedValue(getNewBorn());
	}

	public String getDecoratedValue(String aString) {
		String decoratedValue = null;

		if ("Y".equals(aString)) {
			if (Language.ENGLISH.getCode().equalsIgnoreCase(getLanguage())) {
				decoratedValue = WebConstants.ENGLISH_Y;
			} else {
				decoratedValue = WebConstants.FRENCH_Y;
			}
		} else {
			if (Language.ENGLISH.getCode().equalsIgnoreCase(getLanguage())) {
				decoratedValue = WebConstants.ENGLISH_N;
			} else {
				decoratedValue = WebConstants.FRENCH_N;
			}
		}
		return decoratedValue;
	}

	public String getDxType1() {
		return dxType1;
	}

	public String getDxType2() {
		return dxType2;
	}

	public String getDxType3() {
		return dxType3;
	}

	public String getDxType4() {
		return dxType4;
	}

	public String getDxType6() {
		return dxType6;
	}

	public String getDxType9() {
		return dxType9;
	}

	public String getDxTypeW() {
		return dxTypeW;
	}

	public String getDxTypeX() {
		return dxTypeX;
	}

	public String getDxTypeY() {
		return dxTypeY;
	}

	public String getGender() {
		return gender;
	}

	public String getLanguage() {
		return language;
	}

	public String getMrdxMain() {
		return mrdxMain;
	}

	public String getNewBorn() {
		return newBorn;
	}

	public String getValidationXml() {
		return validationXml;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDataHolding(String dataHolding) {
		this.dataHolding = dataHolding;
	}

	public void setDxType1(String dxType1) {
		this.dxType1 = dxType1;
	}

	public void setDxType2(String dxType2) {
		this.dxType2 = dxType2;
	}

	public void setDxType3(String dxType3) {
		this.dxType3 = dxType3;
	}

	public void setDxType4(String dxType4) {
		this.dxType4 = dxType4;
	}

	public void setDxType6(String dxType6) {
		this.dxType6 = dxType6;
	}

	public void setDxType9(String dxType9) {
		this.dxType9 = dxType9;
	}

	public void setDxTypeW(String dxTypeW) {
		this.dxTypeW = dxTypeW;
	}

	public void setDxTypeX(String dxTypeX) {
		this.dxTypeX = dxTypeX;
	}

	public void setDxTypeY(String dxTypeY) {
		this.dxTypeY = dxTypeY;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setMrdxMain(String mrdxMain) {
		this.mrdxMain = mrdxMain;
	}

	public void setNewBorn(String newBorn) {
		this.newBorn = newBorn;
	}

	public void setValidationXml(String validationXml) {
		this.validationXml = validationXml;
	}

}
