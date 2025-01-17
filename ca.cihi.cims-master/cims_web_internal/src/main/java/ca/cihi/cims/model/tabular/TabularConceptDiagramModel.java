package ca.cihi.cims.model.tabular;

import org.springframework.web.multipart.MultipartFile;

public class TabularConceptDiagramModel {

	private String name;
	private boolean remove;
	private MultipartFile file;

	// -----------------------------------------------

	public TabularConceptDiagramModel() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TabularConceptDiagramModel other = (TabularConceptDiagramModel) obj;
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (remove != other.remove) {
			return false;
		}
		return true;
	}

	public MultipartFile getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (file == null ? 0 : file.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (remove ? 1231 : 1237);
		return result;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

}
