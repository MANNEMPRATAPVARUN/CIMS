package ca.cihi.cims.web.bean.search;

import javax.validation.constraints.NotNull;

import ca.cihi.cims.model.changerequest.ChangeRequestCategory;

public class IndexChangesBean extends ChangeRequestPropetiesBean {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private Long bookId;
	@NotNull
	private Long leadTermId;
	private String leadTermText;
	
	public IndexChangesBean() {
		setRequestCategory(ChangeRequestCategory.I.name());
	}
	
	public Long getBookId() {
		return bookId;
	}
	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
	public Long getLeadTermId() {
		return leadTermId;
	}
	public void setLeadTermId(Long leadTermId) {
		this.leadTermId = leadTermId;
	}
	public String getLeadTermText() {
		return leadTermText;
	}
	public void setLeadTermText(String leadTermText) {
		this.leadTermText = leadTermText;
	}
}
