package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This class queries for sequences values and stores a buffer of them in
 * memory.
 */
@Component
public class JdbcSequences implements Sequences {

	private static final int IDS_TO_CACHE = 10;

	private Map<String, List<Long>> sequences = new HashMap<String, List<Long>>();

	private JdbcTemplate jdbc;

	private static final String SEQUENCE_QUERY = "select ?.nextval from dual connect by level < "
			+ IDS_TO_CACHE;

	@Override
	public synchronized long nextValue(String sequenceName) {

		if (!sequences.containsKey(sequenceName)) {
			sequences.put(sequenceName, new ArrayList<Long>());
		}

		List<Long> availableIds = sequences.get(sequenceName);

		if (availableIds.isEmpty()) {
			String query = SEQUENCE_QUERY.replaceAll("\\?", sequenceName);

			List<Long> newIds = jdbc.queryForList(query, Long.class);

			availableIds.addAll(newIds);
		}

		return availableIds.remove(0);
	}

	@Autowired
	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

}
