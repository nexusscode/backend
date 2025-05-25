package org.nexusscode.backend.global.aop.Trace;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class FlowLoggingAspect {

    private final LogTrace logTrace;

    @Around("execution(* org.nexusscode.backend..repository..*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            status = logTrace.begin("[Repository Layer] Call : " + joinPoint.getSignature());
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Around("execution(* org.nexusscode.backend..client..*(..))")
    public Object logClient(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            status = logTrace.begin("[CLIENT Layer] Call : " + joinPoint.getSignature());
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Around("execution(* org.nexusscode.backend..service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            status = logTrace.begin("[Service Layer] Call : " + joinPoint.getSignature());
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            if (status != null) {
                logTrace.exception(status, e);
            } else {
                log.error("[Service] exception occurred before trace started", e);
            }
            throw e;        }
    }
}

