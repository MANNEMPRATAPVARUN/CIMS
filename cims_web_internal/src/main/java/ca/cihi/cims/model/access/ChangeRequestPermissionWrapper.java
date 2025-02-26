package ca.cihi.cims.model.access;

import java.util.Set;

import ca.cihi.cims.Language;

public class ChangeRequestPermissionWrapper implements ChangeRequestPermission {

	private final ChangeRequestPermission permission;

	// ----------------------------------------------

	public ChangeRequestPermissionWrapper(ChangeRequestPermission permission) {
		this.permission = permission;
	}

	public Set<Language> getWriteLanguages() {
		return permission.getWriteLanguages();
	}

	@Override
	public boolean isCanAdd() {
		return permission.isCanAdd();
	}

	public boolean isCanRead() {
		return permission.isCanRead();
	}

	public boolean isCanDelete() {
		return permission.isCanDelete();
	}

	public boolean isCanWrite() {
		return permission.isCanWrite();
	}

	public boolean isCanWrite(Language language) {
		return permission.isCanWrite(language);
	}

}
