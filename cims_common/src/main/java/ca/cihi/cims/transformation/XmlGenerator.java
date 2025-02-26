package ca.cihi.cims.transformation;

import java.util.List;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.model.TabularConceptInfo;
import ca.cihi.cims.model.TransformationError;

public interface XmlGenerator {

	String generateXml(final String classification, final String version,
			final TabularConcept tabularConcept,
			final List<TransformationError> errors, final String dtdFile,
			final String language, final ContextAccess ctxx,
			final String presentationType,
			final TabularConceptInfo tabularConceptInfo);
}
