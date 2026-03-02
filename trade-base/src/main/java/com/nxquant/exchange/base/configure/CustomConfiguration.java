package com.nxquant.exchange.base.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class CustomConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String PREFIX = "com.nxquant.exchange";

    private String inputTopic;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    @Bean
    @ConfigurationProperties(prefix = PREFIX + ".global")
    public CustomConfiguration loadProperties(Environment environment) {
        logger.info("Configuration is triggered, timestamp={}", System.currentTimeMillis());
        return this;
    }

    private void extractPropertiesConfig(Environment environment, String prefix, Properties properties) {
        Binder.get(environment).bind(prefix, Bindable.mapOf(String.class, String.class))
                .ifBound(map -> map.entrySet().forEach(entry -> properties.merge(entry.getKey(), entry.getValue(), (ov, nv) -> nv)));
    }
}
