package ca.cihi.cims.dal.jdbc;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcSequencesTest {

	@Mock
	private JdbcTemplate jdbc;

	private JdbcSequences sequences;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		sequences = new JdbcSequences();
		sequences.setJdbc(jdbc);
	}

	@Test
	public void testGettingSequencesCallsTheDatabase() {

		String sequenceName = "foo";

		List<Long> ids = new ArrayList<Long>();
		ids.add(0L);
		ids.add(1L);
		ids.add(2L);
		when(
				jdbc.queryForList("select " + sequenceName
						+ ".nextval from dual connect by level < 10",
						Long.class)).thenReturn(ids);

		Assert.assertEquals(0L, sequences.nextValue(sequenceName));
		Assert.assertEquals(1L, sequences.nextValue(sequenceName));
		Assert.assertEquals(2L, sequences.nextValue(sequenceName));

	}
}
