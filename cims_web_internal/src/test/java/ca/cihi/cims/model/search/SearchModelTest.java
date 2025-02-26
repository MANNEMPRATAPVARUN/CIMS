package ca.cihi.cims.model.search;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test coverage for beans within {@code ca.cihi.cims.model.search} package
 * 
 * @author rshnaper
 * 
 */
public class SearchModelTest {

	@Test
	public void testColumn() {
		ColumnType columnType = new ColumnType();
		Column column = new Column(1, columnType);
		column.setOrder(1);

		MatcherAssert.assertThat(column, Matchers.hasProperty("id", Matchers.is(1L)));
		MatcherAssert.assertThat(column, Matchers.hasProperty("order", Matchers.is(1)));

		column.setId(2);
		MatcherAssert.assertThat(column, Matchers.hasProperty("id", Matchers.is(2L)));

		MatcherAssert.assertThat(column.hashCode(), Matchers.is(Objects.hash(2L, columnType.getId())));

		MatcherAssert.assertThat(column.compareTo(column), Matchers.is(0));

		MatcherAssert.assertThat(column.equals(column), Matchers.is(true));
		MatcherAssert.assertThat(column.equals(columnType), Matchers.is(false));
	}

	@Test
	public void testColumnType() {
		ColumnType columnType = new ColumnType();
		columnType.setDefault(true);
		columnType.setDisplayName("displayName");
		columnType.setId(1L);
		columnType.setModelName("modelName");
		columnType.setOrder(1);

		MatcherAssert.assertThat(columnType, Matchers.hasProperty("id", Matchers.is(1L)));
		MatcherAssert.assertThat(columnType, Matchers.hasProperty("default", Matchers.is(true)));
		MatcherAssert.assertThat(columnType, Matchers.hasProperty("displayName", Matchers.is("displayName")));
		MatcherAssert.assertThat(columnType, Matchers.hasProperty("modelName", Matchers.is("modelName")));
		MatcherAssert.assertThat(columnType, Matchers.hasProperty("order", Matchers.is(1)));
	}

	@Test
	public void testCriterion() {
		CriterionType type = new CriterionType(1);
		Criterion criterion = new Criterion(1, type);
		criterion.setValue("value");

		MatcherAssert.assertThat(criterion, Matchers.hasProperty("id", Matchers.is(1L)));
		MatcherAssert.assertThat(criterion, Matchers.hasProperty("type", Matchers.is(type)));
		MatcherAssert.assertThat(criterion, Matchers.hasProperty("value", Matchers.is("value")));
		MatcherAssert.assertThat(criterion.hashCode(), Matchers.is(Objects.hash(1L, type.getId())));
	}

	@Test
	public void testCriterionType() {
		CriterionType type = new CriterionType(1);
		type.setCardinalityMax(Integer.MAX_VALUE);
		type.setCardinalityMin(Integer.MIN_VALUE);
		type.setClassName("className");
		type.setDisplayName("displayName");
		type.setModelName("modelName");

		MatcherAssert.assertThat(type, Matchers.hasProperty("id", Matchers.is(1L)));
		MatcherAssert.assertThat(type, Matchers.hasProperty("cardinalityMax", Matchers.is(Integer.MAX_VALUE)));
		MatcherAssert.assertThat(type, Matchers.hasProperty("cardinalityMin", Matchers.is(Integer.MIN_VALUE)));
		MatcherAssert.assertThat(type, Matchers.hasProperty("className", Matchers.is("className")));
		MatcherAssert.assertThat(type, Matchers.hasProperty("displayName", Matchers.is("displayName")));
		MatcherAssert.assertThat(type, Matchers.hasProperty("modelName", Matchers.is("modelName")));

		type.setId(2);
		MatcherAssert.assertThat(type, Matchers.hasProperty("id", Matchers.is(2L)));
	}

	@Test
	public void testSearch() {
		SearchType searchType = new SearchType(1, "name");

		CriterionType criterionType = new CriterionType(1);
		Criterion criterion = new Criterion(1, criterionType);
		criterion.setValue("value");

		ColumnType columnType = new ColumnType();
		Column column = new Column(1, columnType);

		Date createdDate = new Date();
		Date updatedDate = new Date();

		long id = 1;
		long ownerId = 123;
		String name = "name";
		boolean isShared = true;
		String classificationName = "classificationName";

		Search search = new Search(id, searchType);
		search.setClassificationName(classificationName);
		search.setColumns(Arrays.asList(column));
		search.setCreated(createdDate);
		search.setId(id);
		search.setName(name);
		search.setOwnerId(ownerId);
		search.setShared(isShared);
		search.setType(searchType);
		search.setUpdated(updatedDate);
		search.addCriterion(criterion);

		MatcherAssert.assertThat(search, Matchers.hasProperty("id", Matchers.is(id)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("classificationName", Matchers.is(classificationName)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("created", Matchers.is(createdDate)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("name", Matchers.is(name)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("ownerId", Matchers.is(ownerId)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("shared", Matchers.is(isShared)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("type", Matchers.is(searchType)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("updated", Matchers.is(updatedDate)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("columns", Matchers.contains(column)));
		MatcherAssert.assertThat(search, Matchers.hasProperty("criteria", Matchers.contains(criterion)));

		// add null criterion
		search.addCriterion(null);
		MatcherAssert.assertThat(search, Matchers.hasProperty("criteria", Matchers.not(Matchers.nullValue())));

		// remove criterion
		search.removeCriterion(null);
		MatcherAssert.assertThat(search, Matchers.hasProperty("criteria", Matchers.contains(criterion)));

		search.removeCriterion(criterion);
		MatcherAssert.assertThat(search, Matchers.hasProperty("criteria", Matchers.empty()));

		// equals test
		MatcherAssert.assertThat(search.equals(search), Matchers.is(true));

		// hashcode test
		MatcherAssert.assertThat(search.hashCode(),
				Matchers.is(Objects.hash(search.getId(), search.getOwnerId(), search.getType().getId())));
	}

	@Test
	public void testSearchType() {
		SearchType type = new SearchType(1, "name");

		MatcherAssert.assertThat(type, Matchers.hasProperty("id", Matchers.is(1L)));
		MatcherAssert.assertThat(type, Matchers.hasProperty("name", Matchers.is("name")));

		type.setId(2);
		type.setName("name2");
		MatcherAssert.assertThat(type, Matchers.hasProperty("id", Matchers.is(2L)));
		MatcherAssert.assertThat(type, Matchers.hasProperty("name", Matchers.is("name2")));
	}

	@Test
	public void testSearchTypeEnum() {
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ChangeRequestProperties.getTypeName()),
				Matchers.is(SearchTypes.ChangeRequestProperties));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.CCITabularComparative.getTypeName()),
				Matchers.is(SearchTypes.CCITabularComparative));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.CCITabularSimple.getTypeName()),
				Matchers.is(SearchTypes.CCITabularSimple));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ChangeRequestCCITabular.getTypeName()),
				Matchers.is(SearchTypes.ChangeRequestCCITabular));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ChangeRequestICDTabular.getTypeName()),
				Matchers.is(SearchTypes.ChangeRequestICDTabular));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ChangeRequestIndex.getTypeName()),
				Matchers.is(SearchTypes.ChangeRequestIndex));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.CCIReferenceValuesComparative.getTypeName()),
				Matchers.is(SearchTypes.CCIReferenceValuesComparative));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ICDTabularComparative.getTypeName()),
				Matchers.is(SearchTypes.ICDTabularComparative));
		MatcherAssert.assertThat(SearchTypes.forName(SearchTypes.ICDTabularSimple.getTypeName()),
				Matchers.is(SearchTypes.ICDTabularSimple));
	}
}
