package ca.cihi.cims.model.tabular.validation;

public class TabularConceptIcdDxTypeModel {

	private long elementId;

	private boolean MRDxMain;
	private boolean diagType1;
	private boolean diagType2;
	private boolean diagType3;
	private boolean diagType4;
	private boolean diagType6;
	private boolean diagType8;
	private boolean diagType9;
	private boolean diagTypeW;
	private boolean diagTypeX;
	private boolean diagTypeY;

	// ------------------------------------------

	public long getElementId() {
		return elementId;
	}

	public boolean isDiagType1() {
		return diagType1;
	}

	public boolean isDiagType2() {
		return diagType2;
	}

	public boolean isDiagType3() {
		return diagType3;
	}

	public boolean isDiagType4() {
		return diagType4;
	}

	public boolean isDiagType6() {
		return diagType6;
	}

	public boolean isDiagType8() {
		return diagType8;
	}

	public boolean isDiagType9() {
		return diagType9;
	}

	public boolean isDiagTypeW() {
		return diagTypeW;
	}

	public boolean isDiagTypeX() {
		return diagTypeX;
	}

	public boolean isDiagTypeY() {
		return diagTypeY;
	}

	public boolean isMRDxMain() {
		return MRDxMain;
	}

	public void setDiagType1(boolean diagType1) {
		this.diagType1 = diagType1;
	}

	public void setDiagType2(boolean diagType2) {
		this.diagType2 = diagType2;
	}

	public void setDiagType3(boolean diagType3) {
		this.diagType3 = diagType3;
	}

	public void setDiagType4(boolean diagType4) {
		this.diagType4 = diagType4;
	}

	public void setDiagType6(boolean diagType6) {
		this.diagType6 = diagType6;
	}

	public void setDiagType8(boolean diagType8) {
		this.diagType8 = diagType8;
	}

	public void setDiagType9(boolean diagType9) {
		this.diagType9 = diagType9;
	}

	public void setDiagTypeW(boolean diagTypeW) {
		this.diagTypeW = diagTypeW;
	}

	public void setDiagTypeX(boolean diagTypeX) {
		this.diagTypeX = diagTypeX;
	}

	public void setDiagTypeY(boolean diagTypeY) {
		this.diagTypeY = diagTypeY;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setMRDxMain(boolean mRDxMain) {
		MRDxMain = mRDxMain;
	}

	@Override
	public String toString() {
		return "TabularConceptIcdDxTypeModel [elementId=" + elementId + ", MRDxMain=" + MRDxMain + ", diagType1="
				+ diagType1 + ", diagType2=" + diagType2 + ", diagType3=" + diagType3 + ", diagType4=" + diagType4
				+ ", diagType6=" + diagType6 + ", diagType8=" + diagType8 + ", diagType9=" + diagType9 + ", diagTypeW="
				+ diagTypeW + ", diagTypeX=" + diagTypeX + ", diagTypeY=" + diagTypeY + "]";
	}

}
