package com.nxquant.exchange.base.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CrossDomain implements WebMvcConfigurer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //@CrossOrigin这个注解好像也可以做到跨越访问，可以在Controller上定义
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("AddCorsMappings is triggered, timestamp={}", System.currentTimeMillis());
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("*");
    }
}
