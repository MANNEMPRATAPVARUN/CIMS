package ca.cihi.cims.framework.domain;

import java.util.List;
import java.util.stream.Collectors;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.handler.ClasssHandler;

/**
 * @author miftimie
 * @version 1.0
 * @created 13-Jun-2016 10:47:25 AM
 */
public class Classs {

	/**
	 * Adds a new classs in the system, instantiates an object for it and returns it.
	 *
	 * @param data
	 */
	public static Classs create(ClasssDTO data) {
		ClasssHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ClasssHandler.class);
		ClasssDTO dto = handler.createClasss(data);

		return new Classs(dto);
	}

	/**
	 * classsDTO = ClasssHandler.getClasss(classsId) return new Classs(classsDTO )
	 *
	 * @param classsId
	 */
	public static Classs findById(Long classsId) {
		ClasssHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ClasssHandler.class);
		return new Classs(handler.getClasss(classsId));
	}

	/**
	 *
	 * @param className
	 * @param baseClassificationName
	 */
	public static Classs findByName(String classsName, String baseClassificationName) {
		ClasssHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ClasssHandler.class);

		return new Classs(handler.getClasss(classsName, baseClassificationName));
	}

	/**
	 *
	 * @param classNames
	 * @param baseClassificationName
	 */
	public static List<Classs> findByNames(List<String> classsNames, String baseClassificationName) {
		ClasssHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ClasssHandler.class);

		return handler.getClassses(classsNames, baseClassificationName).stream().map(item -> new Classs(item))
				.collect(Collectors.toList());
	}

	private String baseClassificationName;

	private Long classId;

	private String className;

	private String friendlyName;

	private String tableName;

	public Classs() {

	}

	/**
	 * this.classId =data.classId .....
	 *
	 * @param data
	 */
	public Classs(ClasssDTO data) {
		this.setClassId(data.getClasssId());
		this.setBaseClassificationName(data.getBaseClassificationName());
		this.setClassName(data.getClasssName());
		this.setTableName(data.getTableName());
		this.setFriendlyName(data.getFriendlyName());
	}

	public String getBaseClassificationName() {
		return baseClassificationName;
	}

	public Long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setBaseClassificationName(String baseClassificationName) {
		this.baseClassificationName = baseClassificationName;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}