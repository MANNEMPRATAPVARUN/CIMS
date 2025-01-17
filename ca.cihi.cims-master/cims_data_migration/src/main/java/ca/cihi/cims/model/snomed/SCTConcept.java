package ca.cihi.cims.model.snomed;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCTConcept implements SCTBase,Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(SCTConcept.class);

	private long id;
	private Date effectiveTime;
	private String activeIndCode;	
	private long moduleId;
	private long definitionStatusId;
	
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

	public long getDefinitionStatusId() {
		return definitionStatusId;
	}

	public void setDefinitionStatusId(long definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}
	
	//This method is to populate values based on concept file format
	//id	effectiveTime	active	moduleId	definitionStatusId
	public void setValues(String[] values) throws Exception {
		//LOGGER.debug("values[0]="+values[0]+" values[1]="+values[1]+" values[2]="+values[2]+" values[3]="+values[3]+" values[4]="+values[4]);
		this.setId(Long.parseLong(values[0]));
		String DATE_FORMAT = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.setEffectiveTime(sdf.parse(values[1]));
		this.setActiveIndCode(values[2]);
		this.setModuleId(Long.parseLong(values[3]));
		this.setDefinitionStatusId(Long.parseLong(values[4]));
	}
	
	
}
