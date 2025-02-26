package ca.cihi.cims.model.snomed;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCTDesc implements SCTBase,Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(SCTDesc.class);
	
	private long id;     
	private Date effectiveTime;     
	private String activeIndCode;   
	private long moduleId;            
	private long conceptId;          
	private String languageCode;       
	private long typeId;             
	private String term;                
	private long caseSignificanceId; 
	  
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getActiveIndCode() {
		return activeIndCode;
	}

	public void setActiveIndCode(String activeIndCode) {
		this.activeIndCode = activeIndCode;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getConceptId() {
		return conceptId;
	}

	public void setConceptId(long conceptId) {
		this.conceptId = conceptId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public long getCaseSignificanceId() {
		return caseSignificanceId;
	}

	public void setCaseSignificanceId(long caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}
	
	//This is to populate one record based on below format
	//id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId
	public void setValues(String[] values) throws Exception {
		/*
		LOGGER.debug("values[0]="+values[0]+" values[1]="+values[1]+" values[2]="+values[2]+" values[3]="+values[3]+
				                  " values[4]="+values[4] + "values[5]=" +values[5] + "values[6]=" +values[6] +
				                  " values[7]="+values[7] + "values[8]=" +values[8] );
       */				                  
		this.setId(Long.parseLong(values[0]));
		String DATE_FORMAT = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.setEffectiveTime(sdf.parse(values[1]));
		this.setActiveIndCode(values[2]);
		this.setModuleId(Long.parseLong(values[3]));
		this.setConceptId(Long.parseLong(values[4]));
		this.setLanguageCode(values[5]);
		this.setTypeId(Long.parseLong(values[6]));
		this.setTerm(values[7]);
		this.setCaseSignificanceId(Long.parseLong(values[8]));
	}
	
}
