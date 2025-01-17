package ca.cihi.cims.dal;

public abstract class PropertyVersion extends ElementVersion {

	private long domainElementId;
	
	public long getDomainElementId() {
		return domainElementId;
	}

	public void setDomainElementId(long domainElementId) {
		this.domainElementId = domainElementId;
	}

}
