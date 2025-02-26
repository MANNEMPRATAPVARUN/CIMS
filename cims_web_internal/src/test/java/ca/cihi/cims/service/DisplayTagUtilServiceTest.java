package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class DisplayTagUtilServiceTest {

	@Mock
	protected HttpServletRequest request;

	@Autowired
	private DisplayTagUtilService dtService;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	private final String paramName = "componentTable";

	@Before
	public void initialize() {

		String PARAM_PAGE = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_PAGE);
		String PARAM_SORT = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_SORT);
		String PARAM_ORDR = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_ORDER);

		MockitoAnnotations.initMocks(this);
		when(request.getParameter(PARAM_PAGE)).thenReturn("3");
		when(request.getParameter(PARAM_SORT)).thenReturn("1");
		when(request.getParameter(PARAM_ORDR)).thenReturn("1");
	}

	@Test
	public void pageLinksTest() {

		Map<String, Object> mapOfStuff = dtService.addForPageLinks(request, paramName);
		assertTrue(mapOfStuff.size() > 0);

		for (String key : mapOfStuff.keySet()) {
			LOGGER.debug("Key: " + key + ": " + mapOfStuff.get(key));

		}

	}
}
