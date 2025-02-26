package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.model.refset.RefsetExport;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.util.RefsetExportUtils;
import ca.cihi.cims.web.bean.refset.RecordViewBean;

@Component
public class PicklistRefsetExportService implements RefsetExportService {
	/**
	 * Column Output Start Row Number.
	 */
	private static final short COLUMN_OUTPUT_START_ROW = 3;

	/**
	 * Processor Type.
	 */
	public static final String PROCESSOR_TYPE = "picklist";

	/**
	 * Reference to picklist service.
	 */
	@Autowired
	private PicklistService picklistService;

	@Override
	public String getSheetName(RefsetOutputConfiguration refsetOutputConfiguration) {
		PicklistOutputDTO picklistOutput = RefsetFactory
		        .getPicklistOutputConfigByOutputId(refsetOutputConfiguration.getOutputId().intValue());

		if (picklistOutput == null) {
			return null;
		}

		return WorkbookUtil.createSafeSheetName(picklistOutput.getTabName().trim()).replaceAll("\\s+", "_")
		        .replaceAll("[\\t\\n\\r]", "");
	}

	@Override
	public String getType() {
		return PROCESSOR_TYPE;
	}

	@Override
	public String processExport(RefsetOutputConfiguration refsetOutputConfiguration, Workbook wb, String sheetName,
	        String languageCode, boolean backToTableContent) {
		List<PicklistColumnOutputDTO> picklistColumnOutput = RefsetFactory
		        .getPicklistColumnOutputConfigById(refsetOutputConfiguration.getOutputId().intValue());
		XSSFSheet sheet = null;
		String exportSheetName = sheetName;

		try {
			sheet = (XSSFSheet) wb.createSheet(exportSheetName);
		} catch (Exception e) {
			exportSheetName = exportSheetName + "_" + ((new Random()).nextInt(900) + 100);

			sheet = (XSSFSheet) wb.createSheet(exportSheetName);
		}

		if (picklistColumnOutput == null) {
			return exportSheetName;
		}

		RecordViewBean recordViewBean = new RecordViewBean();

		recordViewBean.setContextId(refsetOutputConfiguration.getRefsetContextId());
		recordViewBean.setContainerElementId(refsetOutputConfiguration.getId());
		recordViewBean.setContainerSublist(false);

		List<LightRecord> columnValueList = picklistService.listRecords(recordViewBean);

		if (columnValueList == null) {
			return exportSheetName;
		}

		Collections.reverse(columnValueList);

		if (backToTableContent) {
			Row row2 = sheet.createRow((short) 1);
			Cell cell2 = row2.createCell(0);
			cell2.setCellValue(RefsetExportUtils.getBackToTableContentByLanguageCode(languageCode));

			CreationHelper createHelper = wb.getCreationHelper();

			Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
			link.setAddress("'" + RefsetExportUtils.getTableContentByLanguageCode(languageCode) + "'!A1");
			cell2.setHyperlink(link);

			CellStyle hlinkStyle = wb.createCellStyle();
			Font hlinkFont = wb.createFont();
			hlinkFont.setUnderline(Font.U_SINGLE);
			hlinkFont.setColor(IndexedColors.BLUE.getIndex());
			hlinkStyle.setFont(hlinkFont);

			cell2.setCellStyle(hlinkStyle);
		}

		PicklistOutputDTO picklistOutput = RefsetFactory
		        .getPicklistOutputConfigByOutputId(refsetOutputConfiguration.getOutputId().intValue());

		if (picklistOutput != null) {
			Row row3 = sheet.createRow((short) 2);
			Cell cell3 = row3.createCell(0);
			cell3.setCellValue(picklistOutput.getTabName() + ": " + picklistOutput.getTableName());

			CellStyle headerStyle2 = wb.createCellStyle();

			Font headerFont2 = wb.createFont();
			headerFont2.setFontHeightInPoints((short) 8);
			headerFont2.setFontName("Arial");

			headerStyle2.setFont(headerFont2);
			cell3.setCellStyle(headerStyle2);
		}

		CellStyle headerStyle3 = wb.createCellStyle();
		headerStyle3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		headerStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Font headerFont3 = wb.createFont();
		headerFont3.setColor(IndexedColors.WHITE.getIndex());
		headerFont3.setFontHeightInPoints((short) 11);
		headerFont3.setFontName("Calibri");
		headerFont3.setBold(true);

		headerStyle3.setFont(headerFont3);

		XSSFRow row = sheet.createRow(COLUMN_OUTPUT_START_ROW);

		int i = 0;
		Map<Integer, PicklistColumnOutputDTO> columnIdMap = new HashMap<Integer, PicklistColumnOutputDTO>();

		for (PicklistColumnOutputDTO p : picklistColumnOutput) {
			columnIdMap.put(p.getPickListColumnOutputId(), p);

			/**
			 * Don't include sublist column in the export.
			 */
			if (p.getDisplayModeCode() != null) {
				continue;
			}

			XSSFCell cell = row.createCell(i);

			ElementIdentifier elementIdentifier = new ElementIdentifier();
			elementIdentifier.setElementId(p.getColumnId());

			Column column = RefsetFactory.getColumn(p.getRefsetContextId(), elementIdentifier,
			        ConceptLoadDegree.MINIMAL);

			cell.setCellValue(column.getColumnName());
			cell.setCellStyle(headerStyle3);
			sheet.autoSizeColumn(cell.getColumnIndex());

			i++;
		}

		String lastColumnLetter = CellReference.convertNumToColString(i - 1);
		int lastColumnNumber = i;

		List<PicklistColumnOutputDTO> expandSublist = new ArrayList<PicklistColumnOutputDTO>();
		Long expandSublistColumnElementId = null;
		Map<Long, PicklistColumnOutputDTO> collapseSublist = new HashMap<Long, PicklistColumnOutputDTO>();

		for (PicklistColumnOutputDTO p : picklistColumnOutput) {
			if (p.getParentPickListColumnOutputId() == null) {
				continue;
			}

			PicklistColumnOutputDTO parent = columnIdMap.get(p.getParentPickListColumnOutputId());

			if (parent == null) {
				continue;
			}

			if ("EXP".equals(parent.getDisplayModeCode())) {
				expandSublist.add(p);

				expandSublistColumnElementId = parent.getColumnId();
			}

			if ("CLP".equals(parent.getDisplayModeCode())) {
				collapseSublist.put(parent.getColumnId(), p);
			}
		}

		Map<Long, List<RefsetExport>> cellValues = new HashMap<Long, List<RefsetExport>>();

		for (LightRecord r : columnValueList) {
			Map<Long, ValueDTO> values = r.getValues();

			List<RefsetExport> rList = new ArrayList<RefsetExport>();

			cellValues.put(r.getRecordIdentifier().getElementId(), rList);

			for (PicklistColumnOutputDTO p : picklistColumnOutput) {
				if (p.getParentPickListColumnOutputId() != null) {
					RefsetExport refsetExport = new RefsetExport();

					refsetExport.setOrderNumber(p.getOrderNumber());
					refsetExport.setColumnId(p.getColumnId());
					refsetExport.setSublistColumn(true);

					rList.add(refsetExport);

					continue;
				}

				if (p.getDisplayModeCode() != null) {
					continue;
				}

				ValueDTO v = values.get(p.getColumnId());

				RefsetExport refsetExport = new RefsetExport();

				refsetExport.setCellValue(v != null && v.getTextValue() != null ? v.getTextValue().trim() : "");
				refsetExport.setCellWrapText(false);
				refsetExport.setOrderNumber(p.getOrderNumber());
				refsetExport.setColumnId(p.getColumnId());
				refsetExport.setSublistColumn(false);

				rList.add(refsetExport);
			}
		}

		List<List<RefsetExport>> exportList = new ArrayList<List<RefsetExport>>();

		if (!collapseSublist.isEmpty()) {
			for (LightRecord r : columnValueList) {
				List<RefsetExport> exportRow = cellValues.get(r.getRecordIdentifier().getElementId());
				List<RefsetExport> newRow = new ArrayList<RefsetExport>();

				for (RefsetExport export : exportRow) {
					for (Map.Entry<Long, PicklistColumnOutputDTO> collapse : collapseSublist.entrySet()) {
						PicklistColumnOutputDTO p = collapse.getValue();

						if (p.getColumnId() != export.getColumnId()) {
							newRow.add(export);

							continue;
						}

						List<String> collpaseValue = getSublistCollapseValue(
						        refsetOutputConfiguration.getRefsetContextId(), r.getRecordIdentifier().getElementId(),
						        collapse.getKey(), p);

						RefsetExport newRefsetExport = new RefsetExport();

						StringBuffer sb = new StringBuffer();

						for (String s : collpaseValue) {
							if (sb.length() == 0) {
								sb.append(s);

								continue;
							}

							sb.append("\n");
							sb.append(s);
						}

						newRefsetExport.setCellValue(sb.toString());
						newRefsetExport.setCellWrapText(true);
						newRefsetExport.setOrderNumber(p.getOrderNumber());
						newRefsetExport.setColumnId(p.getColumnId());
						newRefsetExport.setNumCollapseValues(collpaseValue != null ? collpaseValue.size() : 0);

						newRow.add(newRefsetExport);
					}
				}

				cellValues.put(r.getRecordIdentifier().getElementId(), newRow);
			}
		}

		for (LightRecord r : columnValueList) {
			List<RefsetExport> rows = cellValues.get(r.getRecordIdentifier().getElementId());

			if (expandSublistColumnElementId != null) {
				List<List<RefsetExport>> expandRows = getExpandRecordExport(
				        refsetOutputConfiguration.getRefsetContextId(), r.getRecordIdentifier().getElementId(),
				        expandSublistColumnElementId, rows, expandSublist);

				if (expandRows != null) {
					exportList.addAll(expandRows);
				}

				continue;
			}

			exportList.add(rows);
		}

		i = 1;

		if (getNumberTables(wb) == 0) {
			XSSFTable pTable = sheet.createTable();
			CTTable ctTable = pTable.getCTTable();
			AreaReference ctDataRange = new AreaReference(new CellReference(3, 0),
			        new CellReference(3 + exportList.size(), lastColumnNumber - 1));

			ctTable.setRef(ctDataRange.formatAsString());
			ctTable.setDisplayName("picklistTable");
			ctTable.setName("picklistTable");
			ctTable.setId(1);

			CTTableColumns columns = ctTable.addNewTableColumns();
			columns.setCount(lastColumnNumber);

			for (int m = 0; m < columns.getCount(); m++) {
				CTTableColumn column = columns.addNewTableColumn();

				column.setName("Column");
				column.setId(m + 1);
			}
		}

		CellStyle cellStyle = wb.createCellStyle();

		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setLocked(false);
		int maxColumnIndex=0;//max used column in the sheet required for autoformat later
		for (List<RefsetExport> re : exportList) {
			
		    Collections.sort(re);	
					
			List<RefsetExport> dataList = new ArrayList<RefsetExport>(new LinkedHashSet<RefsetExport>(re));

			XSSFRow valueRow = sheet.createRow(COLUMN_OUTPUT_START_ROW + i);
			int numberOfCollapseValues = 1;

			int k = 0;

			for (RefsetExport rx : dataList) {
				XSSFCell cell = valueRow.createCell(k);

				cell.setCellValue(
				        rx.getCellValue() != null && rx.getCellValue().trim().length() > 0 ? rx.getCellValue() : "N/A");

				if (rx.isCellWrapText()) {
					cellStyle.setWrapText(true);

					numberOfCollapseValues = numberOfCollapseValues > rx.getNumCollapseValues() ? numberOfCollapseValues
					        : rx.getNumCollapseValues();
				}

				cell.setCellStyle(cellStyle);
				maxColumnIndex=cell.getColumnIndex()>maxColumnIndex?cell.getColumnIndex():maxColumnIndex;

				k++;
			}

			valueRow.setHeightInPoints(numberOfCollapseValues * sheet.getDefaultRowHeightInPoints());
			i++;
		}
		autoSizeColumn(sheet, maxColumnIndex);
		int lastRowNumber = COLUMN_OUTPUT_START_ROW + i;

		if (backToTableContent) {
			String summary = RefsetExportUtils.getSummaryByLanguageCode(languageCode);

			Row first = sheet.createRow((short) 0);
			Cell cell = first.createCell(0);
			cell.setCellValue(summary.replaceAll("LASTCELL", lastColumnLetter + lastRowNumber));

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

		if (!exportList.isEmpty()) {
			sheet.createFreezePane(0, 4);
		}

		Name name = wb.createName();
		name.setNameName(exportSheetName + (new Random()).nextInt(90000));
		String reference = "'" + exportSheetName + "'!A1";
		name.setRefersToFormula(reference);

		return exportSheetName;
	}

	
	/**
     * Formats the size for the worksheet upto the maxColumn Parameter
     * @param sheet
     * @param maxColumnIndex
     */
    private void autoSizeColumn(XSSFSheet sheet, int maxColumnIndex) {
    	
    	//System.out.println("maxColumnIndex "+maxColumnIndex);
    	for(int i=0;i<=maxColumnIndex;i++)
    	{
    		sheet.autoSizeColumn(i);
    	}
		
	}
	
	private List<List<RefsetExport>> getExpandRecordExport(Long contextId, Long recordId, Long columnElementId,
	        List<RefsetExport> commonTermList, List<PicklistColumnOutputDTO> expandSublist) {
		RecordViewBean rvb = new RecordViewBean();

		rvb.setContextId(contextId);
		rvb.setContainerElementId(columnElementId);
		rvb.setRecordElementId(recordId);
		rvb.setContainerSublist(true);

		List<LightRecord> cvList = picklistService.listRecords(rvb);

		List<List<RefsetExport>> expandExport = new ArrayList<List<RefsetExport>>();

		if (cvList == null || cvList.isEmpty()) {
			expandExport.add(commonTermList);

			return expandExport;
		}

		Collections.reverse(cvList);

		for (LightRecord record : cvList) {
			List<RefsetExport> row = new ArrayList<RefsetExport>();
			Map<Long, ValueDTO> values = record.getValues();

			for (RefsetExport refsetExport : commonTermList) {
				if (refsetExport.isSublistColumn()) {
					Long columnId = refsetExport.getColumnId();

					for (PicklistColumnOutputDTO p : expandSublist) {
						if (p.getColumnId() != columnId) {
							continue;
						}

						ValueDTO v = values.get(p.getColumnId());

						RefsetExport newRefsetExport = new RefsetExport();

						newRefsetExport
						        .setCellValue(v != null && v.getTextValue() != null ? v.getTextValue().trim() : "");
						newRefsetExport.setCellWrapText(false);
						newRefsetExport.setOrderNumber(p.getOrderNumber());
						newRefsetExport.setColumnId(p.getColumnId());

						row.add(newRefsetExport);
					}

					continue;
				}

				row.add(refsetExport);
			}

			expandExport.add(row);
		}

		return !expandExport.isEmpty() ? expandExport : null;
	}

	private List<String> getSublistCollapseValue(Long contextId, Long recordId, Long columnElementId,
	        PicklistColumnOutputDTO p) {
		RecordViewBean rvb = new RecordViewBean();

		rvb.setContextId(contextId);
		rvb.setContainerElementId(columnElementId);
		rvb.setRecordElementId(recordId);
		rvb.setContainerSublist(true);

		List<LightRecord> cvList = picklistService.listRecords(rvb);

		if (cvList == null || cvList.isEmpty()) {
			return new ArrayList<String>();
		}

		Collections.reverse(cvList);

		List<String> vList = new ArrayList<String>();

		for (LightRecord record : cvList) {
			Map<Long, ValueDTO> values = record.getValues();

			ValueDTO v = values.get(p.getColumnId());

			vList.add(v != null && v.getTextValue() != null ? v.getTextValue().trim() : "");
		}

		return vList;
	}

	@Override
	public void createExportTitle(Long refsetContextId, RefsetOutputTitleDTO refsetOutputTitle, Workbook wb,
	        String languageCode, int exportSheetNum) {
		String titleTab = RefsetExportUtils.getTitleNameByLanguageCode(languageCode);

		Sheet sheet = wb.createSheet(titleTab);

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
		cell2.setCellValue(refsetOutputTitle.getTitle() != null ? refsetOutputTitle.getTitle() : "N/A");

		CellStyle headerStyle2 = wb.createCellStyle();

		Font headerFont2 = wb.createFont();
		headerFont2.setFontHeightInPoints((short) 26);
		headerFont2.setFontName("Calibri");

		headerStyle2.setFont(headerFont2);
		cell2.setCellStyle(headerStyle2);
	}

	private int getNumberTables(Workbook wb) {
		int totalTable = 0;

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(i);

			List<XSSFTable> tables = sheet.getTables();

			totalTable += tables != null ? tables.size() : 0;
		}

		return totalTable;
	}
}
