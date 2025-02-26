package ca.cihi.cims.web.controller.tabular;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.Language;
import ca.cihi.cims.service.ClassificationService;

@Controller
@RequestMapping(value = "/tabulars/diagram/show")
public class TabularDiagramShowController {

	@Autowired
	private ClassificationService service;

	// ------------------------------------------------------------------

	public void setService(ClassificationService service) {
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.GET)
	public HttpEntity<byte[]> show(@RequestParam("id") long tabularId, @RequestParam("lang") Language lang,
			HttpServletResponse response) {
		byte[] content = service.getTabularDiagramContent(tabularId, lang);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", "image/gif");
		headers.add("Content-Length", "" + content.length);
		headers.add("Content-Disposition", "inline");
		return new HttpEntity<byte[]>(content, headers);
	}

}
