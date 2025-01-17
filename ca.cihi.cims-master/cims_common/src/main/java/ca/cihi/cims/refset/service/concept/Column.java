package ca.cihi.cims.refset.service.concept;

import java.util.List;

import org.springframework.validation.ObjectError;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:13:04 PM
 */
public interface Column extends RefsetConcept {

	public String getColumnName();

	public Short getColumnOrder();

	public String getColumnType();

	/**
	 * return list
	 *
	 * @throws Exception
	 */
	public List<Column> listColumns();

	/**
	 *
	 * @param name
	 */
	public void setColumnName(String name);

	/**
	 *
	 * @param order
	 */
	public void setColumnOrder(Short order);

	/**
	 * check if column removable or not
	 *
	 * @return
	 */
	public ObjectError removable();

}