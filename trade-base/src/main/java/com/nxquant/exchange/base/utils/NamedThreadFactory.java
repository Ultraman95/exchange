package com.nxquant.exchange.base.utils;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private static final String NAME_PATTERN = "%s-%d";
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String threadNamePrefix;
    private final boolean isDaemon;

    public NamedThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public NamedThreadFactory(String threadNamePrefix, boolean isDaemon) {
        this.threadNamePrefix = (threadNamePrefix != null && !threadNamePrefix.isEmpty()) ? threadNamePrefix : "platform";
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = String.format(Locale.ROOT, NAME_PATTERN, threadNamePrefix, threadNumber.getAndIncrement());
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(isDaemon);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
