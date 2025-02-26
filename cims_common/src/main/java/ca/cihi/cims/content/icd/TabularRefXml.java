package ca.cihi.cims.content.icd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = "TABULAR_REF")
@XmlAccessorType(XmlAccessType.FIELD)
public class TabularRefXml {

	@XmlAttribute(name = "type")
	private String type;

	@XmlElement(name = "TF_CONTAINER_CONCEPT_ID")
	private String containerConceptIdPath;

	@XmlElement(name = "CODE_PRESENTATION")
	private String codePresentation;

	// ---------------------------------------------------

	public String getCodePresentation() {
		return codePresentation;
	}

	public String getContainerConceptIdPath() {
		return containerConceptIdPath;
	}

	public long getElementId() {
		if (StringUtils.isEmpty(containerConceptIdPath)) {
			return -1;
		} else {
			return Long.parseLong(containerConceptIdPath.substring(containerConceptIdPath.lastIndexOf("/") + 1));
		}
	}

	public String getType() {
		return type;
	}

	public void setCodePresentation(String codePresentation) {
		this.codePresentation = codePresentation;
	}

	public void setContainerConceptIdPath(String containerConceptIdPath) {
		this.containerConceptIdPath = containerConceptIdPath;
	}

	public void setType(String type) {
		this.type = type;
	}

}
