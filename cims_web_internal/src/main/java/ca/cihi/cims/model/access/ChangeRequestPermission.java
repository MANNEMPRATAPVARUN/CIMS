package ca.cihi.cims.model.access;

import java.util.Set;

import ca.cihi.cims.Language;

public interface ChangeRequestPermission {

	Set<Language> getWriteLanguages();

	boolean isCanAdd();

	boolean isCanRead();

	boolean isCanDelete();

	boolean isCanWrite();

	boolean isCanWrite(Language language);

}