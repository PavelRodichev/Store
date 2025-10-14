package com.pavel.store.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MethodTimeAspect {


    @Around("com.pavel.store.aop.CommonAspect.methodTimeAnnotation()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        log.info("the beginning of the method: {} in class {} execution", methodName, className);
        Object result = null;
        boolean success = false;
        try {
            result = joinPoint.proceed();
            success = true;
            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("error when using the method {} in class {},operating time is: {} ms", methodName, className, executionTime);
            throw ex;
        } finally {
            if (success) {
                Long execution = System.currentTimeMillis() - start;
                log.info("the method: {} in class{} was successfully executed,method's operating time is:{} ms", methodName, className, execution);
            }
        }
    }

}

