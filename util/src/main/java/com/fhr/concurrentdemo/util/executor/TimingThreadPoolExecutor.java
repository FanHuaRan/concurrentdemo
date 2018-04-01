package com.fhr.concurrentdemo.util.executor;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * @author FanHuaran
 * @description 带时间统计信息的线程池
 * @create 2018-04-01 14:57
 **/
@ThreadSafe
public class TimingThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Logger logger = Logger.getLogger("TimingThreadPoolExecutor");

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    private final AtomicLong numTasks = new AtomicLong(0);

    private final AtomicLong totalTime = new AtomicLong(0);

    public TimingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        logger.fine(String.format("Thread %s: start %s", t, r));
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            logger.fine(String.format("Thread %s: end %s,time=%d ns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    @Override
    protected void terminated() {
        try {
            logger.info(String.format("Terminated: avg time = %d ns", totalTime.get() / numTasks.get()));
        } finally {
            super.terminated();
        }
    }
}