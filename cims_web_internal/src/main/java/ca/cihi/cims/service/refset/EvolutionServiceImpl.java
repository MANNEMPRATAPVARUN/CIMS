package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.ElementStatus;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.model.refset.BaseOutputContent;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.util.RefsetExportUtils;

/**
 * 
 * @author lzhu
 *
 */
public class EvolutionServiceImpl implements EvolutionService{

	private static final String EVOLUTION_REPORT_PREFIX = "EvolutionReportOf";
	private static final String TYPE_PICKLIST_COLUMN_EVOLUTION = "PicklistColumnEvolution";
	
	private static final String COMMON_TERM_RECORD = "Common Term Record";
	private static final String SUB_RECORD = "Sub-Record";
	private static final String CHANGE_TYPE_NEW = "New";
    private static final String CHANGE_TYPE_UPDATE = "Update";
	private static final String CHANGE_TYPE_REMOVED = "Removed";
    private static final String CHANGE_TYPE_DISABLED = "Disabled";
    
    private static final String RECORD_TYPE_MAIN = "MAIN";
    private static final String RECORD_TYPE_SUB = "SUB";
    private static final String ONE_SPACE = " ";
	
    private static final String CIMS_ICD_10_CA_CODE = "CIMS ICD-10-CA Code";
    private static final String CIMS_CCI_CODE = "CIMS CCI Code";
    
    private static final String ENGLISH_SHEET_NAME_PICKLIST_EVOLUTION = "Picklist Evolution"; 
    private static final String FRENCH_SHEET_NAME_PICKLIST_EVOLUTION = "Évolution de Liste de sélection";
    private static final String ENGLISH_SHEET_NAME_CONFIGURATION_EVOLUTION = "Configuration Evolution"; 
    private static final  String FRENCH_SHEET_NAME_CONFIGURATION_EVOLUTION = "Évolution de la configuration"; 
  
    private static final String ENGLISH_TITLE_SHEET_DESC = "Evolution Document"; 
    private static final String FRENCH_TITLE_SHEET_DESC = "Document d'évolution";
    
    private static final String[] ENGLISH_EVOLUTION_COLUMNS= {"Evolution Record Number","CIMS Classification Standard Code","Column Name","Old Value","New Value","Type of Change"};
    private static final String[] FRENCH_EVOLUTION_COLUMNS = {"Numéro de l’enregistrement de l’évolution","Code de la norme de classification CIMS","Titre de la colonne","Ancienne valeur","Nouvelle valeur","Type de changement"};
    private static final int EVOLUTION_TABLE_HEADER_ROW_NUM = 3;
    private static final int EVOLUTION_TABLE_DESC_ROW_NUM = 2;
    private static final int EVOLUTION_TABLE_LINK_ROW_NUM = 1;
    
    private static final String[] ENGLISH_CONFIG_COLUMNS = {"Column Name", "New or Deleted Column"};
    private static final String[] FRENCH_CONFIG_COLUMNS = {"Titre de la colonne", "Colonne nouvelle ou supprimée"};
    
    private static final String ENGLISH_EVOLUTION_TABLE_DESC = "Evolution table: Evolution of picklist from base version BASEVERSION to current version CURRENTVERSION";
    private static final String FRENCH_EVOLUTION_TABLE_DESC = "Le tableau d’évolution montre l’évolution de la liste de sélection, de la version BASEVERSION d’origine à la version CURRENTVERSION actuelle";
    
    private static final String ENGLISH_CONFIGURATION_TABLE_DESC = "Evolution of Picklist Structure";
    private static final String FRENCH_CONFIGURATION_TABLE_DESC = "Évolution de la structure de la liste de sélection";
    
    private static final String COLUMN_EVOLUTION_STATUS_CODE_NEW = "New";
    private static final String COLUMN_EVOLUTION_STATUS_CODE_DELETED = "Deleted";
    
    private static final String ENGLISH_COLUMN_EVOLUTION_STATUS_DESC_NEW = "New Column";
    private static final String FRENCH_COLUMN_EVOLUTION_STATUS_DESC_NEW = "Nouvelle colonne";
    
    private static final String ENGLISH_COLUMN_EVOLUTION_STATUS_DESC_DELETED = "Deleted Column";
    private static final String FRENCH_COLUMN_EVOLUTION_STATUS_DESC_DELETED = "Colonne supprimée";
    
    private static final String ENGLISH_UPDATE_SUB_RECORD = "Update Sub-Record";
    private static final String FRENCH_UPDATE_SUB_RECORD = "Mise à jour de l’enregistrement auxiliaire";
    private static final String ENGLISH_NEW_SUB_RECORD = "New Sub-Record";
    private static final String FRENCH_NEW_SUB_RECORD = "Nouvel enregistrement auxiliaire";
    private static final String ENGLISH_REMOVED_SUB_RECORD = "Removed Sub-Record";
    private static final String FRENCH_REMOVED_SUB_RECORD = "Suppression de l’enregistrement auxiliaire";
    private static final String ENGLISH_UPDATE_COMMON_TERM_RECORD = "Update Common Term Record";
    private static final String FRENCH_UPDATE_COMMON_TERM_RECORD = "Mise à jour de l’enregistrement terme commun";
    private static final String ENGLISH_NEW_COMMON_TERM_RECORD = "New Common Term Record";
    private static final String FRENCH_NEW_COMMON_TERM_RECORD = "Nouvel enregistrement terme commun";
    private static final String ENGLISH_REMOVED_COMMON_TERM_RECORD = "Removed Common Term Record";
    private static final String FRENCH_REMOVED_COMMON_TERM_RECORD = "Suppression de l’enregistrement terme commun";
    private static final String ENGLISH_DISABLED_COMMON_TERM_RECORD = "Disabled Common Term Record";
    private static final String FRENCH_DISABLED_COMMON_TERM_RECORD = "Désactivation de l’enregistrement terme commun";
    
    private static final String ENGLISH_PICKLIST_EVOLUTION_SHEET_DESC = "This tab contains a link back to the Table of contents, the data table’s name and description, and a data table that starts in cell A4 and ends in cell ENDCELL";
    private static final String FRENCH_PICKLIST_EVOLUTION_SHEET_DESC = "Cet onglet contient un lien de retour à la table des matières, le nom et la description du tableau de données ainsi qu’un tableau de données commençant à la cellule A4 pour se terminer à la cellule ENDCELL";
    
    private static final String NULL_VALUE = "Null";
    private static final String NA_VALUE = "NA";
    
    private static int TOTAL_SHEET_NUM = 2;
    
    private static Map<String, String> EVOLUTION_TABLE_DESC_MAP = new HashMap<String, String>();
    private static Map<String, String> SHEET_NAME_PICKLIST_EVOLUTION_MAP = new HashMap<String, String>();
    private static Map<String, String> SHEET_NAME_CONFIGURATION_EVOLUTION_MAP = new HashMap<String, String>();
    private static Map<String, String> TITLE_SHEET_DESC_MAP = new HashMap<String, String>();
    private static Map<String, String> CONFIGURATION_TABLE_DESC_MAP = new HashMap<String, String>();
    private static Map<String, String> COLUMN_EVOLUTION_STATUS_DESC_NEW_MAP = new HashMap<String, String>();
    private static Map<String, String> COLUMN_EVOLUTION_STATUS_DESC_DELETED_MAP = new HashMap<String, String>();
    private static Map<String, String[]> EVOLUTION_COLUMNS_MAP = new HashMap<String, String[]>();
    private static Map<String, String[]> CONFIG_COLUMNS_MAP = new HashMap<String, String[]>();
    
    private static Map<String, String> UPDATE_SUB_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> NEW_SUB_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> REMOVED_SUB_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> UPDATE_COMMON_TERM_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> NEW_COMMON_TERM_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> REMOVED_COMMON_TERM_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> DISABLED_COMMON_TERM_RECORD_MAP = new HashMap<String, String>();
    private static Map<String, String> PICKLIST_EVOLUTION_SHEET_DESC_MAP = new HashMap<String, String>();
    
    static {
    	EVOLUTION_TABLE_DESC_MAP.put(Language.ENG.getCode(), ENGLISH_EVOLUTION_TABLE_DESC);
    	EVOLUTION_TABLE_DESC_MAP.put(Language.FRA.getCode(), FRENCH_EVOLUTION_TABLE_DESC);
    	SHEET_NAME_PICKLIST_EVOLUTION_MAP.put(Language.ENG.getCode(), ENGLISH_SHEET_NAME_PICKLIST_EVOLUTION);
    	SHEET_NAME_PICKLIST_EVOLUTION_MAP.put(Language.FRA.getCode(), FRENCH_SHEET_NAME_PICKLIST_EVOLUTION);
    	SHEET_NAME_CONFIGURATION_EVOLUTION_MAP.put(Language.ENG.getCode(), ENGLISH_SHEET_NAME_CONFIGURATION_EVOLUTION);
    	SHEET_NAME_CONFIGURATION_EVOLUTION_MAP.put(Language.FRA.getCode(), FRENCH_SHEET_NAME_CONFIGURATION_EVOLUTION);
    	TITLE_SHEET_DESC_MAP.put(Language.ENG.getCode(), ENGLISH_TITLE_SHEET_DESC);
    	TITLE_SHEET_DESC_MAP.put(Language.FRA.getCode(), FRENCH_TITLE_SHEET_DESC);
    	CONFIGURATION_TABLE_DESC_MAP.put(Language.ENG.getCode(), ENGLISH_CONFIGURATION_TABLE_DESC);
    	CONFIGURATION_TABLE_DESC_MAP.put(Language.FRA.getCode(), FRENCH_CONFIGURATION_TABLE_DESC);
    	COLUMN_EVOLUTION_STATUS_DESC_NEW_MAP.put(Language.ENG.getCode(), ENGLISH_COLUMN_EVOLUTION_STATUS_DESC_NEW);
    	COLUMN_EVOLUTION_STATUS_DESC_NEW_MAP.put(Language.FRA.getCode(), FRENCH_COLUMN_EVOLUTION_STATUS_DESC_NEW);
    	COLUMN_EVOLUTION_STATUS_DESC_DELETED_MAP.put(Language.ENG.getCode(), ENGLISH_COLUMN_EVOLUTION_STATUS_DESC_DELETED);
    	COLUMN_EVOLUTION_STATUS_DESC_DELETED_MAP.put(Language.FRA.getCode(), FRENCH_COLUMN_EVOLUTION_STATUS_DESC_DELETED);
    	EVOLUTION_COLUMNS_MAP.put(Language.ENG.getCode(), ENGLISH_EVOLUTION_COLUMNS);
    	EVOLUTION_COLUMNS_MAP.put(Language.FRA.getCode(), FRENCH_EVOLUTION_COLUMNS);
    	CONFIG_COLUMNS_MAP.put(Language.ENG.getCode(), ENGLISH_CONFIG_COLUMNS);
    	CONFIG_COLUMNS_MAP.put(Language.FRA.getCode(), FRENCH_CONFIG_COLUMNS);
    	
    	UPDATE_SUB_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_UPDATE_SUB_RECORD);
    	UPDATE_SUB_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_UPDATE_SUB_RECORD);
    	NEW_SUB_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_NEW_SUB_RECORD);
    	NEW_SUB_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_NEW_SUB_RECORD);
    	REMOVED_SUB_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_REMOVED_SUB_RECORD);
    	REMOVED_SUB_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_REMOVED_SUB_RECORD);
    	UPDATE_COMMON_TERM_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_UPDATE_COMMON_TERM_RECORD);
    	UPDATE_COMMON_TERM_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_UPDATE_COMMON_TERM_RECORD);
    	NEW_COMMON_TERM_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_NEW_COMMON_TERM_RECORD);
    	NEW_COMMON_TERM_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_NEW_COMMON_TERM_RECORD);
    	REMOVED_COMMON_TERM_RECORD_MAP.put(Language.ENG.getCode(), ENGLISH_REMOVED_COMMON_TERM_RECORD);
    	REMOVED_COMMON_TERM_RECORD_MAP.put(Language.FRA.getCode(), FRENCH_REMOVED_COMMON_TERM_RECORD);
    	DISABLED_COMMON_TERM_RECORD_MAP.put(Language.ENG.getCode(),ENGLISH_DISABLED_COMMON_TERM_RECORD);
    	DISABLED_COMMON_TERM_RECORD_MAP.put(Language.FRA.getCode(),FRENCH_DISABLED_COMMON_TERM_RECORD);
    	
    	PICKLIST_EVOLUTION_SHEET_DESC_MAP.put(Language.ENG.getCode(),ENGLISH_PICKLIST_EVOLUTION_SHEET_DESC);
    	PICKLIST_EVOLUTION_SHEET_DESC_MAP.put(Language.FRA.getCode(),FRENCH_PICKLIST_EVOLUTION_SHEET_DESC);
    }
    
	@Override
	public boolean verifyPicklistOutputConfig(Long refsetContextId, Long elementId, Long elementVersionId, Long picklistElementId) {
		if (!isPicklistExistsInBaseVersion(refsetContextId,elementId,elementVersionId,picklistElementId)){
			return false;
		}
		List<PicklistColumnOutputDTO> pColumnOutputList = RefsetFactory.getPicklistColumnOutputConfig(refsetContextId,picklistElementId);
		for (PicklistColumnOutputDTO pColumnOutput:pColumnOutputList){
			Long columnElementId = pColumnOutput.getColumnId();
			ElementIdentifier elementIdentifier = new ElementIdentifier();
			elementIdentifier.setElementId(columnElementId);
			Column column = RefsetFactory.getColumn(refsetContextId, elementIdentifier, ConceptLoadDegree.REGULAR);
			String columnType = column.getColumnType();
			if (ColumnType.CCI_CODE.getColumnTypeDisplay().equals(columnType) || ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay().equals(columnType)
					|| ColumnType.ICD10CA_CODE.getColumnTypeDisplay().equals(columnType) || ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay().equals(columnType) ){
				return true;
			}
		}
		return false;
	}
	
	private boolean isPicklistExistsInBaseVersion(Long refsetContextId,  Long elementId, Long elementVersionId, Long picklistElementId){
		Long baseContextId = Context.findById(refsetContextId).getBaseContextId();
		if (baseContextId==null){
			return false;
		}
		//Context baseContext = Context.findById(baseContextId);
		//ElementIdentifier elementIdentifier = baseContext.getContextElementIdentifier();
		ElementIdentifier elementIdentifier = new ElementIdentifier();
		elementIdentifier.setElementId(elementId);
		elementIdentifier.setElementVersionId(elementVersionId);
		Refset refset = RefsetFactory.getRefset(baseContextId, elementIdentifier, ConceptLoadDegree.REGULAR);
		return refset.listPickLists().stream().anyMatch(t -> t.getElementIdentifier().getElementId().equals(picklistElementId));
	}

	public List<PicklistColumnEvolutionResultDTO> getPicklistColumnEvolutionList(PicklistColumnEvolutionRequestDTO request) {
		return RefsetFactory.getPicklistColumnEvolutionList(request);
	}

	@Override
	public BaseOutputContent getPicklistColumnEvolutionContent(PicklistColumnEvolutionRequestDTO request) {
		List<PicklistColumnEvolutionResultDTO> evolutionList = getPicklistColumnEvolutionList(request);
		
		BaseOutputContent outputContent = new BaseOutputContent();
		PicklistOutputDTO pickListOutput =  RefsetFactory.getPicklistOutputConfigByOutputId(request.getPicklistOutputId().intValue());
		outputContent.setFileName(EVOLUTION_REPORT_PREFIX+pickListOutput.getName()); 
		outputContent.setOutputType(TYPE_PICKLIST_COLUMN_EVOLUTION);
		XSSFWorkbook wb = new XSSFWorkbook();
		outputContent.setWorkbook(wb);
		String languageCode = pickListOutput.getLanguageCode();
		List<PicklistColumnEvolutionResultDTO> newList = completeList(evolutionList,languageCode,request.getCciContextId(),request.getIcd10caContextId());
		
		createTitleSheet(TITLE_SHEET_DESC_MAP.get(languageCode), wb, languageCode, TOTAL_SHEET_NUM);
		createTableOfContentSheet(wb,languageCode);
		createPickListEvolutionSheet(wb, newList, request.getVersionCode(), request.getBaseVersionCode(),languageCode);
		
		List<PicklistColumnConfigEvolutionDTO> columnConfigList = RefsetFactory.getPicklistColumnConfigEvolutionList(request);
		createConfigEvolutionSheet(wb, columnConfigList, languageCode);
		return outputContent;
	}
	
	private void createConfigEvolutionSheet(XSSFWorkbook wb, List<PicklistColumnConfigEvolutionDTO> columnConfigList, String languageCode){		
		Sheet configEvolution = wb.createSheet(SHEET_NAME_CONFIGURATION_EVOLUTION_MAP.get(languageCode));
		configEvolution.setDisplayGridlines(false);
		createBackToTableContentRow(wb,configEvolution,EVOLUTION_TABLE_LINK_ROW_NUM,languageCode);
		createOneColumnRow(configEvolution,EVOLUTION_TABLE_DESC_ROW_NUM,CONFIGURATION_TABLE_DESC_MAP.get(languageCode));
		int rowNum = createConfigEvolutionTableHeader(configEvolution,EVOLUTION_TABLE_HEADER_ROW_NUM,languageCode);
		rowNum = createConfigEvolutionTableContent(configEvolution,rowNum,columnConfigList,languageCode);
		String sheetDesc = PICKLIST_EVOLUTION_SHEET_DESC_MAP.get(languageCode).replace("ENDCELL", "B"+rowNum);
	    createSheetDesc(wb, configEvolution, sheetDesc);
	}
	
	private void createPickListEvolutionSheet(XSSFWorkbook wb, List<PicklistColumnEvolutionResultDTO> newList, String versionCode, String baseVersionCode, String languageCode){		
        Sheet pickListEvolution = wb.createSheet(SHEET_NAME_PICKLIST_EVOLUTION_MAP.get(languageCode));
        pickListEvolution.setDisplayGridlines(false);
        createBackToTableContentRow(wb,pickListEvolution,EVOLUTION_TABLE_LINK_ROW_NUM,languageCode);
        createPickListEvolutionDesc(pickListEvolution,EVOLUTION_TABLE_DESC_ROW_NUM,versionCode,baseVersionCode,languageCode);
        int rowNum = createPickListEvolutionTableHeader(pickListEvolution,EVOLUTION_TABLE_HEADER_ROW_NUM,languageCode);
        rowNum = createPickListEvolutionTableContent(pickListEvolution,rowNum,newList,languageCode);
        String sheetDesc = PICKLIST_EVOLUTION_SHEET_DESC_MAP.get(languageCode).replace("ENDCELL", "F"+rowNum);
        createSheetDesc(wb, pickListEvolution, sheetDesc);
	}
	
	public void createTableOfContentSheet(XSSFWorkbook wb, String languageCode){
		Sheet tableContent = wb.createSheet(RefsetExportUtils.getTableContentByLanguageCode(languageCode));
		tableContent.setDisplayGridlines(false);
        Row row = tableContent.createRow((short) 0);
	    Cell cell = row.createCell(0);
	    cell.setCellValue(RefsetExportUtils.getTableContentDescByLanguageCode(languageCode));
	    CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("Calibri");

        headerStyle.setFont(headerFont);
        cell.setCellStyle(headerStyle);

        Row row2 = tableContent.createRow((short) 1);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue(RefsetExportUtils.getTableContentByLanguageCode(languageCode));

        CellStyle headerStyle2 = wb.createCellStyle();

        Font headerFont2 = wb.createFont();
        headerFont2.setFontHeightInPoints((short) 11);
        headerFont2.setFontName("Calibri");

        headerStyle2.setFont(headerFont2);
        cell2.setCellStyle(headerStyle2);
        
        createSingleColumnLinkRow(wb,tableContent,3,SHEET_NAME_PICKLIST_EVOLUTION_MAP.get(languageCode));
        createSingleColumnLinkRow(wb,tableContent,4,SHEET_NAME_CONFIGURATION_EVOLUTION_MAP.get(languageCode));
	}
	
	public void createTitleSheet(String titleContent, XSSFWorkbook wb,
            String languageCode, int exportSheetNum) {
        String titleTab = RefsetExportUtils.getTitleNameByLanguageCode(languageCode);

        Sheet sheet = wb.createSheet(titleTab);
        sheet.setDisplayGridlines(false);
        Row row = sheet.createRow((short) 0);
        Cell cell = row.createCell(0);

        String title = RefsetExportUtils.getTitleDescriptionByLanguageCode(languageCode);

        cell.setCellValue(title.replaceAll("NUMOFSHEETS", String.valueOf(exportSheetNum)));

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("Calibri");

        headerStyle.setFont(headerFont);
        cell.setCellStyle(headerStyle);

        Row row2 = sheet.createRow((short) 1);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue(titleContent);

        CellStyle headerStyle2 = wb.createCellStyle();

        Font headerFont2 = wb.createFont();
        headerFont2.setFontHeightInPoints((short) 26);
        headerFont2.setFontName("Calibri");

        headerStyle2.setFont(headerFont2);
        cell2.setCellStyle(headerStyle2);
    }
	
	private void createSheetDesc(XSSFWorkbook wb, Sheet sheet, String title){
        Row row = sheet.createRow((short) 0);
        Cell cell = row.createCell(0);
        
        cell.setCellValue(title);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("Calibri");

        headerStyle.setFont(headerFont);
        cell.setCellStyle(headerStyle);
	}
	
	private void createBackToTableContentRow(XSSFWorkbook wb, Sheet sheet, int rowNum, String languageCode){
		createSingleColumnLinkRow(wb,sheet,rowNum,RefsetExportUtils.getTableContentByLanguageCode(languageCode));
	}
	
	private void createSingleColumnLinkRow(XSSFWorkbook wb, Sheet sheet, int rowNum, String rowData){
		Row row2 = sheet.createRow(rowNum);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue(rowData);

        CreationHelper createHelper = wb.getCreationHelper();

        Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
        link.setAddress("'" + rowData + "'!A1");
        cell2.setHyperlink(link);

        CellStyle hlinkStyle = wb.createCellStyle();
        Font hlinkFont = wb.createFont();
        hlinkFont.setUnderline(Font.U_SINGLE);
        hlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlinkFont);

        cell2.setCellStyle(hlinkStyle);
	}
	
	private void createPickListEvolutionDesc(Sheet pickListEvolution, int rowNum, String versionCode, String baseVersionCode,String languageCode){
		String desc = EVOLUTION_TABLE_DESC_MAP.get(languageCode);
		Row row = pickListEvolution.createRow(rowNum);
		Cell cell = row.createCell(0);
		cell.setCellValue(desc.replaceAll("BASEVERSION", baseVersionCode).replaceAll("CURRENTVERSION", versionCode));
	}
	
	private void createOneColumnRow(Sheet sheet, int rowNum, String value){
		Row row = sheet.createRow(rowNum);
		Cell cell = row.createCell(0);
		cell.setCellValue(value);
	}
	
	private int createPickListEvolutionTableHeader(Sheet pickListEvolution, int rowNum,String languageCode){
		String[] columns = EVOLUTION_COLUMNS_MAP.get(languageCode);
		Row tblHeader = pickListEvolution.createRow(rowNum);
		CellStyle tableHeaderstyle = applyTableHeaderStyle(pickListEvolution);
		int colNum = 0;
		for (int i=0; i<columns.length; i++){	
			Cell tblHeaderColumnCell = tblHeader.createCell(colNum, Cell.CELL_TYPE_STRING);
			tblHeaderColumnCell.setCellValue(columns[i]);
			tblHeaderColumnCell.setCellStyle(tableHeaderstyle);
			if (i<2){
				pickListEvolution.setColumnWidth(colNum, 255*(columns[i].length()+2));
			}else{
				pickListEvolution.setColumnWidth(colNum, 255*(columns[i].length()+15));
			}
			colNum++;
		}
		return ++rowNum;
	}
	
	private int createConfigEvolutionTableHeader(Sheet sheet, int rowNum,String languageCode){
		String[] columns = CONFIG_COLUMNS_MAP.get(languageCode);
		Row tblHeader = sheet.createRow(rowNum);
		CellStyle tableHeaderstyle = applyTableHeaderStyle(sheet);
		int colNum = 0;
		for (int i=0; i<columns.length; i++){	
			Cell tblHeaderColumnCell = tblHeader.createCell(colNum, Cell.CELL_TYPE_STRING);
			tblHeaderColumnCell.setCellValue(columns[i]);
			tblHeaderColumnCell.setCellStyle(tableHeaderstyle);
			if (i==0){
				sheet.setColumnWidth(colNum, 255*(columns[i].length()+15));
			}else {
				sheet.setColumnWidth(colNum, 255*(columns[i].length()+5));
			}
			colNum++;
		}
		return ++rowNum;
	}
	
	private int createPickListEvolutionTableContent(Sheet pickListEvolution, int rowNum, List<PicklistColumnEvolutionResultDTO> newList,String languageCode){
		int evolutionRecordNum = 0;
		CellStyle cellStyle = applyTableContentStyle(pickListEvolution); 
		String tmpConceptCode = "";
		for (PicklistColumnEvolutionResultDTO evolutionResult:newList){		
			int columnNum = 0;
			if (!evolutionResult.getConceptCode().equals(tmpConceptCode)){
				evolutionRecordNum++;
				tmpConceptCode = evolutionResult.getConceptCode();
			}
			Row row = pickListEvolution.createRow(rowNum);
			columnNum = createColumn(row,columnNum,Integer.toString(evolutionRecordNum),cellStyle);
			columnNum = createColumn(row,columnNum,evolutionResult.getConceptCode(),cellStyle);
			columnNum = createColumn(row,columnNum,evolutionResult.getColumnName(),cellStyle);
			columnNum = createColumn(row,columnNum,evolutionResult.getOldValue(),cellStyle);
			columnNum = createColumn(row,columnNum,evolutionResult.getNewValue(),cellStyle);
			columnNum = createColumn(row,columnNum,evolutionResult.getChangeTypeDesc(),cellStyle);
			rowNum++;
		}
		return rowNum;
	}
	
	private int createConfigEvolutionTableContent(Sheet sheet, int rowNum, List<PicklistColumnConfigEvolutionDTO> resultList,String languageCode){
		CellStyle cellStyle = applyTableContentStyle(sheet); 
		for (PicklistColumnConfigEvolutionDTO columnEvolution:resultList){		
			int columnNum = 0;			
			Row row = sheet.createRow(rowNum);
			columnNum = createColumn(row,columnNum,columnEvolution.getColumnName(),cellStyle);
			String columnStatus = "";
			if (COLUMN_EVOLUTION_STATUS_CODE_NEW.equals(columnEvolution.getStatus())){
				columnStatus = COLUMN_EVOLUTION_STATUS_DESC_NEW_MAP.get(languageCode);
			}else if (COLUMN_EVOLUTION_STATUS_CODE_DELETED.equals(columnEvolution.getStatus())){
				columnStatus = COLUMN_EVOLUTION_STATUS_DESC_DELETED_MAP.get(languageCode);
			}
			columnNum = createColumn(row,columnNum,columnStatus,cellStyle);
			rowNum++;
		}
		return rowNum;
	}
	
	private int createColumn(Row row, int columnNum, String value, CellStyle cellStyle){
		Cell cell = row.createCell(columnNum, Cell.CELL_TYPE_STRING);
		cell.setCellStyle(cellStyle);
		if (value==null || value.trim().isEmpty()){
			value = NA_VALUE;
		}
		cell.setCellValue(value);
		return ++columnNum;		
	}
	
	/**
	 * 
	 * @param sheet
	 * @return
	 */
	private CellStyle applyTableHeaderStyle(Sheet sheet) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.WHITE.index);		
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		applyCellBorder(style);
		
		Font font = sheet.getWorkbook().createFont();
		font.setColor(IndexedColors.BLACK.index);
		font.setFontHeightInPoints((short) 11);
		font.setFontName("Calibri");
		font.setBold(true);
		
		style.setFont(font);
		return style;
	}
	
	private CellStyle applyTableContentStyle(Sheet sheet) {
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);		
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		applyCellBorder(cellStyle);
	    //cellStyle.setLocked(false);
		
		Font font = sheet.getWorkbook().createFont();
		font.setColor(IndexedColors.BLACK.index);
		font.setFontHeightInPoints((short) 11);
		font.setFontName("Calibri");
		
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	private void applyCellBorder(CellStyle cellStyle){
	    cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	}
	
	private List<PicklistColumnEvolutionResultDTO> completeList(List<PicklistColumnEvolutionResultDTO> originalList, String languageCode,Long cciContextId, Long icd10caContextid){
		List<String> disabledCodeList =  new ArrayList<String>();
		List<PicklistColumnEvolutionResultDTO> resultList =  new ArrayList<PicklistColumnEvolutionResultDTO>();
		for (PicklistColumnEvolutionResultDTO picklistColumnEvolutionResult : originalList){
			if (ElementStatus.ACTIVE.getStatus().equals(picklistColumnEvolutionResult.getNewStatus())){
				if (ElementStatus.ACTIVE.getStatus().equals(picklistColumnEvolutionResult.getOldStatus())){
					String newValue = picklistColumnEvolutionResult.getNewValue();
					String oldValue = picklistColumnEvolutionResult.getOldValue();
					if (newValue==null) {
						newValue=StringUtils.EMPTY;
					}
					if (oldValue==null){
						oldValue=StringUtils.EMPTY;
					}
					if (!newValue.equals(oldValue)){
						if (picklistColumnEvolutionResult.getRecordType().equals(RECORD_TYPE_MAIN)){
							picklistColumnEvolutionResult.setChangeTypeDesc(UPDATE_COMMON_TERM_RECORD_MAP.get(languageCode));
						}else {
							picklistColumnEvolutionResult.setChangeTypeDesc(UPDATE_SUB_RECORD_MAP.get(languageCode));
						}
						picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_UPDATE);
						resultList.add(picklistColumnEvolutionResult);
					}
				}else {
					if (picklistColumnEvolutionResult.getRecordType().equals(RECORD_TYPE_MAIN)){
						picklistColumnEvolutionResult.setChangeTypeDesc(NEW_COMMON_TERM_RECORD_MAP.get(languageCode));
						picklistColumnEvolutionResult.setOldValue(NULL_VALUE);
					}else {
						picklistColumnEvolutionResult.setChangeTypeDesc(NEW_SUB_RECORD_MAP.get(languageCode));
						picklistColumnEvolutionResult.setOldValue(NULL_VALUE);
					}
					picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_NEW);
					resultList.add(picklistColumnEvolutionResult);
				}
			} else if (ElementStatus.REMOVED.getStatus().equals(picklistColumnEvolutionResult.getNewStatus())){
				if (ElementStatus.ACTIVE.getStatus().equals(picklistColumnEvolutionResult.getOldStatus())){	
					if (picklistColumnEvolutionResult.getRecordType().equals(RECORD_TYPE_MAIN)){
						if (isClassificationStandardColumn(picklistColumnEvolutionResult.getColumnType())){
							if(isCodeDisabled(picklistColumnEvolutionResult.getConceptCode(),icd10caContextid, cciContextId)){
								picklistColumnEvolutionResult.setChangeTypeDesc(DISABLED_COMMON_TERM_RECORD_MAP.get(languageCode));
								picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_DISABLED);
								disabledCodeList.add(picklistColumnEvolutionResult.getConceptCode());
							}else {
								picklistColumnEvolutionResult.setChangeTypeDesc(REMOVED_COMMON_TERM_RECORD_MAP.get(languageCode));
								picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_REMOVED);
							}
						}
						else {
							picklistColumnEvolutionResult.setChangeTypeDesc(REMOVED_COMMON_TERM_RECORD_MAP.get(languageCode));
							picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_REMOVED);
						}
					}else {
						picklistColumnEvolutionResult.setChangeTypeDesc(REMOVED_SUB_RECORD_MAP.get(languageCode));
						picklistColumnEvolutionResult.setRecordStatus(CHANGE_TYPE_REMOVED);
					}
					picklistColumnEvolutionResult.setNewValue(NULL_VALUE);
					resultList.add(picklistColumnEvolutionResult);
				}
			}
		}
		List<PicklistColumnEvolutionResultDTO> cleanList = removeDupItems(resultList);
		List<PicklistColumnEvolutionResultDTO> fianalList = removeDisabledCodeItems(cleanList,disabledCodeList);
		return fianalList;
	}
	
	private boolean isClassificationStandardColumn(String columnType){
		if (CIMS_ICD_10_CA_CODE.equals(columnType) || CIMS_CCI_CODE.equals(columnType) ){
			return true;
		}else {
			return false;
		}		
	}
	
	private List<PicklistColumnEvolutionResultDTO> removeDisabledCodeItems(List<PicklistColumnEvolutionResultDTO> originalList, List<String> disabledCodeList){
		List<PicklistColumnEvolutionResultDTO> resultList =  new ArrayList<PicklistColumnEvolutionResultDTO>();
		for (PicklistColumnEvolutionResultDTO picklistColumnEvolutionResult: originalList){
			resultList.add(picklistColumnEvolutionResult);
		}
		
		for (PicklistColumnEvolutionResultDTO picklistColumnEvolutionResult: originalList){
			for (String code:disabledCodeList){
				if (picklistColumnEvolutionResult.getConceptCode().equals(code) && !isClassificationStandardColumn(picklistColumnEvolutionResult.getColumnType())){
					resultList.remove(picklistColumnEvolutionResult);
				}
			}
		}
		return resultList;
	}
	
	
	private List<PicklistColumnEvolutionResultDTO> removeDupItems(List<PicklistColumnEvolutionResultDTO> originalList){
		List<PicklistColumnEvolutionResultDTO> resultList =  new ArrayList<PicklistColumnEvolutionResultDTO>();
		for (PicklistColumnEvolutionResultDTO picklistColumnEvolutionResult:originalList){
			resultList.add(picklistColumnEvolutionResult);
		}
		
		List<PicklistColumnEvolutionResultDTO> statusNewList =  new ArrayList<PicklistColumnEvolutionResultDTO>(); 
		List<PicklistColumnEvolutionResultDTO> statusRemovedList =  new ArrayList<PicklistColumnEvolutionResultDTO>(); 
		for (PicklistColumnEvolutionResultDTO picklistColumnEvolutionResult : originalList){
			if (CHANGE_TYPE_NEW.equals(picklistColumnEvolutionResult.getRecordStatus())){
				statusNewList.add(picklistColumnEvolutionResult);
			}else if (CHANGE_TYPE_REMOVED.equals(picklistColumnEvolutionResult.getRecordStatus())){
				statusRemovedList.add(picklistColumnEvolutionResult);
			}
		}
		for (PicklistColumnEvolutionResultDTO removedItem : statusRemovedList){
			for (PicklistColumnEvolutionResultDTO newItem : statusNewList){
				if (removedItem.equals(newItem)){
					resultList.remove(removedItem);
					resultList.remove(newItem);
				}
			}
		}
		
		return resultList;
	}
	
	private boolean isCodeDisabled(String code, Long icd10caContextid, Long cciContextId){
		if (ElementStatus.DISABLED.getStatus().equals(RefsetFactory.getConceptStatus(code,icd10caContextid,cciContextId))){
			return true;
		}
		return false;
	}

	@Override
	public List<PicklistColumnConfigEvolutionDTO> getPicklistColumnConfigEvolutionList(
			PicklistColumnEvolutionRequestDTO request) {
		return RefsetFactory.getPicklistColumnConfigEvolutionList(request);
	}
	
}
