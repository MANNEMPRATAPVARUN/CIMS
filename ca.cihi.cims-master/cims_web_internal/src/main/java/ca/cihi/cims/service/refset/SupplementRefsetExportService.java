package ca.cihi.cims.service.refset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.SupplementRefsetOutputConfiguration;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.util.RefsetExportUtils;

@Component
public class SupplementRefsetExportService implements RefsetExportService {
	/**
	 * Reference to logger.
	 */
	private static final Log LOGGER = LogFactory.getLog(SupplementRefsetExportService.class);

	/**
	 * Processor Type.
	 */
	public static final String PROCESSOR_TYPE = "supplement";

	@Override
	public String getSheetName(RefsetOutputConfiguration refsetOutputConfiguration) {
		ElementIdentifier elementIdentifier = new ElementIdentifier();

		elementIdentifier.setElementId(refsetOutputConfiguration.getId());
		elementIdentifier.setElementVersionId(null);

		Supplement supplement = RefsetFactory.getSupplement(refsetOutputConfiguration.getRefsetContextId(),
		        elementIdentifier, ConceptLoadDegree.MINIMAL);

		return supplement != null ? WorkbookUtil.createSafeSheetName(supplement.getName().trim())
		        .replaceAll("\\s+", "_").replaceAll("[\\t\\n\\r]", "") : "supplement";
	}

	@Override
	public String getType() {
		return PROCESSOR_TYPE;
	}

	@Override
	public String processExport(RefsetOutputConfiguration refsetOutputConfiguration, Workbook wb, String sheetName,
	        String languageCode, boolean backToTableContent) {
		XSSFSheet sheet = (XSSFSheet) wb.createSheet();

		ElementIdentifier elementIdentifier = new ElementIdentifier();

		elementIdentifier.setElementId(refsetOutputConfiguration.getId());
		elementIdentifier.setElementVersionId(null);

		Supplement supplement = RefsetFactory.getSupplement(refsetOutputConfiguration.getRefsetContextId(),
		        elementIdentifier, ConceptLoadDegree.REGULAR);

		if (supplement == null) {
			return sheet.getSheetName();
		}

		byte content[] = supplement.getContent(Language.ENG);
		ByteArrayInputStream in = new ByteArrayInputStream(content);

		try {
			String exportSheetName = cloneSupplement(wb, sheet, sheetName, in, languageCode, backToTableContent);

			in.close();

			return exportSheetName;
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return sheet.getSheetName();
	}

	private String cloneSupplement(Workbook wb, XSSFSheet newSheet, String sheetName, ByteArrayInputStream in, String languageCode,
	        boolean backToTableContent) throws IOException {
		XSSFWorkbook wbInput = new XSSFWorkbook(in);

		XSSFSheet sheet = wbInput.getSheetAt(0);

		String exportSheetName = backToTableContent ? sheet.getSheetName() : sheetName;

		try {
			wb.setSheetName(wb.getSheetIndex(newSheet), exportSheetName);
		} catch (Exception e) {
			exportSheetName = sheet.getSheetName() + "_" + ((new Random()).nextInt(900) + 100);

			wb.setSheetName(wb.getSheetIndex(newSheet), exportSheetName);
		}

		try {
			copyPicture(wbInput, newSheet, wb);
		} catch (Exception e) {
			LOGGER.error(e);
		}

		newSheet.setDefaultRowHeight(sheet.getDefaultRowHeight());
		newSheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());

		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

			/**
			 * Just add it to the sheet on the new workbook.
			 */
			newSheet.addMergedRegion(mergedRegion);
		}

		// Copy content
		for (int rowNumber = sheet.getFirstRowNum(); rowNumber <= sheet.getLastRowNum(); rowNumber++) {
			final XSSFRow oldRow = sheet.getRow(rowNumber);

			if (oldRow != null) {
				final XSSFRow newRow = newSheet.createRow(rowNumber);
				newRow.setHeight(oldRow.getHeight());

				for (int columnNumber = oldRow.getFirstCellNum(); columnNumber < oldRow
				        .getLastCellNum(); columnNumber++) {
					newSheet.setColumnWidth(columnNumber, sheet.getColumnWidth(columnNumber));

					final XSSFCell oldCell = oldRow.getCell(columnNumber);

					if (oldCell != null) {
						final XSSFCell newCell = newRow.createCell(columnNumber);

						// If there is a cell comment, copy
						if (oldCell.getCellComment() != null) {
							newCell.setCellComment(oldCell.getCellComment());
						}

						// If there is a cell hyperlink, copy
						if (oldCell.getHyperlink() != null) {
							newCell.setHyperlink(oldCell.getHyperlink());
						}

						// Set the cell data type
						newCell.setCellType(oldCell.getCellType());

						// Copy style
						DataFormat newDataFormat = newCell.getSheet().getWorkbook().createDataFormat();
						short newFormat = newDataFormat.getFormat(oldCell.getCellStyle().getDataFormatString());

						CellStyle newCellStyle = wb.createCellStyle();

						try {
							Font oldFont = oldCell.getSheet().getWorkbook()
							        .getFontAt(oldCell.getCellStyle().getFontIndex());
//							Font newFont = newCell.getSheet().getWorkbook().findFont(oldFont.getBold(),
//							        oldFont.getColor(), oldFont.getFontHeight(), oldFont.getFontName(),
//							        oldFont.getItalic(), oldFont.getStrikeout(), oldFont.getTypeOffset(),
//							        oldFont.getUnderline());
							Font newFont = newCell.getSheet().getWorkbook().createFont();
							newFont.setBold(oldFont.getBold());
							newFont.setColor(oldFont.getColor());
							newFont.setFontHeight(oldFont.getFontHeight());
							newFont.setFontName(oldFont.getFontName());
							newFont.setItalic(oldFont.getItalic());
							newFont.setStrikeout(oldFont.getStrikeout());
							newFont.setTypeOffset(oldFont.getTypeOffset());
							newFont.setUnderline(oldFont.getUnderline());
							newFont.setCharSet(oldFont.getCharSet());

							newCellStyle.setFont(newFont);
						} catch (Exception e) {
							LOGGER.error(e);
						}

						newCellStyle.setDataFormat(newFormat);

						newCellStyle.setAlignment(oldCell.getCellStyle().getAlignmentEnum());
						newCellStyle.setBorderBottom(oldCell.getCellStyle().getBorderBottomEnum());
						newCellStyle.setBorderTop(oldCell.getCellStyle().getBorderTopEnum());
						newCellStyle.setBorderLeft(oldCell.getCellStyle().getBorderLeftEnum());
						newCellStyle.setBorderRight(oldCell.getCellStyle().getBorderRightEnum());
						newCellStyle.setBottomBorderColor(oldCell.getCellStyle().getBottomBorderColor());
						newCellStyle.setDataFormat(oldCell.getCellStyle().getDataFormat());

						XSSFColor fillBackgroundColor = oldCell.getCellStyle().getFillBackgroundColorColor();

						if (fillBackgroundColor != null) {
							try {
								newCellStyle.setFillBackgroundColor(fillBackgroundColor.getIndexed());
							} catch (Exception e) {
								newCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
							}
						}

						XSSFColor fillForegroundColor = oldCell.getCellStyle().getFillForegroundColorColor();

						if (fillForegroundColor != null) {
							try {
								newCellStyle.setFillForegroundColor(fillForegroundColor.getIndexed());
							} catch (Exception e) {
								newCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
							}
						}

						newCellStyle.setHidden(oldCell.getCellStyle().getHidden());
						newCellStyle.setIndention(oldCell.getCellStyle().getIndention());
						newCellStyle.setLeftBorderColor(oldCell.getCellStyle().getLeftBorderColor());
						newCellStyle.setLocked(oldCell.getCellStyle().getLocked());
						newCellStyle.setRightBorderColor(oldCell.getCellStyle().getRightBorderColor());
						newCellStyle.setTopBorderColor(oldCell.getCellStyle().getTopBorderColor());
						newCellStyle.setVerticalAlignment(oldCell.getCellStyle().getVerticalAlignmentEnum());
						newCellStyle.setWrapText(oldCell.getCellStyle().getWrapText());

						newCell.setCellStyle(newCellStyle);

						// Copy value
						setCellValue(newCell, getCellValue(oldCell));
					}
				}
			}
		}

		if (backToTableContent) {
			XSSFRow row = newSheet.getRow(1);
			row = row != null ? row : newSheet.createRow(1);

			Cell cell = row.getCell(0);
			cell = cell != null ? cell : row.createCell(0);

			cell.setCellValue(RefsetExportUtils.getBackToTableContentByLanguageCode(languageCode));

			CreationHelper createHelper = wb.getCreationHelper();

			Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
			link.setAddress("'" + RefsetExportUtils.getTableContentByLanguageCode(languageCode) + "'!A1");
			cell.setHyperlink(link);

			CellStyle hlinkStyle = wb.createCellStyle();
			Font hlinkFont = wb.createFont();
			hlinkFont.setUnderline(Font.U_SINGLE);
			hlinkFont.setColor(IndexedColors.BLUE.getIndex());
			hlinkStyle.setFont(hlinkFont);

			cell.setCellStyle(hlinkStyle);
		}

		return exportSheetName;
	}

	private void setCellValue(final XSSFCell cell, final Object value) {
		if (value instanceof Boolean) {
			cell.setCellValue((boolean) value);
		} else if (value instanceof Byte) {
			cell.setCellValue((byte) value);
		} else if (value instanceof Double) {
			cell.setCellValue((double) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Object getCellValue(final XSSFCell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue(); // boolean
		case Cell.CELL_TYPE_ERROR:
			return cell.getErrorCellValue(); // byte
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue(); // double
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_STRING:
		case Cell.CELL_TYPE_BLANK:
			return cell.getStringCellValue(); // String
		default:
			throw new IllegalArgumentException();
		}
	}

	private void copyPicture(Workbook wb, Sheet newSheet, Workbook targetWorkbook) {
		XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);

		if (sheet == null) {
			return;
		}

		Drawing drawing = sheet.createDrawingPatriarch();

		if (drawing == null) {
			return;
		}

		if (!(drawing instanceof XSSFDrawing)) {
			return;
		}

		XSSFDrawing draw = (XSSFDrawing) drawing;

		if (draw.getShapes() == null) {
			return;
		}

		for (XSSFShape shape : draw.getShapes()) {
			if (shape instanceof Picture) {
				XSSFPicture picture = (XSSFPicture) shape;

				XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();

				if (anchor == null) {
					continue;
				}

				XSSFPictureData pict = picture.getPictureData();

				if (pict == null) {
					continue;
				}

				CreationHelper helper = targetWorkbook.getCreationHelper();
				Drawing newDrawing = newSheet.createDrawingPatriarch();
				ClientAnchor newAnchor = helper.createClientAnchor();

				newAnchor.setCol1(anchor.getCol1());
				newAnchor.setRow1(anchor.getRow1());
				newAnchor.setCol2(anchor.getCol2());
				newAnchor.setRow2(anchor.getRow2());
				newAnchor.setDx1(anchor.getDx1());
				newAnchor.setDx2(anchor.getDx2());
				newAnchor.setDy1(anchor.getDy1());
				newAnchor.setDy2(anchor.getDy2());

				int pictureIdx = targetWorkbook.addPicture(pict.getData(), pict.getPictureType());

				newDrawing.createPicture(newAnchor, pictureIdx);
			}
		}
	}

	@Override
	public void createExportTitle(Long refsetContextId, RefsetOutputTitleDTO refsetOutputTitle, Workbook wb,
	        String languageCode, int exportSheetNum) {
		SupplementRefsetOutputConfiguration refsetOutputConfiguration = new SupplementRefsetOutputConfiguration();

		refsetOutputConfiguration.setSupplementId(refsetOutputTitle.getSupplementId());
		refsetOutputConfiguration.setRefsetOutputId(refsetOutputTitle.getRefsetOutputId());
		refsetOutputConfiguration.setRefsetContextId(refsetContextId);

		processExport(refsetOutputConfiguration, wb, RefsetExportUtils.getTitleNameByLanguageCode(languageCode),
		        languageCode, false);
	}
}
