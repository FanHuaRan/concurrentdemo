package com.fhr.concurrentdemo.util.executor;

import net.jcip.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author FanHuaran
 * @description 弥补shutdownNow局限性的记录中断任务的executor,基于装饰器模式/代理模式
 * @create 2018-04-01 14:28
 **/
@NotThreadSafe
public class TrackingExecutor implements ExecutorService {

    private final ExecutorService executorService;

    private final Set<Runnable> taskCancelledAtShutdown = new ConcurrentSkipListSet<>();

    public TrackingExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public List<Runnable> getCancelledTasks() {
        if (!executorService.isTerminated()) {
            throw new IllegalStateException("executor is running!");
        }
        return new ArrayList<>(taskCancelledAtShutdown);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(() -> {
            try {
                command.run();
            } finally {
                // 这儿含有竞态条件，一些被认为已经取消的任务实际上已经完成
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    taskCancelledAtShutdown.add(command);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executorService.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.invokeAny(tasks, timeout, unit);
    }
}
