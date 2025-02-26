package ca.cihi.cims.web.bean.refset;

import org.springframework.web.multipart.MultipartFile;

public class SupplementViewBean extends RefsetBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2103125384956715322L;

	private String code;
	private String name;

	private String fileName;
	private byte content[];
	
	private MultipartFile file;

	private Long supplementElementId;
	private Long supplementElementVersionId;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSupplementElementId() {
		return supplementElementId;
	}

	public void setSupplementElementId(Long supplementElementId) {
		this.supplementElementId = supplementElementId;
	}

	public Long getSupplementElementVersionId() {
		return supplementElementVersionId;
	}

	public void setSupplementElementVersionId(Long supplementElementVersionId) {
		this.supplementElementVersionId = supplementElementVersionId;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte content[]) {
		this.content = content;
	}
}
