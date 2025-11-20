package com.example.ProjectWork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL:   /uploads/**
        // PATH:  file:uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        // se vuoi essere super sicuro:
        // .addResourceLocations("file:./uploads/");
    }
}
