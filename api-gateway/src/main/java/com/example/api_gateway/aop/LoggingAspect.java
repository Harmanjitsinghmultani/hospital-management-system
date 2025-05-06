package com.example.api_gateway.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.cloud.gateway.filter.GlobalFilter+.filter(..))")
    public Mono<Void> logGlobalFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerWebExchange exchange = (ServerWebExchange) joinPoint.getArgs()[0];
        GatewayFilterChain chain = (GatewayFilterChain) joinPoint.getArgs()[1];

        String requestPath = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();

        // Get route information if available
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";
        URI routeUri = route != null ? route.getUri() : null;

        logger.info("🌐 [GATEWAY] Incoming request: {} {} | Route: {} → {}",
                method, requestPath, routeId, routeUri);

        long startTime = System.currentTimeMillis();

        return ((Mono<Void>) joinPoint.proceed())
                .doOnSuccess(v -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int status = Optional.ofNullable(exchange.getResponse().getStatusCode())
                            .map(code -> code.value())
                            .orElse(0);

                    logger.info("✅ [GATEWAY] Completed: {} {} | Status: {} | Time: {}ms",
                            method, requestPath, status, duration);
                })
                .doOnError(throwable -> {
                    logger.error("❌ [GATEWAY] Error processing: {} {} | Error: {}",
                            method, requestPath, throwable.getMessage());
                });
    }

    @Around("execution(* com.example.api_gateway.config..*(..))")
    public Object logConfigMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug("⚙️ [CONFIG] Executing: {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        logger.debug("⚙️ [CONFIG] Completed: {}.{}() in {}ms",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                duration);

        return result;
    }
}