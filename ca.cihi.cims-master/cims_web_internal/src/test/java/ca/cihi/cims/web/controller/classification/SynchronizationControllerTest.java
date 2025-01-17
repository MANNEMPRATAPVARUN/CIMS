package ca.cihi.cims.web.controller.classification;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.exception.ConcurrentUpdateException;
import ca.cihi.cims.model.SynchronizationStatus;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.service.synchronization.SynchronizationService;

@SuppressWarnings("static-access")
public class SynchronizationControllerTest {

	private MockMvc mockMvc;

	@Mock
	private SynchronizationService synchronizationService;

	// --------------------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
		SynchronizationController controller = new SynchronizationController();
		controller.setSynchronizationService(synchronizationService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

		long instanceId = 100;
		long changeRequestIdValid = 200;
		long changeRequestIdInvalid = 300;
		SynchronizationStatus status = new SynchronizationStatus();
		status.setCurrent(1);
		status.setTotal(10);

		when(synchronizationService.getInstanceId()).thenReturn(instanceId);
		when(synchronizationService.getSynchronizationStatus(changeRequestIdValid)).thenReturn(status);
		Mockito.doThrow(new ConcurrentUpdateException("test")).when(synchronizationService).synchronizeAsync(
				nullable(OptimisticLock.class), nullable(User.class), eq(changeRequestIdInvalid));
		ArgumentMatcher<Object> nullMatcher = new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object arg0) {
				return arg0 == null;
			}
		};

		// start: ok
		mockMvc.perform(get("/synchronization/start?ccp_rid={ccp_rid}&lockTimestamp=900", changeRequestIdValid) //
				.sessionAttr(WebConstants.CURRENT_USER, new User()))
				.andExpect(request().attribute(controller.INSTANCE_ID, equalTo(instanceId)))//
				.andExpect(request().attribute(controller.CONCURRENT_ERROR, is(nullValue())))//
				.andExpect(view().name(controller.SYNCHRONIZATION_VIEW));
		// status
		mockMvc
				.perform(get("/synchronization/status?ccp_rid={ccp_rid}", //
						changeRequestIdValid) //
						.sessionAttr(WebConstants.CURRENT_USER, new User()))
				//
				.andExpect(
						content()
								.string(
										"{\"total\":10,\"current\":1,\"currentCode\":null,\"error\":null,\"instanceId\":0,\"lockTimestamp\":0}"));
		// start: error
		mockMvc.perform(get("/synchronization/start?ccp_rid={ccp_rid}&lockTimestamp=900", changeRequestIdInvalid) //
				.sessionAttr(WebConstants.CURRENT_USER, new User()))//
				.andExpect(request().attribute(controller.INSTANCE_ID, is(nullValue())));
	}
}
