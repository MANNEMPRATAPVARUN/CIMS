package ca.cihi.cims.dal.jdbc;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-test.xml" })
public class ModelEmitterTest {

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamed;

	@Value("classpath:ca/cihi/cims/dal/jdbc/ModelEmitterProperties.sql")
	private Resource propertyQueryResource;

	@Value("classpath:ca/cihi/cims/dal/jdbc/ModelEmitterConceptRelationships.sql")
	private Resource relationshipsQueryResource;

	@Test
	@Ignore
	public void emitModel() throws IOException {

		dumpClassificationGraphs("CCI");
		dumpClassificationGraphs("ICD-10-CA");
	}

	private void dumpClassificationGraphs(String classification) throws IOException {
		dumpList(classification + " Properties", jdbcNamed.query(readResourceAsString(propertyQueryResource),
				new HashMap<String, Object>(), new HGPropertyReader(classification)));

		System.err.println("\n\n\n");

		dumpList(classification + " Relationships", jdbcNamed.query(readResourceAsString(relationshipsQueryResource),
				new HashMap<String, Object>(), new HGRelationshipReader(classification)));
	}

	private void dumpList(String graphName, List<String> list) {
		System.err.println("// " + graphName);
		System.err.println("digraph {");
		for (String str : list) {
			System.err.println(str);
		}
		System.err.println("}");
	}

	private final class HGPropertyReader extends DotDirectiveExtractor {

		public HGPropertyReader(String classification) {
			super(classification);
		}

		public void extractDirectives(ResultSet arg0, ArrayList<String> dotDirectives) throws SQLException {

			String elementClass = arg0.getString(2);
			String propertyClass = arg0.getString(3);

			dotDirectives.add(elementClass + "[shape=\"doubleoctagon\"];");
			dotDirectives.add(elementClass + "->" + propertyClass + ";");
		}
	}

	private final class HGRelationshipReader extends DotDirectiveExtractor {

		public HGRelationshipReader(String classification) {
			super(classification);
		}

		public void extractDirectives(ResultSet arg0, ArrayList<String> dotDirectives) throws SQLException {
			String elementClass = arg0.getString(2);
			String propertyClass = arg0.getString(3);
			String targetClass = arg0.getString(4);

			dotDirectives.add(elementClass + "[shape=\"doubleoctagon\"];");
			dotDirectives.add(elementClass + "->" + targetClass + " [label=\"" + propertyClass + "\"];");
		}
	}

	private abstract class DotDirectiveExtractor implements ResultSetExtractor<List<String>> {

		protected String classification;

		public DotDirectiveExtractor(String classification) {
			this.classification = classification;
		}

		@Override
		public List<String> extractData(ResultSet arg0) throws SQLException, DataAccessException {

			ArrayList<String> dotDirectives = new ArrayList<String>();

			while (arg0.next()) {
				String classification = arg0.getString(1);

				if (this.classification.equals(classification)) {
					extractDirectives(arg0, dotDirectives);
				}
			}

			return dotDirectives;
		}

		protected abstract void extractDirectives(ResultSet arg0, ArrayList<String> dotDirectives) throws SQLException;
	}

	private String readResourceAsString(Resource resource) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(resource.getInputStream(), writer, "UTF-8");
		return writer.toString();
	}
}
