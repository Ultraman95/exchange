package com.nxquant.exchange.base.lifecycle.beanlife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class CustomBean implements InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String name;

    public CustomBean() {
        logger.info("---调用Bean的函数(constructor)");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("---调用Bean的函数(setName/setAttribute)");
        this.name = name;
    }

    @PostConstruct
    public void postConstruct(){
        logger.info("---调用Bean的函数(postConstruct)");
    }

    //MainConfig中@Bean 的initMethod
    public void initMethod(){
        logger.info("---调用Bean的函数(initMethod)");
    }

    //InitializingBean接口的方法afterPropertiesSet
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("---调用Bean的函数(afterPropertiesSet)");
    }

    @PreDestroy
    public void preDestroy(){
        logger.info("---调用Bean的函数(preDestroy)");
    }

    //DisposableBean接口的方法destroy
    @Override
    public void destroy() throws Exception {
        logger.info("---调用Bean的函数(destroy)");
    }

    //MainConfig中@Bean的destroyMethod
    public void destroyMethod(){
        logger.info("---调用Bean的函数(destroyMethod)");
    }
}
