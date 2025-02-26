package ca.cihi.cims.framework.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;

public interface SearchMapper {

	List<PropertyHierarchyDTO> searchHierarchy(Map<String, Object> params);
}
