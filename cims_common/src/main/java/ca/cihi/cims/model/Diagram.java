package ca.cihi.cims.model;

public class Diagram {

	private String diagramFileName;
	private byte[] diagramBytes;

	public byte[] getDiagramBytes() {
		return diagramBytes;
	}

	public String getDiagramFileName() {
		return diagramFileName;
	}

	public void setDiagramBytes(byte[] diagramBytes) {
		this.diagramBytes = diagramBytes;
	}

	public void setDiagramFileName(String diagramFileName) {
		this.diagramFileName = diagramFileName;
	}

}
