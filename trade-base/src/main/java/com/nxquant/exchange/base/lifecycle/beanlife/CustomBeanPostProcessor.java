package com.nxquant.exchange.base.lifecycle.beanlife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass() == CustomBean.class){
            logger.info("---调用[postProcessBeforeInitialization]");
        }
        return bean;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass() == CustomBean.class){
            logger.info("---调用[postProcessAfterInitialization]");
        }
        return bean;
    }
}
