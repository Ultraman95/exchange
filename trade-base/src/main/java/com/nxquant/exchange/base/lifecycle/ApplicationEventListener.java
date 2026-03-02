package com.nxquant.exchange.base.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 总的事件监听,可以去掉其余监听
 */
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationStartedEvent){
            //获取上下文
            ApplicationContext applicationContext = ((ApplicationStartedEvent) event).getApplicationContext();
            logger.info("ApplicationEventListener is triggered, ApplicationStartedEvent, timestamp={}", event.getTimestamp());
        }else if(event instanceof ContextClosedEvent){
            //程序异常的处理
            stop();
        }
    }

    private void stop(){
        logger.info("Application Stop !");
    }
}
