package com.fitness.activityservice.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApiVersionInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiVersionInterceptor.class);
    private static final String VERSION_HEADER = "API-Version";
    private static final String DEFAULT_VERSION = "1.0";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiVersion = request.getHeader(VERSION_HEADER);
        if (apiVersion == null) {
            apiVersion = DEFAULT_VERSION;
        }
        request.setAttribute("API_VERSION", apiVersion);
        logger.debug("API Version: {}", apiVersion);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        response.setHeader(VERSION_HEADER, request.getAttribute("API_VERSION").toString());
    }
}
