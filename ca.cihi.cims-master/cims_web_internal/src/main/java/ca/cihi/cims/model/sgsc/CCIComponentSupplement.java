package ca.cihi.cims.model.sgsc;

import org.springframework.util.StringUtils;

public class CCIComponentSupplement extends CodeDescription {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3023297889539035568L;
	private String note;

	public String getCodeDescription() {
		return "(" + getConceptCode() + ") " + getDescription();
	}

	public int getLength() {
		return getConceptCode().length();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toHtmlString() {
		StringBuilder sb = new StringBuilder();

		sb.append("<tr><td colspan='4'>").append("(").append(getConceptCode()).append(")&nbsp;&nbsp;").append(getDescription()).append("</td></tr>");

		if (!StringUtils.isEmpty(note)) {
			sb.append(getNote());
		}
		sb.append("<tr><td height='10' colspan='4'>&nbsp;</td></tr>");
		return sb.toString();
	}
}
