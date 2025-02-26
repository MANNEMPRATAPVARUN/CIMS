package ca.cihi.cims.test;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DbConnInfo {

	private String username;

	private String password;

	public static final String REDACTED = "REDACTED";

	public DbConnInfo(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
