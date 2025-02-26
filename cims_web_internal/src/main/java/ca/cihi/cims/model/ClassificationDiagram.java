package ca.cihi.cims.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import ca.cihi.cims.content.shared.Diagram;

public class ClassificationDiagram implements Serializable {

	private static final long serialVersionUID = 1L;

	public static ClassificationDiagram convert(Diagram diagram) {

		ClassificationDiagram model = new ClassificationDiagram();

		model.setElementId(diagram.getElementId());
		model.setDescription(diagram.getDiagramDescription());
		model.setFileName(diagram.getDiagramFileName());
		model.setStatus(diagram.getStatus());

		return model;
	}

	@Size(min = 0, max = 200, message = "Description may not be greater than 200 characters")
	private String description;

	@Size(min = 1, max = 50, message = "Please specify an image file.")
	private String fileName;

	@NotEmpty
	private String status;

	private long elementId;

	private MultipartFile diagramFile;

	public String getDescription() {
		return description;
	}

	public MultipartFile getDiagramFile() {
		return diagramFile;
	}

	public long getElementId() {
		return elementId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiagramFile(MultipartFile diagramFile) {
		this.diagramFile = diagramFile;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ClassificationDiagram [description=" + description + ", fileName=" + fileName + ", status=" + status
				+ ", elementId=" + elementId + ", diagramFile=" + diagramFile + "]";
	}

}
