package com.example.appointment_service.security;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomFilterWrapper extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(CustomFilterWrapper.class);
    private final Filter delegate;

    public CustomFilterWrapper(Filter delegate) {
        this.delegate = delegate;
        logger.info("Wrapping filter: {}", delegate.getClass().getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        delegate.doFilter(request, response, chain);
    }
}