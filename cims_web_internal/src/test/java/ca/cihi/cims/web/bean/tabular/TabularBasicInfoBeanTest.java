package ca.cihi.cims.web.bean.tabular;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;

public class TabularBasicInfoBeanTest {
	private TabularBasicInfoBean bean;

	@Before
	public void setUp() {
		bean = new TabularBasicInfoBean();
	}

	@Test
	public void testGetsAndSets() {
		bean.setAddBlockType(TabularConceptType.ICD_BLOCK);
		bean.setAddCategoryType(TabularConceptType.ICD_CATEGORY);
		bean.setAddCodeVisible(true);
		bean.setAddGroupType(TabularConceptType.CCI_GROUP);
		bean.setAddQualifierVisible(true);
		bean.setAddType(TabularConceptType.CCI_BLOCK);
		bean.setCanadianEnhancementEditable(true);
		bean.setCanadianEnhancementVisible(true);
		bean.setCciInvasivenessLevels(null);
		bean.setChildTable(true);
		bean.setChildTableVisible(true);
		TabularConceptModel model = new TabularConceptModel();
		bean.setModel(model);
		bean.setCode("code");
		bean.setCodeEditable(true);
		bean.setCodeVisible(true);
		bean.setDaggerAsteriskEditable(true);
		bean.setDaggerAsteriskTypes(null);
		bean.setDaggerAsteriskVisible(true);
		bean.setEdit(true);
		bean.setEditable(true);
		bean.setEnglishEditable(true);
		bean.setEnglishVisible(true);
		bean.setErrorMessage("errorMessage");
		bean.setFrenchEditable(true);
		bean.setFrenchVisible(true);
		bean.setInvasivenessLevelEditable(true);
		bean.setInvasivenessLevelVisible(true);
		bean.setLockTimestamp(0l);

		bean.setNodeTitle("nodeTitle");
		bean.setReferenceLinksVisible(true);
		bean.setRemoveVisible(true);
		bean.setResetVisible(true);
		bean.setResult(null);
		bean.setSaveVisible(true);
		bean.setStatusEditable(true);
		bean.setStatusVisible(true);
		bean.setUserTitleEditable(true);
		bean.setUserTitleEng("userTitleEng");
		bean.setUserTitleFra("userTitleFra");
		assertTrue(bean.getAddBlockType() == TabularConceptType.ICD_BLOCK);
		assertTrue(bean.getAddCategoryType() == TabularConceptType.ICD_CATEGORY);
		assertTrue(bean.isAddCodeVisible());
		assertTrue(bean.getAddGroupType() == TabularConceptType.CCI_GROUP);
		assertTrue(bean.isAddQualifierVisible());
		assertTrue(bean.getAddType() == TabularConceptType.CCI_BLOCK);
		assertTrue(bean.isCanadianEnhancementEditable());
		assertTrue(bean.isCanadianEnhancementVisible());
		assertTrue(bean.getCciInvasivenessLevels() == null);
		assertTrue(bean.isChildTable());
		assertTrue(bean.isChildTableVisible());
		assertTrue(bean.getCode() != null);
		assertTrue(bean.isCodeEditable());
		assertTrue(bean.isCodeVisible());
		assertTrue(bean.isDaggerAsteriskEditable());
		assertTrue(bean.getDaggerAsteriskTypes() == null);
		assertTrue(bean.isDaggerAsteriskVisible());
		assertTrue(bean.isEdit());
		assertTrue(bean.isEditable());
		assertTrue(bean.isEnglishEditable());
		assertTrue(bean.isEnglishVisible());
		assertTrue(bean.getErrorMessage().equals("errorMessage"));
		assertTrue(bean.isFrenchEditable());
		assertTrue(bean.isFrenchVisible());
		assertTrue(bean.isInvasivenessLevelEditable());
		assertTrue(bean.isInvasivenessLevelVisible());
		assertTrue(bean.getLockTimestamp() == 0l);
		assertTrue(bean.getModel() != null);
		assertTrue(bean.getNodeTitle().equals("nodeTitle"));
		assertTrue(bean.isReferenceLinksVisible());
		assertTrue(bean.isRemoveVisible());
		assertTrue(bean.isResetVisible());
		assertTrue(bean.getResult() == null);
		assertTrue(bean.isSaveVisible());
		assertTrue(bean.isStatusEditable());
		assertTrue(bean.isStatusVisible());
		assertTrue(bean.isUserTitleEditable());
		assertTrue(bean.getUserTitleEng().equals("userTitleEng"));
		assertTrue(bean.getUserTitleFra().equals("userTitleFra"));

	}
}
