package com.inrsystem.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request Parameters: {}", request.getParameterMap());
        logger.info("Request TOKEN: {}", request.getHeader("TOKEN"));
        // 记录更多请求信息，如Headers等

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("Response Status: {}", response.getStatus());
        // 在请求处理完成后执行，但在视图渲染前
        // 这里可以记录响应内容等
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求完成并视图渲染后执行
        // 这里可以记录请求处理耗时等信息
    }
}

