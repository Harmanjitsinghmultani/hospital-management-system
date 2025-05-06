package com.example.appointment_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.example.appointment_service.controller..*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("▶️ [CONTROLLER] Entering: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.example.appointment_service.controller..*(..))", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        String responseBody = "";

        if (result instanceof ResponseEntity) {
            responseBody = ((ResponseEntity<?>) result).getBody() != null ?
                    ((ResponseEntity<?>) result).getBody().toString() : "empty";
        } else if (result != null) {
            responseBody = result.toString();
        }

        logger.info("✅ [CONTROLLER] Exiting: {}.{}() with response: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                responseBody);
    }

    @Around("execution(* com.example.appointment_service.service.impl..*(..))")
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

    @AfterThrowing(pointcut = "execution(* com.example.appointment_service..*(..))", throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Exception ex) {
        logger.error("❌ [ERROR] In {}.{}() - Exception: {} - Stack Trace: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage(),
                ex.getStackTrace()[0].toString());
    }
}