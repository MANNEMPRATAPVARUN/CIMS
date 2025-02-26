package ca.cihi.cims.content.icd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = "INDEX_REF")
@XmlAccessorType(XmlAccessType.FIELD)
public class IndexReferenceXml {

	@XmlElement(name = "REFERENCE_LINK_DESC")
	private String referenceLinkDescription;

	// "/885945/1709265/1709299/1709389"
	@XmlElement(name = "CONTAINER_INDEX_ID")
	private String containerIndexIdPath;

	// ---------------------------------------------------

	public String getContainerIndexIdPath() {
		return containerIndexIdPath;
	}

	public long getElementId() {
		if (StringUtils.isEmpty(containerIndexIdPath)) {
			return -1;
		} else {
			return Long.parseLong(containerIndexIdPath.substring(containerIndexIdPath.lastIndexOf("/") + 1));
		}
	}

	public String getReferenceLinkDescription() {
		return referenceLinkDescription;
	}

	public void setContainerIndexIdPath(String containerIndexIdPath) {
		this.containerIndexIdPath = containerIndexIdPath;
	}

	public void setReferenceLinkDescription(String referenceLinkDescription) {
		this.referenceLinkDescription = referenceLinkDescription;
	}

}
