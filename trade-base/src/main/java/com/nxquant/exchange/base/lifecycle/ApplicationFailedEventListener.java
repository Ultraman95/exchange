package com.nxquant.exchange.base.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ApplicationFailedEvent applicationFailedEvent) {
        //程序启动失败时，触发的
        logger.error("ApplicationFailedEvent is triggered, timestamp={}", applicationFailedEvent.getTimestamp());
    }
}
