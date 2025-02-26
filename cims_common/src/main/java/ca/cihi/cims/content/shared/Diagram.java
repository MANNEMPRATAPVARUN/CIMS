package ca.cihi.cims.content.shared;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.GraphicsPropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGStatus;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("Diagram")
public abstract class Diagram extends BaseConcept implements Comparable<Diagram> {

	public static Diagram create(ContextAccess access) {

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Diagram", "");
		Diagram diagram = access.createWrapper(Diagram.class, "Diagram", businessKey);

		return diagram;
	}

	// Todo - create setParent method and set to RootConcept
	// unfortunately dont think it works if its private tho

	@Override
	public int compareTo(Diagram other) {
		return new CompareToBuilder().append(getElementId(), other.getElementId()).toComparison();
	}

	@HGProperty(className = "DiagramDescription", elementClass = TextPropertyVersion.class)
	public abstract String getDiagramDescription();

	@HGProperty(className = "DiagramFigure", elementClass = GraphicsPropertyVersion.class)
	public abstract byte[] getDiagramFigure();

	@HGProperty(className = "DiagramFileName", elementClass = TextPropertyVersion.class)
	public abstract String getDiagramFileName();

	@HGStatus
	public abstract String getStatus();

	public abstract void setDiagramDescription(String diagramDescription);

	public abstract void setDiagramFigure(byte[] diagramFigure);

	public abstract void setDiagramFileName(String diagramFileName);

	public abstract void setStatus(String status);

}
