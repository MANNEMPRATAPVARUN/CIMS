package ca.cihi.cims.bll.hg;

import net.sf.cglib.proxy.Enhancer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.UsesContextAccess;
import ca.cihi.cims.hg.WrapperFactory;
import ca.cihi.cims.hg.mapper.config.MappingConfig;
import ca.cihi.cims.hg.mapper.config.WrapperConfig;

@Component
public class HGWrapperFactory implements WrapperFactory {
	private MappingConfig mappingConfig;

	/**
	 * This method use {@link Enhancer} to create wrapper class proxy, and create method intercepter using WrapperConfig
	 * from the mapping configuration created during application start, also inject {@link ContextAccess} into the the
	 * proxy class.
	 */
	@Override
	public <T> T newInstance(Class<T> clazz, ContextElementAccess elementOps) {

		Enhancer enhancer = new Enhancer();

		enhancer.setSuperclass(clazz);
		enhancer.setInterfaces(new Class[] { Identified.class });

		WrapperConfig entityCfg = mappingConfig.getEntity(clazz);
		if (entityCfg == null) {

			throw new NullPointerException("No entity config found for class " + clazz);
		}

		enhancer.setCallback(new HgInterceptor(entityCfg, elementOps));

		@SuppressWarnings("unchecked")
		T t = (T) enhancer.create();

		if (t instanceof UsesContextAccess) {
			((UsesContextAccess) t).setContextAccess((ContextAccess) elementOps);
		}

		return t;
	}

	@Autowired
	public void setMappingConfig(MappingConfig mappingConfig) {
		this.mappingConfig = mappingConfig;
	}

}
