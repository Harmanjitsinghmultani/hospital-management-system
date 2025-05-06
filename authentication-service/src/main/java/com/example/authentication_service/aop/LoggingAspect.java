package com.example.authentication_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.authentication_service.controller.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        Object[] args = joinPoint.getArgs();

        String argsString = Arrays.stream(args)
                .map(arg -> {
                    if (arg != null && arg.toString().contains("password")) {
                        return arg.toString().replaceAll("password=[^,&\\]]*", "password=****");
                    }
                    return arg != null ? arg.toString() : "null";
                })
                .collect(Collectors.joining(", "));

        log.info("▶️ [AUTH-CONTROLLER] Entering: {}.{}() with arguments: [{}]",
                className, methodName, argsString);
    }

    @AfterReturning(pointcut = "execution(* com.example.authentication_service.controller.*.*(..))",
            returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        String response = result != null ? result.toString() : "void";
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();
            response = body != null ? body.toString() : "null";
        }

        response = response.replaceAll("token=[^,&\\]]*", "token=****");

        log.info("✅ [AUTH-CONTROLLER] Exiting: {}.{}() with response: {}",
                className, methodName, response);
    }

    @Around("execution(* com.example.authentication_service.service.*.*(..))")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        log.debug("⏳ [AUTH-SERVICE] Starting: {}.{}()", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        log.debug("⏱️ [AUTH-SERVICE] Completed: {}.{}() in {} ms | Result: {}",
                className, methodName, duration,
                result != null ? result.toString() : "void");

        return result;
    }

    @AfterThrowing(pointcut = "within(com.example.authentication_service..*)",
            throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Exception ex) {
        log.error("❌ [AUTH-ERROR] In {}.{}() - Exception: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage(), ex);
    }
}