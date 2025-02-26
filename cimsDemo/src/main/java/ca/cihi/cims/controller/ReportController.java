package ca.cihi.cims.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.web.view.ExcelReportView;
import ca.cihi.cims.web.view.PdfReportView;


/**
 * A Spring controller that allows the users to download an Excel document
 * */
@Controller
public class ReportController // extends AbstractController
{
	private static final Log LOGGER = LogFactory.getLog(ReportController.class);
	@RequestMapping(value = "/cimsReport.htm", method = RequestMethod.GET)
	public ModelAndView setupForm() {
		LOGGER.debug("< setupForm");
		// dummy data
		Map<String, String> reportData = new HashMap<String, String>();
		reportData.put("Jan-2013", "$100,000,000");
		reportData.put("Feb-2013", "$110,000,000");
		reportData.put("Mar-2013", "$130,000,000");
		reportData.put("Apr-2013", "$140,000,000");
		reportData.put("May-2013", "$200,000,000");
		//model.addAttribute("reportData", reportData);		
		LOGGER.debug("> setupForm ");
		return new ModelAndView("report/summary", "reportData", reportData);
	}

	/**
	 * * Handle request to download an Excel document
	 * 
	 * @throws ServletRequestBindingException
	 * */
	// @RequestMapping(value = "/cimsReport.htm")
	@RequestMapping(value = "/cimsReport.htm", params = "output=excel")
	protected ModelAndView viewExcel(HttpServletRequest request) {
		LOGGER.debug("< Controller.viewExcel");
		// String output = ServletRequestUtils.getStringParameter(request,
		// "output");
		// dummy data
		Map<String, String> reportDataE = new HashMap<String, String>();
		reportDataE.put("Jan-2013", "$100,000,000");
		reportDataE.put("Feb-2013", "$110,000,000");
		reportDataE.put("Mar-2013", "$130,000,000");
		reportDataE.put("Apr-2013", "$140,000,000");
		reportDataE.put("May-2013", "$200,000,000");
		reportDataE.put("Jan-2013", "$100,000,000");
		reportDataE.put("Feb-2013", "$110,000,000");
		reportDataE.put("Mar-2013", "$130,000,000");
		reportDataE.put("Apr-2013", "$140,000,000");
		reportDataE.put("May-2013", "$200,000,000");
		reportDataE.put("Jan-2013", "$100,000,000");
		reportDataE.put("Feb-2013", "$110,000,000");
		reportDataE.put("Mar-2013", "$130,000,000");
		reportDataE.put("Apr-2013", "$140,000,000");
		reportDataE.put("May-2013", "$200,000,000");
		reportDataE.put("Jan-2013", "$100,000,000");
		reportDataE.put("Feb-2013", "$110,000,000");
		reportDataE.put("Mar-2013", "$130,000,000");
		reportDataE.put("Apr-2013", "$140,000,000");
		reportDataE.put("May-2013", "$200,000,000");
		// return excel view
		ExcelReportView excelView = new ExcelReportView();
		LOGGER.debug(">Controller.viewExcel");
		return new ModelAndView(excelView, "reportData", reportDataE);
	}
	
	@RequestMapping(value = "/cimsReport.htm", params = "output=pdf")
	protected ModelAndView viewPdf(HttpServletRequest request) {
		LOGGER.debug("< Controller.viewPdf");		
		// dummy data
		Map<String, String> reportDataP = new HashMap<String, String>();
		reportDataP.put("Jan-2013", "$100,000,000");
		reportDataP.put("Feb-2013", "$110,000,000");
		
		// return pdf view
		PdfReportView pdfView = new PdfReportView();
		LOGGER.debug(">Controller.viewPdf");
		return new ModelAndView(pdfView, "reportData", reportDataP);
	}
}
