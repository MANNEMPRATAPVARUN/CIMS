package ca.cihi.cims.web.controller.supplement;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.web.bean.KeyValueBean;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/supplements/remove")
public class SupplementRemoveController {

	@Autowired
	private ClassificationService service;

	// --------------------------------------------------

	@ResponseBody
	@RequestMapping(method = GET)
	public KeyValueBean remove(@ModelAttribute(value = CURRENT_USER) User user, Model model,
			@RequestParam("lockTimestamp") long lockTimestamp, @RequestParam("id") long indexId,
			@RequestParam(value = "language", defaultValue = "ENG") String language) throws Exception {
		try {
			service.deleteSupplementById(new OptimisticLock(lockTimestamp), user, indexId, Language
					.fromString(language));
			return new KeyValueBean();
		} catch (Exception ex) {
			return new KeyValueBean("error", ex.getMessage());
		}
	}

	public void setService(ClassificationService service) {
		this.service = service;
	}

}
