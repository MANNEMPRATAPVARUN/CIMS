package ca.cihi.cims.refset.service.concept;

import java.util.List;

import ca.cihi.cims.refset.exception.DuplicateCodeNameException;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:12:23 PM
 */
public interface PickList extends RecordsList {

	public String getClassificationStandard();

	public String getCode();

	public String getName();

	public List<Column> listColumns();

	public void setName(String name) throws DuplicateCodeNameException;

	List<Column> listSublistColumns();

}