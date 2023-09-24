package com.inrsystem.config;

import com.inrsystem.interceptor.RequestLoggingInterceptor;
import com.inrsystem.interceptor.TokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Resource
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，要声明拦截器对象和要拦截的请求
        registry.addInterceptor(new RequestLoggingInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") //所有路径都被拦截
                .excludePathPatterns("/login") // 排除用户登录请求
                .excludePathPatterns("/research/getEvent");


    }

}
