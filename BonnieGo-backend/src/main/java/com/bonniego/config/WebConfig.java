package com.bonniego.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//如果前端和後端分開部署，需要在後端配置 CORS

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500") // 確保URL和前端一致
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 添加 OPTIONS 方法
                .allowedHeaders("*") // 允許所有請求頭
                .allowCredentials(true); // 允許攜帶 Cookie
    }

    //公開上傳目錄
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }


}

