package ca.cihi.cims.util.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import ca.cihi.cims.util.timer.Perf;

@Aspect
public class PerformanceLoggingAspect {
	// @Around("execution(* ca.cihi.cims.*.*(..))")
	// @Around("execution(* *(..))")
	@Around("execution(* ca.cihi.cims.bll..*.*(..)) || execution(* ca.cihi.cims.dal..*.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {


		String foo = joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName();

		Perf.start(foo);

		Object retval = joinPoint.proceed();

		Perf.stop(foo);

		return retval;
	}

	// @Before("execution(* *(..))")
	// @Before("execution(* ca.cihi.cims.bll.*.*(..))")
	public void foo() {
		System.err.println("Do nothing.");
	}
}
