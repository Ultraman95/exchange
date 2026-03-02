package com.nxquant.exchange.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * 自定义异步线程池
 * 也可以不用这么麻烦，直接在启动类上加@EnableAsync，使用系统默认的异步线程池
 */


@Configuration
@EnableAsync
public class AsyncThreadPool implements AsyncConfigurer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //当前线程数
        taskExecutor.setCorePoolSize(5);
        //最大线程数
        taskExecutor.setMaxPoolSize(120);
        //线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(10);
        //等待任务在关机时完成--表明等待所有线程执行完
        //taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        //等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
        //taskExecutor.setAwaitTerminationSeconds(60 * 15);
        //线程最大空闲时间
        //taskExecutor.setKeepAliveSeconds(300);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix("MyAsync-");
        //拒绝策略
        //taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        taskExecutor.initialize();
        logger.info("开启异步线程池");
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncExceptionHandler();
    }

    class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        //手动处理捕获的异常
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
            logger.error("捕获线程异常信息, method={}", method.getName(), throwable);
        }
    }
}
