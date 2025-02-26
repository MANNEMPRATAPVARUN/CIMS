package ca.cihi.cims.service.reports;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Service
public class ReportServiceImpl implements ReportService {

	private static final Log LOGGER = LogFactory.getLog(ReportServiceImpl.class);
	public static final String WORKSHEET_NAME = "name";
	public static final String PARA_NAME = "paraName";
	public static final String COLUMN = "column_number";
	public static final String ROW = "row_number";
	public static final String START_ROW_NUBMER = "startRowNumber";
	public static final String DETAIL_NAME = "name";
	public static final String DETAIL_HEADER_NAME = "name";
	private static final String WORK_SHEET_NAME = "/workbook/worksheet[@name='";
	private static final String PARA_TYPE = "paraType";

	@Autowired
	private LookupService lookupService;
	@Autowired
	private ReportMapper reportMapper;
	@Autowired
	private ConceptService conceptService;

	private ReportGeneratorFactory reportGeneratorFactory;

	private CellStyle createMarkStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		return style;
	}

	@Override
	public void generateReport(final Map<String, Object> reportData, Workbook workbook, InputStream schemaFile)
			throws Exception {
		try {
			// Load the report schema
			SAXBuilder builder = new SAXBuilder();

			Document document = builder.build(schemaFile);
			XPath worksheetXPath = XPath.newInstance("/workbook/worksheet");

			@SuppressWarnings("unchecked")
			List<Element> worksheetList = worksheetXPath.selectNodes(document);

			for (Element worksheet : worksheetList) {
				populateWorksheet(workbook, worksheet, document, reportData);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public Map<String, Object> generateReportData(ReportViewBean reportViewBean) {
		ReportGenerator reportGenerator = reportGeneratorFactory.createReportGenerator(reportViewBean.getReportType());
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setConceptService(conceptService);
		return reportGenerator.generatReportData(reportViewBean);
	}

	public ReportGeneratorFactory getReportGeneratorFactory() {
		return reportGeneratorFactory;
	}

	private List<Map<String, Object>> nullSafe(List<Map<String, Object>> detailDataList) {
		if (detailDataList == null) {
			return Collections.emptyList();
		}
		return detailDataList;
	}

	private void populateCell(Sheet sheet, int rowNumber, int colNumber, Integer cellValue, CellStyle markStyle) {
		Row row = sheet.getRow(rowNumber - 1);
		if (row == null) {
			row = sheet.createRow(rowNumber - 1);
		}
		Cell cell = row.getCell(colNumber - 1);
		if (cell == null) {
			cell = row.createCell(colNumber - 1);
		}
		if (cellValue != null) {
			cell.setCellValue(cellValue);
		} else {
			cell.setCellValue("");
		}
	}

	private void populateCell(Sheet sheet, final int rowNumber, final int colNumber, String cellValue,
			CellStyle cellStyle) {
		Row row = sheet.getRow(rowNumber - 1);
		if (row == null) {
			row = sheet.createRow(rowNumber - 1);
		}
		Cell cell = row.getCell(colNumber - 1);
		if (cell == null) {
			cell = row.createCell(colNumber - 1);
		}
		String realValue = cellValue;
		if (cellValue != null && cellValue.contains(WebConstants.MARK)) {
			realValue = cellValue.replace(WebConstants.MARK, "");

			cell.setCellStyle(cellStyle);
		}
		cell.setCellValue(realValue);
	}

	private void populateDetail(Sheet sheet, final List<Element> detailParameters, final int startRowNumber,
			final List<Map<String, Object>> detailDataList, CellStyle markStyle) {

		int startRow = startRowNumber;

		for (Map<String, Object> detailData : nullSafe(detailDataList)) {

			for (Element detailParameter : detailParameters) {

				try {
					String paraName = detailParameter.getChildText(PARA_NAME);
					int colNo = Integer.parseInt(detailParameter.getChildText(COLUMN));
					String paraType = detailParameter.getChildText(PARA_TYPE);
					if ("Number".equals(paraType)) {
						populateCell(sheet, startRow, colNo, (Integer) detailData.get(paraName), markStyle);
					} else {
						populateCell(sheet, startRow, colNo, (String) detailData.get(paraName), markStyle);
					}
				} catch (NumberFormatException nfex) {
					LOGGER.error(nfex.getMessage());
				}
			}
			startRow++;
		}
	}

	private void populateDetailHeader(Sheet sheet, final List<Element> detailHeaderParameters,
			final Map<String, Object> detailHeaderMap, CellStyle markStyle) {
		for (Element headerParameter : detailHeaderParameters) {
			try {
				String paraName = headerParameter.getChildText(PARA_NAME);
				int rowNo = Integer.parseInt(headerParameter.getChildText(ROW));
				int colNo = Integer.parseInt(headerParameter.getChildText(COLUMN));
				String paraType = headerParameter.getChildText(PARA_TYPE);
				if ("Number".equals(paraType)) {
					populateCell(sheet, rowNo, colNo, (Integer) detailHeaderMap.get(paraName), markStyle);
				} else {
					populateCell(sheet, rowNo, colNo, (String) detailHeaderMap.get(paraName), markStyle);
				}
			} catch (NumberFormatException nfex) {
				LOGGER.error(nfex.getMessage());
			}
		}
	}

	private void populateHead(Sheet sheet, final List<Element> headParameters, final Map<String, Object> paraData,
			CellStyle markStyle) {
		for (Element headParameter : headParameters) {
			try {
				String paraName = headParameter.getChildText(PARA_NAME);
				int rowNo = Integer.parseInt(headParameter.getChildText(ROW));
				int colNo = Integer.parseInt(headParameter.getChildText(COLUMN));
				String paraType = headParameter.getChildText(PARA_TYPE);
				if ("Number".equals(paraType)) {
					populateCell(sheet, rowNo, colNo, (Integer) paraData.get(paraName), markStyle);
				} else {
					populateCell(sheet, rowNo, colNo, (String) paraData.get(paraName), markStyle);
				}
			} catch (NumberFormatException nfex) {
				LOGGER.error(nfex.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void populateWorksheet(Workbook wb, final Element worksheetElem, final Document document,
			final Map<String, Object> paraData) throws JDOMException {
		String worksheetName = worksheetElem.getAttributeValue(WORKSHEET_NAME);
		Sheet sheet = wb.getSheet(worksheetName);

		CellStyle markStyle = createMarkStyle(wb);
		try {
			XPath headParamXPath = XPath.newInstance(WORK_SHEET_NAME + worksheetName + "']/header/parameter");
			List<Element> headParamNodes = headParamXPath.selectNodes(document);

			if (headParamNodes != null && !headParamNodes.isEmpty()) {
				List<Element> headParameters = headParamXPath.selectNodes(document);
				populateHead(sheet, headParameters, paraData, markStyle);
			}

			XPath detailHeaderParamXPath = XPath.newInstance(WORK_SHEET_NAME + worksheetName
					+ "']/detailHeader/parameter");

			List<Element> detailHeaderParaNodes = detailHeaderParamXPath.selectNodes(document);

			if (detailHeaderParaNodes != null && !detailHeaderParaNodes.isEmpty()) {
				Element detailHeaderElement = detailHeaderParaNodes.get(0).getParentElement();
				String detailHeaderDataName = detailHeaderElement.getAttributeValue(DETAIL_HEADER_NAME);
				Map<String, Object> detailHeaderMap = (Map<String, Object>) paraData.get(detailHeaderDataName);
				populateDetailHeader(sheet, detailHeaderParaNodes, detailHeaderMap, markStyle);
			}

			XPath detailXPath = XPath.newInstance(WORK_SHEET_NAME + worksheetName + "']/detail");

			List<Element> detailNodes = detailXPath.selectNodes(document);

			if (detailNodes != null && !detailNodes.isEmpty()) {
				for (Element detailNode : detailNodes) {
					List<Element> detailParamNodes = detailNode.getChildren("parameter");
					if (detailParamNodes != null && !detailParamNodes.isEmpty()) {

						int startRowNumber = Integer.parseInt(detailNode.getAttributeValue(START_ROW_NUBMER));
						String detailDataName = detailNode.getAttributeValue(DETAIL_NAME);

						List<Map<String, Object>> detailDataMap = (List<Map<String, Object>>) paraData
								.get(detailDataName);

						populateDetail(sheet, detailParamNodes, startRowNumber, detailDataMap, markStyle);
					}
				}

			}

			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);

		} catch (JDOMException jdomex) {
			LOGGER.error(jdomex.getMessage());
			throw jdomex;
		} catch (NumberFormatException nfex) {
			LOGGER.error(nfex.getMessage());
			throw nfex;
		}

	}

	@Autowired
	public void setReportGeneratorFactory(ReportGeneratorFactory reportGeneratorFactory) {
		this.reportGeneratorFactory = reportGeneratorFactory;
	}

}