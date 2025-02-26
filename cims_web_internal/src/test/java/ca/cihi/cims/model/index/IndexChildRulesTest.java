package ca.cihi.cims.model.index;

import static ca.cihi.cims.model.index.IndexType.CCI_ALPHABETIC_INDEX;
import static ca.cihi.cims.model.index.IndexType.CCI_BOOK_INDEX;
import static ca.cihi.cims.model.index.IndexType.CCI_LETTER_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_ALPHABETIC_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_BOOK_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_EXTERNAL_INJURY_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_LETTER_INDEX;
import static ca.cihi.cims.model.index.IndexType.ICD_NEOPLASM_INDEX;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class IndexChildRulesTest {

	@Test
	public void testRU118() throws Exception {
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_BOOK_INDEX, 1, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertFalse(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_LETTER_INDEX, 1, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertTrue(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_LETTER_INDEX, 2, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertFalse(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertTrue(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_LETTER_INDEX, 3, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertFalse(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertTrue(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_ALPHABETIC_INDEX, 1, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertTrue(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_ALPHABETIC_INDEX, 1, 9);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertFalse(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertFalse(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, ICD_NEOPLASM_INDEX, 4, 1);
			assertFalse(rules.canAdd(ICD_BOOK_INDEX));
			assertFalse(rules.canAdd(ICD_LETTER_INDEX));
			assertFalse(rules.canAdd(ICD_ALPHABETIC_INDEX));
			assertTrue(rules.canAdd(ICD_NEOPLASM_INDEX));
			assertFalse(rules.canAdd(ICD_EXTERNAL_INJURY_INDEX));
			assertFalse(rules.canAdd(ICD_DRUGS_AND_CHEMICALS_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, CCI_BOOK_INDEX, 1, 1);
			assertFalse(rules.canAdd(CCI_BOOK_INDEX));
			assertFalse(rules.canAdd(CCI_LETTER_INDEX));
			assertFalse(rules.canAdd(CCI_ALPHABETIC_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, CCI_BOOK_INDEX, 1, 1);
			assertFalse(rules.canAdd(CCI_BOOK_INDEX));
			assertFalse(rules.canAdd(CCI_LETTER_INDEX));
			assertFalse(rules.canAdd(CCI_ALPHABETIC_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, CCI_ALPHABETIC_INDEX, 1, 2);
			assertFalse(rules.canAdd(CCI_BOOK_INDEX));
			assertFalse(rules.canAdd(CCI_LETTER_INDEX));
			assertTrue(rules.canAdd(CCI_ALPHABETIC_INDEX));
		}
		{
			IndexChildRules rules = new IndexChildRules(true, CCI_LETTER_INDEX, 1, 1);
			assertFalse(rules.canAdd(CCI_BOOK_INDEX));
			assertFalse(rules.canAdd(CCI_LETTER_INDEX));
			assertTrue(rules.canAdd(CCI_ALPHABETIC_INDEX));
		}
	}

}
