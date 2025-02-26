package ca.cihi.cims.converter.search.injector;

/**
 * Interface that is implemented by various bean value injectors to provide functionality of automatic injection of
 * random values to the specified bean instance
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public interface BeanValueInjector<T> {
	public void inject(T bean);
}