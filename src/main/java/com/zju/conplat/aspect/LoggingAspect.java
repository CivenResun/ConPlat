package com.zju.conplat.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Component解析到该切面  Pointcut和Around确定切入点
 * 执行时间
 * @author civeng
 */
@Component
@Aspect
public class LoggingAspect {
    private final static Logger LOGGER = LogManager.getLogger(LoggingAspect.class);

    @Pointcut("execution(public * com.zju.conplat.dao.*.*(..))")
    private void packageDaoPointcut(){
    }

    @Around(value="packageDaoPointcut()")
    public Object aroundPackageDaoPointcut(ProceedingJoinPoint pjp){
        String methodName = pjp.getSignature().getName();
        try {
            long startTime = System.currentTimeMillis();
            Object result = pjp.proceed();
            long endTime = System.currentTimeMillis();
            LOGGER.info("Completed running method '" + methodName + "' " + "in " + (endTime - startTime) + " ms");
            return result;
        } catch (Throwable throwable) {
            LOGGER.error("Method '" + methodName + "'" + " occurs Exception " + throwable);
            throwable.printStackTrace();
        } finally {

        }
        return null;
    }
}
