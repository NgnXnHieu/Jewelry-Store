package com.example.jewelrystore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Config đường dẫn ảo tới thư mục chứa ảnh trên server
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**") // đường URL mà frontend gọi
                .addResourceLocations("file:D:/DACN/Picture/"); // thư mục thật chứa ảnh
    }
}
