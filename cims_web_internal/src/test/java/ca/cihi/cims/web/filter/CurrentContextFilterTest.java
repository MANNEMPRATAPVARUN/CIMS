package ca.cihi.cims.web.filter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CurrentContextFilterTest {

	private CurrentContextFilter filter;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// -------------------------------------------------------

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		filter = new CurrentContextFilter();
		filter.setContextProvider(contextProvider);
		filter.setCurrentContext(currentContext);
		when(request.getParameter(CurrentContextParams.ACTIVATE)).thenReturn("A");
		when(request.getParameter(CurrentContextParams.BASECLASSIFICATION)).thenReturn("ICD-10-CA");

		Collection<ContextIdentifier> contextIdentifiers = contextProvider.findBaseContextIdentifiers("ICD-10-CA");
		Iterator<ContextIdentifier> itContextIds = contextIdentifiers.iterator();
		while (itContextIds.hasNext()) {
			Long contextId = itContextIds.next().getContextId();
			when(request.getParameter(CurrentContextParams.CONTEXT_ID)).thenReturn(String.valueOf(contextId));
			break;
		}
	}

	@Test
	public void testAfterCompletion() throws Exception {
		filter.afterCompletion(request, response, null, null);
		assertTrue("current context", currentContext != null);
	}

	@Test
	public void testPostHandle() throws Exception {
		filter.postHandle(request, response, null, null);
		assertTrue("do nothing", true);
	}

	@Test
	public void testPreHandle() throws Exception {
		boolean preHandled = filter.preHandle(request, response, null);
		assertTrue("Should return true", preHandled);
	}

}
