package ca.cihi.cims.test;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is a debugging class, essentially. It hooks into the spring context
 * startup process (because it a java bean config class) and reports on the
 * username/password being used to connect to the database.
 */
@Configuration
public class DBConnInfoSource {

	@Value("${testDbUsername}")
	private String username;

	private Logger LOGGER = LogManager.getLogger(DBConnInfoSource.class);

	@Bean
	public DbConnInfo buildDbConnInfo() {

		LOGGER.info("Database username: " + username);

		DbConnInfo dbConnInfo = new DbConnInfo(username, DbConnInfo.REDACTED);

		return dbConnInfo;

	}

}
