package ca.cihi.cims.model.tabular;

import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class TabularConceptChildRulesTest {

	@Test
	public void testRU118() throws Exception {
		{
			// 2 levels of blocks
			TabularConceptChildRules rules = new TabularConceptChildRules(true, ICD_BLOCK, 1, true);
			assertTrue(rules.canAdd(ICD_BLOCK));
		}
		{
			// 2 levels of blocks
			TabularConceptChildRules rules = new TabularConceptChildRules(true, ICD_BLOCK, 2, true);
			assertFalse(rules.canAdd(ICD_BLOCK));
			assertTrue(rules.canAdd(ICD_CATEGORY));
		}
		{
			// 1 level of category
			TabularConceptChildRules rules = new TabularConceptChildRules(true, ICD_CATEGORY, 1, true);
			assertFalse(rules.canAdd(ICD_CATEGORY));
			assertFalse(rules.canAdd(ICD_BLOCK));
		}
	}

}
