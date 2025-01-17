package ca.cihi.cims.content.icd;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "index")
public class IcdIndexDrugsAndChemicalsXml extends IndexBaseXml {

	@XmlElement(name = "TABULAR_REF")
	@XmlElementWrapper(name = "DRUGS_DETAIL")
	private List<TabularRefXml> drugsDetail;

	// ----------------------------------------

	public List<TabularRefXml> getDrugsDetail() {
		return drugsDetail;
	}

	public void setDrugsDetail(List<TabularRefXml> drugsDetail) {
		this.drugsDetail = drugsDetail;
	}

}
