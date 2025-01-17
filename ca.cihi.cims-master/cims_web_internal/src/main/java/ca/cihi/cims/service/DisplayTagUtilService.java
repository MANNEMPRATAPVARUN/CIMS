package ca.cihi.cims.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface DisplayTagUtilService {

	Map<String, Object> addForPageLinks(HttpServletRequest request, String paramName);
}
