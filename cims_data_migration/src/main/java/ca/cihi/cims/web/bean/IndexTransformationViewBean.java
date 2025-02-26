package ca.cihi.cims.web.bean;

/**
 * 
 * @author wxing
 * 
 */
public class IndexTransformationViewBean extends BasicInfoBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8430938461348358977L;
	private String bookIndexType;
	private String language;

	public String getBookIndexType() {
		return bookIndexType;
	}

	public String getLanguage() {
		return language;
	}

	public void setBookIndexType(String bookIndexType) {
		this.bookIndexType = bookIndexType;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}