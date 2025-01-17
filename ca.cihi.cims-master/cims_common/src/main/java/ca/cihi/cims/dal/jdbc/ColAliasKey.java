package ca.cihi.cims.dal.jdbc;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import ca.cihi.cims.dal.ElementVersion;

class ColAliasKey {
	private Class<? extends ElementVersion> clazz;
	private ColumnMapping column;

	public ColAliasKey(Class<? extends ElementVersion> clazz,
			ColumnMapping column) {
		this.clazz = clazz;
		this.column = column;
	}

	@Override
	public boolean equals(Object arg0) {
		return EqualsBuilder.reflectionEquals(this, arg0);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}