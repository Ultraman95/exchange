package com.nxquant.exchange.base.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//我们可以通过Order注解或者使用Ordered接口来指定调用顺序，@Order()中的值越小，优先级越高
@Component
@Order(1)
public class CommandRunner implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        logger.info("CommandRunner is triggered, timestamp={}", System.currentTimeMillis());
    }
}
