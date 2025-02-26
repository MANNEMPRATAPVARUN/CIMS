package ca.cihi.cims.sct.web.domain;

import java.io.Serializable;
import java.util.Date;

public class Term implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -5426451755542323593L;
	
	private long selectedTermId;
	private String version;
	private long conceptId;
	private String conceptType;

	private long conceptFsnId;
	private String conceptFsn;
	private long conceptPreferredId;
	private String conceptPreferred;

	private String selectedTermType;
	private String selectedTerm;
	private String selectedTermAcceptability;
	private Date effectiveDate;
	
	private long synonymId;
	private String synonym;

	public long getSynonymId() {
		return synonymId;
	}

	public void setSynonymId(long synonymId) {
		this.synonymId = synonymId;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public long getSelectedTermId() {
		return selectedTermId;
	}

	public void setSelectedTermId(long selectedTermId) {
		this.selectedTermId = selectedTermId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getConceptId() {
		return conceptId;
	}

	public void setConceptId(long conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptType() {
		return conceptType;
	}

	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}

	public long getConceptFsnId() {
		return conceptFsnId;
	}

	public void setConceptFsnId(long conceptFsnId) {
		this.conceptFsnId = conceptFsnId;
	}

	public String getConceptFsn() {
		return conceptFsn;
	}

	public void setConceptFsn(String conceptFsn) {
		this.conceptFsn = conceptFsn;
	}

	public long getConceptPreferredId() {
		return conceptPreferredId;
	}

	public void setConceptPreferredId(long conceptPreferredId) {
		this.conceptPreferredId = conceptPreferredId;
	}

	public String getConceptPreferred() {
		return conceptPreferred;
	}

	public void setConceptPreferred(String conceptPreferred) {
		this.conceptPreferred = conceptPreferred;
	}

	public String getSelectedTermType() {
		return selectedTermType;
	}

	public void setSelectedTermType(String selectedTermType) {
		this.selectedTermType = selectedTermType;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public String getSelectedTermAcceptability() {
		return selectedTermAcceptability;
	}

	public void setSelectedTermAcceptability(String selectedTermAcceptability) {
		this.selectedTermAcceptability = selectedTermAcceptability;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
