package ca.cihi.cims.content.icd;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "index")
public class IcdIndexNeoplasmXml extends IndexBaseXml {

	@XmlElement(name = "TABULAR_REF")
	@XmlElementWrapper(name = "NEOPLASM_DETAIL")
	private List<TabularRefXml> neoplasmDetail;

	// ----------------------------------------

	public List<TabularRefXml> getNeoplasmDetail() {
		return neoplasmDetail;
	}

	public void setNeoplasmDetail(List<TabularRefXml> neoplasmDetail) {
		this.neoplasmDetail = neoplasmDetail;
	}

}
