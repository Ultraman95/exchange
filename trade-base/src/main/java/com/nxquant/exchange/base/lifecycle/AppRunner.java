package com.nxquant.exchange.base.lifecycle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nxquant.exchange.base.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;

@Component
public class AppRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MainService mainWorker;

    private ExecutorService executorService;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("AppRunner is triggered, timestamp={}", System.currentTimeMillis());

        try {
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("AppLogic-%d").build();
            executorService = new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1024),namedThreadFactory);
            executorService.execute(mainWorker::start);
        } catch (Exception exp) {
            logger.error("AppRunner start failure!", exp);
            System.exit(-1);
        }

        //此处还可以创建一个风控线程--专门处理爆仓等等
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
