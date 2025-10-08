package com.pavel.store.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class CommonAspect {

    @Pointcut("within(com.pavel.store.service.*Service)")
    public void inService() {
    }

    @Pointcut("inService() && @annotation(com.pavel.store.aop.MethodTime)")
    public void methodTimeAnnotation() {
    }

}
