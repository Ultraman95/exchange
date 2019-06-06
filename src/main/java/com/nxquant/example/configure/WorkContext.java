package com.nxquant.example.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class WorkContext {
    private final String PREFIX = "com.nxquant.example";

    private String inputTopic;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    @Bean
    @ConfigurationProperties(prefix = PREFIX + ".global")
    public WorkContext loadProperties(Environment environment) {
        return this;
    }

    private void extractPropertiesConfig(Environment environment, String prefix, Properties properties) {
        Binder.get(environment).bind(prefix, Bindable.mapOf(String.class, String.class))
                .ifBound(map -> map.entrySet().forEach(entry -> properties.merge(entry.getKey(), entry.getValue(), (ov, nv) -> nv)));
    }
}