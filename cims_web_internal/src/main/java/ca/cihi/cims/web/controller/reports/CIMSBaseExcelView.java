package ca.cihi.cims.web.controller.reports;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.document.AbstractXlsView;

public abstract class CIMSBaseExcelView extends AbstractXlsView  {
	private String templateUrl;

	public void setUrl(String templateUrl) {
		this.templateUrl = templateUrl;
	}

	@Override
	protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(templateUrl, ".xls", userLocale);

		try {
			return new HSSFWorkbook(inputFile.getInputStream());
		} catch (IOException e) {
			logger.error(e);
		}
		
		return null;
	}

}
