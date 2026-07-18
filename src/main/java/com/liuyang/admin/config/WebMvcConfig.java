package com.liuyang.admin.config;

import com.liuyang.admin.interceptor.JwtInterceptor;
import com.liuyang.admin.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final PermissionInterceptor permissionInterceptor;

    @Value("${file.upload-dir:uploads}") // 将外部的值注入到Bean中
    private String uploadDir;

    public WebMvcConfig(JwtInterceptor jwtInterceptor,
                        PermissionInterceptor permissionInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.permissionInterceptor = permissionInterceptor;
    }

    private static final String[] API_AUTH_EXCLUDES = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/health",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .order(1)
                .addPathPatterns("/api/**")
                .excludePathPatterns(API_AUTH_EXCLUDES);

        registry.addInterceptor(permissionInterceptor)
                .order(2)
                .addPathPatterns("/api/**")
                .excludePathPatterns(API_AUTH_EXCLUDES);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // 配置静态资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
