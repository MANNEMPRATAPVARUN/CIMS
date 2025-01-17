package ca.cihi.cims.web.controller.classification;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.util.CihiDefaultXmlTemplates;
import ca.cihi.cims.util.CihiDefaultXmlTemplates.TemplateType;

@Controller
public class XmlTemplateController {

	private final CihiDefaultXmlTemplates templates;

	// --------------------------------------------

	public XmlTemplateController() throws Exception {
		templates = new CihiDefaultXmlTemplates();
	}

	@ResponseBody
	@RequestMapping(value = "/xmltemplates", method = RequestMethod.GET)
	public String getSynchronizationStatus(@RequestParam("id") TemplateType id) throws Exception {
		return templates.get(id);
	}

}
