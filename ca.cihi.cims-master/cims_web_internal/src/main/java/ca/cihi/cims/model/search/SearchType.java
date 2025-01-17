package ca.cihi.cims.model.search;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * A bean representing a specific type of search object
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p>
 */
public class SearchType extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 1L;

	private long id;
	
	@NotNull
	@Size(max = 50)
	private String name;
	
	public SearchType(){}
	
	public SearchType(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
