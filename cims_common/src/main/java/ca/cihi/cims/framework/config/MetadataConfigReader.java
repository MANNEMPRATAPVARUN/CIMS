package ca.cihi.cims.framework.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.framework.config.annotation.Concept;
import ca.cihi.cims.framework.config.annotation.Property;
import ca.cihi.cims.framework.config.annotation.Relationship;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;

public class MetadataConfigReader {

	private MetadataConfig config = new MetadataConfig();

	public void addClass(Class<? extends ca.cihi.cims.framework.domain.Concept> clazz) {
		if (config.isDefined(clazz)) {
			return;
		}

		ConceptMetadata metadata = new ConceptMetadata();

		configConcept(clazz, metadata);

		configProperty(clazz, metadata);

		config.define(clazz, metadata);
	}

	private <T extends ca.cihi.cims.framework.domain.Concept> void configConcept(Class<T> clazz,
			ConceptMetadata metadata) {
		Concept conceptAnnotation = clazz.getAnnotation(Concept.class);
		if (conceptAnnotation == null) {
			return;
		}
		metadata.setPropertyKeys(new HashMap<>());
		Map<ConceptLoadDegree, ConceptPropertyConfiguration> propertyConfigurations = new EnumMap<>(
				ConceptLoadDegree.class);

		for (ConceptLoadDegree degree : ConceptLoadDegree.values()) {
			ConceptPropertyConfiguration configuration = propertyConfigurations.get(degree);
			if (configuration == null) {
				configuration = new ConceptPropertyConfiguration();
				configuration.setKeys(new ArrayList<>());
				propertyConfigurations.put(degree, configuration);
			}
		}
		metadata.setPropertyConfigurations(propertyConfigurations);

		Relationship[] relationships = clazz.getAnnotationsByType(Relationship.class);
		if ((relationships != null) && (relationships.length > 0)) {
			for (Relationship relationship : relationships) {
				configureRelationship(relationship, metadata);
			}
		}
	}

	private <T extends ca.cihi.cims.framework.domain.Concept> void configProperty(Class<T> clazz,
			ConceptMetadata metadata) {
		List<Method> setters = findAllSetterMethods(clazz);

		for (Method setter : setters) {
			processAnnotation(metadata, setter);
		}
	}

	private void configureRelationship(Relationship relationship, ConceptMetadata metadata) {
		ConceptLoadDegree[] degrees = relationship.degrees();
		String classsName = relationship.classsName();
		PropertyKey key = new PropertyKey(classsName, Language.NOLANGUAGE, PropertyType.ConceptProperty);
		metadata.getPropertyKeys().put(key.generateKeyIdentifier(), key);
		for (ConceptLoadDegree degree : degrees) {

			ConceptPropertyConfiguration configuration = metadata.getPropertyConfigurations().get(degree);
			configuration.getKeys().add(key);
		}
	}

	private <T extends ca.cihi.cims.framework.domain.Concept> List<Method> findAllSetterMethods(Class<T> clazz) {
		List<Method> methods = new ArrayList<>();

		for (Method method : clazz.getDeclaredMethods()) {

			if (method.getDeclaringClass().equals(Object.class)) {
				continue;
			}

			if (isSetter(method)) {
				methods.add(method);
			}
		}

		return methods;
	}

	public MetadataConfig getConfig() {
		return config;
	}

	private boolean isSetter(Method method) {
		if (!method.getReturnType().equals(void.class)) {
			return false;
		}

		return method.getName().startsWith("set");
	}

	private void processAnnotation(ConceptMetadata metadata, Method setter) {
		Property propertyAnnotation = setter.getAnnotation(Property.class);

		if (propertyAnnotation != null) {
			String classsName = propertyAnnotation.classsName();
			Language[] languages = propertyAnnotation.languages();
			PropertyType propertyType = propertyAnnotation.propertyType();
			ConceptLoadDegree[] degrees = propertyAnnotation.degrees();

			List<PropertyKey> keys = new ArrayList<>();

			if ((languages == null) || (languages.length == 0)) {
				keys.add(new PropertyKey(classsName, Language.NOLANGUAGE, propertyType));
			} else {
				for (Language language : languages) {
					keys.add(new PropertyKey(classsName, language, propertyType));
				}
			}
			keys.stream().forEach(key -> {
				metadata.getPropertyKeys().put(key.generateKeyIdentifier(), key);
				for (ConceptLoadDegree degree : degrees) {
					ConceptPropertyConfiguration configuration = metadata.getPropertyConfigurations().get(degree);
					configuration.getKeys().add(key);
				}
			});

		}
	}
}
