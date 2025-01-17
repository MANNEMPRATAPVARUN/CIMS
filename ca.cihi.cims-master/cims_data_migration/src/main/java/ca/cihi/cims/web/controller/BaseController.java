package ca.cihi.cims.web.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.web.bean.KeyValueBean;

public class BaseController {

	protected ContextProvider contextProvider;

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	@ModelAttribute("classificationList")
	public Collection<KeyValueBean> populateClassification() {
		final Collection<String> classifications = contextProvider.findBaseClassifications();

		ArrayList<KeyValueBean> keyValues = new ArrayList<KeyValueBean>();
		for (String code : classifications) {
			keyValues.add(new KeyValueBean(code, code));
		}
		return keyValues;
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

}
