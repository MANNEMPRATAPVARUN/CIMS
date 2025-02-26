package ca.cihi.cims;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * You can just retry within the Class Service rather than adding an aspect.  
 * However if you do decide to implement retry logic, add this to the config
 * 
 * 	<aop:aspectj-autoproxy />
	
	<bean id="classServiceAspect" class="ca.cihi.cims.ClassServiceAspect" />
	
	
 * @author HLee
 *
 */
@Aspect
public class ClassServiceAspect {

	// @Before("execution(* ca.cihi.cims.dal.ClassService.getCachedClassId(..))")
	// public void logBefore(JoinPoint joinPoint) {
	// System.err.println("!! hijacked : " + joinPoint.getSignature().getName());
	// }

	@Around("execution(* ca.cihi.cims.dal.ClassService.getCachedClassId(..))")
	public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
		
		int numAttempts = 0;
		Exception e = null;
		
		while (numAttempts <= 1) {
			numAttempts++;
			
			try {
				return pjp.proceed();
			} catch (Exception ex) {
				e = ex;
			}
		}	
		
		throw e;
	}

}
