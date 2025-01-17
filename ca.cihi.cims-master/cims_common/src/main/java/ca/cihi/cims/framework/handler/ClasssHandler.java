package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.mapper.ClasssMapper;

/**
 * encapsulates logic to work with classes in the database
 *
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:51:18 AM
 */
@Component
public class ClasssHandler {

	@Autowired
	private ClasssMapper classsMapper;

	/**
	 * Creates a new class record based on the input parameters. - check that a record with the same classname and base
	 * classification name does not exist -- if exists throw duplicate exception (checked) - Instantiates and returns
	 * Classs Object for the newly created classs record.
	 *
	 * -- return new Classs (.....) Throws exception if class with the same AK (baseClassificationName ,classname)
	 * already exists.
	 *
	 * @param data
	 */
	@Transactional
	public ClasssDTO createClasss(ClasssDTO data) {
		Map<String, Object> params = new HashMap<>();
		params.put("classsId", 0L);
		params.put("baseClassificationName", data.getBaseClassificationName());
		params.put("classsName", data.getClasssName());
		params.put("tableName", data.getTableName());
		params.put("friendlyName", data.getFriendlyName());
		classsMapper.createClasss(params);
		data.setClasssId((Long) params.get("classsId"));
		return data;
	}

	@Transactional
	public void createFullSetClassses(List<ClasssDTO> classsDTOs) {
		classsDTOs.stream().forEach(clazz -> createClasss(clazz));
	}

	/**
	 * Finds the classs record and returns the ClasssDTO object for it
	 *
	 * @param classsId
	 */
	public ClasssDTO getClasss(Long classsId) {
		return classsMapper.getClasss(classsId);
	}

	/**
	 * Finds the classs record and returns the ClasssDTO object for it
	 *
	 * @param classsName
	 * @param baseClassificationName
	 */
	public ClasssDTO getClasss(String classsName, String baseClassificationName) {
		Map<String, Object> params = new HashMap<>();
		params.put("baseClassificationName", baseClassificationName);
		params.put("classsName", classsName);
		return classsMapper.getClasssByClasssNameAndBaseClassificationName(params);
	}

	/**
	 *
	 * @param classsNames
	 * @param baseClassificationName
	 */
	public List<ClasssDTO> getClassses(List<String> classsNames, String baseClassificationName) {
		Map<String, Object> params = new HashMap<>();
		params.put("baseClassificationName", baseClassificationName);
		params.put("classsNames", classsNames);

		return classsMapper.getClassses(params);
	}

}