package ca.cihi.cims.web.controller.supplement;

import static ca.cihi.cims.WebConstants.CURRENT_USER;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/supplements/content")
public class SupplementContentController extends SupplementBaseController {

	@RequestMapping(value = "/groupContent")
	public @ResponseBody String getGroupContent(@RequestParam(value = "language", defaultValue = "ENG") String language,
			@RequestParam(value = "contextId", required = true) Long contextId,
			@RequestParam(value = "sectionCode", required = true) String sectionCode,
			@RequestParam(value = "groupCode", required = true) String groupCode,
			@RequestParam(value = "elid", required = true) Long id) {

		return service.getCCIGroupContent(language, contextId, sectionCode, groupCode, id);
	}

	@RequestMapping(value = "/rubricContent")
	public @ResponseBody String getRubricContent(
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@RequestParam(value = "contextId", required = true) Long contextId,
			@RequestParam(value = "sectionCode", required = true) String sectionCode,
			@RequestParam(value = "groupCode", required = true) String groupCode,
			@RequestParam(value = "id", required = true) Long id) {

		return service.getCCIRubricContent(language, contextId, sectionCode, groupCode, id);
	}

}
