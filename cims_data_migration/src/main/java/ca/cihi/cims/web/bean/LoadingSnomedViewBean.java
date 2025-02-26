package ca.cihi.cims.web.bean;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class LoadingSnomedViewBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//private String sctVersionCode;
    private MultipartFile conceptFile;
    private MultipartFile descFile;
    private MultipartFile relationshipFile;
    private MultipartFile refsetLangFile;
    private String sctVersion;
    
    public String getSctVersion() {
		return sctVersion;
	}
	public void setSctVersion(String sctVersion) {
		this.sctVersion = sctVersion;
	}
/*	public String getSctVersionCode() {
		return sctVersionCode;
	}
	public void setSctVersionCode(String sctVersionCode) {
		this.sctVersionCode = sctVersionCode;
	}*/
	public MultipartFile getConceptFile() {
		return conceptFile;
	}
	public void setConceptFile(MultipartFile conceptFile) {
		this.conceptFile = conceptFile;
	}
	public MultipartFile getDescFile() {
		return descFile;
	}
	public void setDescFile(MultipartFile descFile) {
		this.descFile = descFile;
	}
	public MultipartFile getRelationshipFile() {
		return relationshipFile;
	}
	public void setRelationshipFile(MultipartFile relationshipFile) {
		this.relationshipFile = relationshipFile;
	}
	public MultipartFile getRefsetLangFile() {
		return refsetLangFile;
	}
	public void setRefsetLangFile(MultipartFile refsetLangFile) {
		this.refsetLangFile = refsetLangFile;
	}    


}
