package ca.cihi.cims.content.icd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ca.cihi.cims.content.shared.ValidationXml;

/**
 * FIXME: @XmlType.propOrder is working on elements but not on attributes <br/>
 * https://community.oracle.com/thread/977397 <br/>
 * or attributes must be reverse ordered
 * 
 * @author adenysenko
 */
@XmlRootElement(name = "validation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "language",
		"classification" //
		, "elementId", "genderCode", "genderDescriptionEng", "genderDescriptionFra",
		"ageRange" //
		, "MRDxMain", "dxType1", "dxType2", "dxType3", "dxType4", "dxType6", "dxType9", "dxTypeW", "dxTypeX",
		"dxTypeY", "newBorn" })
public class IcdValidationXml extends ValidationXml {

	@XmlElement(name = "MRDX_MAIN")
	private String MRDxMain;

	@XmlElement(name = "DX_TYPE_1")
	private String dxType1;

	@XmlElement(name = "DX_TYPE_2")
	private String dxType2;

	@XmlElement(name = "DX_TYPE_3")
	private String dxType3;

	@XmlElement(name = "DX_TYPE_4")
	private String dxType4;

	@XmlElement(name = "DX_TYPE_6")
	private String dxType6;

	@XmlElement(name = "DX_TYPE_9")
	private String dxType9;

	@XmlElement(name = "DX_TYPE_W")
	private String dxTypeW;

	@XmlElement(name = "DX_TYPE_X")
	private String dxTypeX;

	@XmlElement(name = "DX_TYPE_Y")
	private String dxTypeY;

	@XmlElement(name = "NEW_BORN")
	private String newBorn;

	// -------------------------------------------------

	@Override
	public boolean equals(Object object) {
		boolean isEqual = false;
		if (object instanceof IcdValidationXml) {
			IcdValidationXml validationXml = (IcdValidationXml) object;
			isEqual = super.equals(object)//
					&& this.MRDxMain.equals(validationXml.MRDxMain)
					&& this.dxType1.equals(validationXml.dxType1)
					&& this.dxType2.equals(validationXml.dxType2)
					&& this.dxType3.equals(validationXml.dxType3)
					&& this.dxType4.equals(validationXml.dxType4)
					&& this.dxType6.equals(validationXml.dxType6)
					&& this.dxType9.equals(validationXml.dxType9)
					&& this.dxTypeW.equals(validationXml.dxTypeW)
					&& this.dxTypeX.equals(validationXml.dxTypeX)
					&& this.dxTypeY.equals(validationXml.dxTypeY)
					&& this.newBorn.equals(validationXml.newBorn);
		}
		return isEqual;
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

	public String getMRDxMain() {
		return MRDxMain;
	}

	public String getNewBorn() {
		return newBorn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (MRDxMain == null ? 0 : MRDxMain.hashCode());
		result = prime * result + (dxType1 == null ? 0 : dxType1.hashCode());
		result = prime * result + (dxType2 == null ? 0 : dxType2.hashCode());
		result = prime * result + (dxType3 == null ? 0 : dxType3.hashCode());
		result = prime * result + (dxType4 == null ? 0 : dxType4.hashCode());
		result = prime * result + (dxType6 == null ? 0 : dxType6.hashCode());
		result = prime * result + (dxType9 == null ? 0 : dxType9.hashCode());
		result = prime * result + (dxTypeW == null ? 0 : dxTypeW.hashCode());
		result = prime * result + (dxTypeX == null ? 0 : dxTypeX.hashCode());
		result = prime * result + (dxTypeY == null ? 0 : dxTypeY.hashCode());
		result = prime * result + (newBorn == null ? 0 : newBorn.hashCode());
		return result;
	}

	public void setDxType1(boolean dxType1) {
		this.dxType1 = toString(dxType1);
	}

	public void setDxType1(String dxType1) {
		this.dxType1 = dxType1;
	}

	public void setDxType2(boolean dxType2) {
		this.dxType2 = toString(dxType2);
	}

	public void setDxType2(String dxType2) {
		this.dxType2 = dxType2;
	}

	public void setDxType3(boolean dxType3) {
		this.dxType3 = toString(dxType3);
	}

	public void setDxType3(String dxType3) {
		this.dxType3 = dxType3;
	}

	public void setDxType4(boolean dxType4) {
		this.dxType4 = toString(dxType4);
	}

	public void setDxType4(String dxType4) {
		this.dxType4 = dxType4;
	}

	public void setDxType6(boolean dxType6) {
		this.dxType6 = toString(dxType6);
	}

	public void setDxType6(String dxType6) {
		this.dxType6 = dxType6;
	}

	public void setDxType9(boolean dxType9) {
		this.dxType9 = toString(dxType9);
	}

	public void setDxType9(String dxType9) {
		this.dxType9 = dxType9;
	}

	public void setDxTypeW(boolean dxTypeW) {
		this.dxTypeW = toString(dxTypeW);
	}

	public void setDxTypeW(String dxTypeW) {
		this.dxTypeW = dxTypeW;
	}

	public void setDxTypeX(boolean dxTypeX) {
		this.dxTypeX = toString(dxTypeX);
	}

	public void setDxTypeX(String dxTypeX) {
		this.dxTypeX = dxTypeX;
	}

	public void setDxTypeY(boolean dxTypeY) {
		this.dxTypeY = toString(dxTypeY);
	}

	public void setDxTypeY(String dxTypeY) {
		this.dxTypeY = dxTypeY;
	}

	public void setMRDxMain(boolean mRDxMain) {
		MRDxMain = toString(mRDxMain);
	}

	public void setMRDxMain(String mRDxMain) {
		MRDxMain = mRDxMain;
	}

	public void setNewBorn(boolean newBorn) {
		this.newBorn = toString(newBorn);
	}

	public void setNewBorn(String newBorn) {
		this.newBorn = newBorn;
	}

	private String toString(boolean bool) {
		return bool ? "Y" : "N";
	}

}
