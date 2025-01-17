package ca.cihi.cims.dal.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ORConfig {

	private Map<Class, ClassORMapping> classMappings = new HashMap<Class, ClassORMapping>();

	public Class forTableName(String tableName) {
		for (Class clazz : classMappings.keySet()) {
			ClassORMapping classMapping = classMappings.get(clazz);
			if (classMapping.getTable().equals(tableName))
				return clazz;
		}
		return null;
	}

	public ClassORMapping getMapping(Class clazz) {
		return classMappings.get(clazz);
	}

	public void addClassMapping(ClassORMapping mapping) {
		classMappings.put(mapping.getJavaClass(), mapping);
	}

	public void setClassMappings(Collection<ClassORMapping> mappings) {
		for (ClassORMapping mapping : mappings)
			addClassMapping(mapping);
	}

}
