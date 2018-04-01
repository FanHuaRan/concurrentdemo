package com.fhr.concurrentdemo.swing;

import java.util.concurrent.*;

/**
 * @author FanHuaran
 * @description 支持取消、完成等、进度等回调的task组件，基于装饰器模式,类似于SwingWorker
 * @create 2018-04-02 00:34
 * @param  <V>
 **/
public abstract class SwingBackgroundTask<V> implements RunnableFuture<V> {
    /**
     * 委托组件
     */
    private final FutureTask<V> computation = new Computation();

    /**
     * 核心函数，里面会调用setProgress
     *
     * @return
     */
    protected abstract V compute();

    /**
     * 任务完成回调函数
     *
     * @param result
     * @param throwable
     * @param canceled
     */
    protected abstract void onCompletion(V result, Throwable throwable, boolean canceled);

    /**
     * 任务更新回调函数
     *
     * @param current
     * @param max
     */
    protected abstract void onProgress(int current, int max);

    /**
     * 设置进度
     *
     * @param current
     * @param max
     */
    protected void setProgress(final int current, final int max) {
        GuiExecutor.getGuiExecutor().execute(() -> {
            onProgress(current, max);
        });
    }

    /**
     * computaion类有回调逻辑
     */
    private class Computation extends FutureTask<V> {
        public Computation() {
            super(() -> SwingBackgroundTask.this.compute());
        }

        @Override
        protected final void done() {
            GuiExecutor.getGuiExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    V value = null;
                    Throwable throwable = null;
                    boolean cancel = false;
                    try {
                        value = get();
                    } catch (ExecutionException e) {
                        throwable = e.getCause();
                    } catch (CancellationException e) {
                        cancel = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        onCompletion(value, throwable, cancel);
                    }
                }
            });
        }
    }

    /******************其它方法直接委托*********************/

    @Override
    public final void run() {
        computation.run();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return computation.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return computation.isCancelled();
    }

    @Override
    public boolean isDone() {
        return computation.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return computation.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return computation.get(timeout, unit);
    }
}
