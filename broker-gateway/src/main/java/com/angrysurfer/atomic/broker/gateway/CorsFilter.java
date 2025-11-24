package com.angrysurfer.atomic.broker.gateway;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Comment out the CORS filter to prevent conflicts with Spring's CORS configuration
/*
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.info("CORS Filter - Method: {}, URI: {}, Origin: {}", method, requestUri, origin);

        // Only add Vary header to indicate that response varies by Origin
        // Let Spring handle CORS properly to avoid conflicts
        response.setHeader("Vary", "Origin");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("CORS Filter - Handling preflight OPTIONS request from origin: {}", origin);
            // Let Spring handle the OPTIONS preflight request
        }

        chain.doFilter(req, res);
    }
}
*/
