package com.nxquant.exchange.base.test;

import com.nxquant.exchange.base.entity.MemoryStorage;
import com.nxquant.exchange.base.lifecycle.beanlife.CustomBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import jakarta.annotation.PostConstruct;

@Configuration
public class TestScan {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    void run(){
        logger.info("PostConstruct is triggered, timestamp={}", System.currentTimeMillis());
    }

    //@Bean是作用在方法上的注解
    //@Lazy注解后就会延迟加载，在Spring容器启动的时候并不会加载，而是在当你第一次使用此bean的时候才会加载(只加载一次)
    @Lazy
    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public CustomBean customBean(){
        logger.info("Load CustomBean");
        return new CustomBean();
    }

    @Bean
    public MemoryStorage memoryStorage(){
        return new MemoryStorage();
    }

    //开启就会报错
    /*
    @Bean
    public EsStorage esStorage(){
        return new EsStorage();
    }
    */
}
