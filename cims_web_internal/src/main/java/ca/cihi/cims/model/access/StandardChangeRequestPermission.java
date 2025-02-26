package ca.cihi.cims.model.access;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.Language.FRENCH;
import static ca.cihi.cims.util.CollectionUtils.asSet;

import java.util.Set;

import ca.cihi.cims.Language;

public class StandardChangeRequestPermission implements ChangeRequestPermission {

	public static final ChangeRequestPermission READ = new StandardChangeRequestPermission(true, false, Language.NONE,
			false);
	public static final ChangeRequestPermission WRITE_FRENCH = new StandardChangeRequestPermission(true, true, FRENCH,
			true);
	public static final ChangeRequestPermission WRITE_ENGLISH = new StandardChangeRequestPermission(true, true,
			ENGLISH, true);
	public static final ChangeRequestPermission WRITE_ALL = new StandardChangeRequestPermission(true, true,
			Language.ALL, true);

	// ------------------------------------------------------------

	private final boolean canRead;
	private final boolean canWrite;
	private final boolean canDelete;
	private final Set<Language> writeLanguages;

	// ------------------------------------------------------------

	public StandardChangeRequestPermission(boolean canRead, boolean canWrite, Language writeLanguage, boolean canDelete) {
		this(canRead, canWrite, asSet(writeLanguage), canDelete);
	}

	public StandardChangeRequestPermission(boolean canRead, boolean canWrite, Set<Language> writeLanguages,
			boolean canDelete) {
		this.canRead = canRead;
		this.canDelete = canDelete;
		this.canWrite = canWrite;
		this.writeLanguages = writeLanguages;
	}

	@Override
	public Set<Language> getWriteLanguages() {
		return writeLanguages;
	}

	@Override
	public boolean isCanAdd() {
		return isCanDelete();
	}

	@Override
	public boolean isCanDelete() {
		return canDelete;
	}

	@Override
	public boolean isCanRead() {
		return canRead;
	}

	@Override
	public boolean isCanWrite() {
		return canWrite;
	}

	@Override
	public boolean isCanWrite(Language language) {
		return canWrite && writeLanguages.contains(language);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[canWrite=" + canWrite + ", writeLanguages=" + writeLanguages + "]";
	}

}
