package ca.cihi.cims.bll.hg;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.hg.mapper.config.ClassNamePropertyConfig;
import ca.cihi.cims.hg.mapper.config.ConceptPropertyConfig;
import ca.cihi.cims.hg.mapper.config.DataPropertyConfig;
import ca.cihi.cims.hg.mapper.config.PropertyConfig;
import ca.cihi.cims.hg.mapper.config.PropertyMethods;
import ca.cihi.cims.hg.mapper.config.StatusPropertyConfig;
import ca.cihi.cims.hg.mapper.config.WrapperConfig;
import ca.cihi.cims.util.timer.Perf;

/**
 * Implementation of {@link MethodInterceptor} used to associate {@link PropertyAdapter} with property methods
 * 
 * 
 */
class HgInterceptor implements MethodInterceptor, Identified {

	private final Map<Method, PropertyAdapter> methodHandlers = new HashMap<Method, PropertyAdapter>();

	private Long elementId;

	public HgInterceptor(WrapperConfig config, ContextElementAccess operations) {
		Perf.start("HGInterceptor.constructor");

		associateAdaptersWithMethods(config, operations);
		Perf.stop("HGInterceptor.constructor");
	}

	private void associateAdaptersWithMethods(WrapperConfig config, ContextElementAccess operations) {
		Map<String, PropertyConfig> properties = config.getProperties();
		Collection<PropertyConfig> values = properties.values();
		for (PropertyConfig propConfig : values) {

			PropertyAdapter adapter = createAdapter(operations, propConfig);

			PropertyMethods methods = propConfig.getPropertyMethods();
			for (Method method : methods.getGetterMethods()) {
				methodHandlers.put(method, adapter);
			}

			for (Method method : methods.getSetterMethods()) {
				methodHandlers.put(method, adapter);

			}
		}
	}

	private PropertyAdapter createAdapter(ContextElementAccess operations, PropertyConfig propConfig) {

		if (propConfig instanceof DataPropertyConfig) {
			return new DataPropertyAdapter(this, (DataPropertyConfig) propConfig, operations);
		}

		if (propConfig instanceof ConceptPropertyConfig) {
			return new ConceptPropertyAdapter(this, (ConceptPropertyConfig) propConfig, operations);
		}

		if (propConfig instanceof ClassNamePropertyConfig) {
			return new ClassNamePropertyAdapter(this, (ClassNamePropertyConfig) propConfig, operations);
		}

		if (propConfig instanceof StatusPropertyConfig) {
			return new StatusPropertyAdapter(this, (StatusPropertyConfig) propConfig, operations);
		}

		throw new UnsupportedOperationException("Unsupported property config type: " + propConfig);
	}

	@Override
	public Long getElementId() {
		return elementId;
	}

	/**
	 * All generated proxied methods call this method instead of the original method. This method use the configured
	 * {@link PropertyAdapter} to handle the get and set method call. The exceptions are getElementId and setElementId
	 * method which have special treatment.
	 */
	@Override
	public Object intercept(Object ob, Method method, Object[] arguments, MethodProxy proxy) throws Throwable {

		if (method.getName().equals("getElementId")) {
			return getElementId();
		}

		// Note: the setElementId method in the wrappers gets intercepted here. It does allow you set the element Id
		// however it does not pass to any Property Adapter and does not mark as dirty. This prevents accidently
		// setting and persisting the element.
		if (method.getName().equals("setElementId")) {
			setElementId((Long) arguments[0]);
			return null;
		}

		// TODO: Support equals and hashCode based on element identity and
		// wrapper type

		@SuppressWarnings("rawtypes")
		PropertyAdapter handler = methodHandlers.get(method);

		if (handler == null) {
			return proxy.invokeSuper(ob, arguments);
		}

		InvocationDetails details = new InvocationDetails(method, arguments);

		if (handler.isGetter(method)) {
			return handler.getValue(details);
		} else {
			handler.setValue(details);
			return null;
		}
	}

	@Override
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

}