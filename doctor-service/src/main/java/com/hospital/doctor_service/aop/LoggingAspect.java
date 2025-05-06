package com.hospital.doctor_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.hospital.doctor_service.controller..*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String argsString = args.length > 0 ? args[0].toString() : "[]";

        logger.info("▶️ [CONTROLLER] Entering: {}.{}() with arguments: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                argsString);
    }

    @AfterReturning(pointcut = "execution(* com.hospital.doctor_service.controller..*(..))", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        String responseBody = "";

        if (result instanceof ResponseEntity) {
            responseBody = ((ResponseEntity<?>) result).getBody().toString();
        } else if (result != null) {
            responseBody = result.toString();
        }

        logger.info("✅ [CONTROLLER] Exiting: {}.{}() with response: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                responseBody);
    }

    @Around("execution(* com.hospital.doctor_service.service.impl..*(..))")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        logger.debug("⏳ [SERVICE] Starting: {}.{}()",
                className,
                methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        logger.debug("⏱️ [SERVICE] Completed: {}.{}() in {} ms | Result: {}",
                className,
                methodName,
                duration,
                result != null ? result.toString() : "void");

        return result;
    }

    @AfterThrowing(pointcut = "execution(* com.hospital.doctor_service..*(..))", throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Exception ex) {
        logger.error("❌ [ERROR] In {}.{}() - Exception: {} - Stack Trace: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage(),
                ex.getStackTrace()[0].toString());
    }
}