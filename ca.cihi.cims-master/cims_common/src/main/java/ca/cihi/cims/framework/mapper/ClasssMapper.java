package ca.cihi.cims.framework.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.framework.dto.ClasssDTO;

public interface ClasssMapper {

	void createClasss(Map<String, Object> params);

	ClasssDTO getClasss(Long classsId);

	ClasssDTO getClasssByClasssNameAndBaseClassificationName(Map<String, Object> params);

	List<ClasssDTO> getClassses(Map<String, Object> params);
}
