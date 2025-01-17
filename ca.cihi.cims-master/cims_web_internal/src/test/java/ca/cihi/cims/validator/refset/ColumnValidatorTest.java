package ca.cihi.cims.validator.refset;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;

public class ColumnValidatorTest {

	@Test
	public void test() {
		ColumnValidator validator = new ColumnValidator();

		Errors errors = new MapBindingResult(new HashMap(), "map");

		PickListColumnBean columnBean = new PickListColumnBean();
		validator.validate(columnBean, errors);

		columnBean.setContextId(1l);
		columnBean.setColumnType(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		columnBean.setColumnOrder(1);
		columnBean.setRevisedColumnName(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		validator.validate(columnBean, errors);

	}
}
