package ca.cihi.cims.web.view;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.document.AbstractPdfView;
import com.lowagie.text.Document;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class PdfReportView extends AbstractPdfView {
	@SuppressWarnings("unchecked")
	@Override
	protected void buildPdfDocument(Map model, Document document,PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> reportData = (Map<String, String>) model.get("reportData");
		
		Table table = new Table(2);
		table.addCell("Month");
		table.addCell("Cost");

		for (Map.Entry<String, String> entry : reportData.entrySet()) {
			table.addCell(entry.getKey());
			table.addCell(entry.getValue());
		}
		document.add(table);
	}

}