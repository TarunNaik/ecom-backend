package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Make sure to replace "/path/to/your/project/uploads/images/"
        // with the absolute path to your image upload directory.
        // The "file:" prefix is crucial.
        String uploadDir = "file:D:\\hands_on\\ecommerce-app\\ecom-backend\\src\\main\\resources\\images\\";

        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadDir);
    }
}
