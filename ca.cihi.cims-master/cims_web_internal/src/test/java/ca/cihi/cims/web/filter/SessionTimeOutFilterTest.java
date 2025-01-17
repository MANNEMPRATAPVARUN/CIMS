package ca.cihi.cims.web.filter;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SessionTimeOutFilterTest {

	@Mock
	private ServletContext context;

	private SessionTimeOutFilter filter;
	@Mock
	private FilterChain filterChain;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void initializeMocks() throws ServletException {
		MockitoAnnotations.initMocks(this);
		filter = new SessionTimeOutFilter();
		List<String> urlList = new ArrayList<>();
		urlList.add("/index.htm");
		urlList.add("/sessionTimeOut.htm");
		urlList.add("/j_spring_cas_security_check");
		MockFilterConfig config = new MockFilterConfig(context);
		when(request.getContextPath()).thenReturn("/cims_web_internal");
		config.addInitParameter("avoid-urls", "/index.htm,/sessionTimeOut.htm,/j_spring_cas_security_check");
		filter.init(config);
	}

	@Test
	public void testFilter() throws IOException, ServletException {

		when(request.getServletPath()).thenReturn("/test.htm");

		filter.doFilter(request, response, filterChain);

		verify(response).sendRedirect("/cims_web_internal/sessionTimeOut.htm");

		when(request.getServletPath()).thenReturn("/j_spring_cas_security_check");

		filter.doFilter(request, response, filterChain);

		assertTrue("do nothing", true);

	}
}
