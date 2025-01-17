package ca.cihi.cims.model.changerequest;

public enum UserCommentType {
     Q ("comment for question")  ,
     A ("comment for advice"),
     C ("Regular Comment");
     
     private UserCommentType(String desc){
		 this.desc =desc;
	 }
     
     private String desc;

	
     
     
     public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
