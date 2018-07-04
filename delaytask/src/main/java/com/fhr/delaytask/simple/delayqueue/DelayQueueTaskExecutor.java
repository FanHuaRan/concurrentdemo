package com.fhr.delaytask.simple.delayqueue;

import com.fhr.delaytask.simple.DelayTaskExecutor;
import com.fhr.delaytask.simple.Task;
import com.fhr.delaytask.simple.TaskHandler;
import sun.plugin.com.Dispatch;

import java.io.Serializable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:基于延迟队列的延迟任务处理器
 */
public class DelayQueueTaskExecutor<V extends Serializable> implements DelayTaskExecutor<V> {

	private final DelayQueue delayQueue = new DelayQueue();

	private final int parallelism;

	private final TaskHandler<V> taskHandler;

	private final ThreadPoolExecutor threadPoolExecutor;

	private volatile boolean stop = false;

	private final Thread dispatchThread;

	public DelayQueueTaskExecutor(int parallelism, TaskHandler<V> taskHandler) {
		this.parallelism = parallelism;
		this.taskHandler = taskHandler;
		this.threadPoolExecutor = new ThreadPoolExecutor(parallelism,parallelism,1000L,TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
		this.dispatchThread = new Thread(new DispatchTask());
	}

	@Override
	public TaskHandler<V> getTaskHandler() {
		return taskHandler;
	}

	@Override
	public void submit(Task<V> task) {
		delayQueue.add(new TaskDelayWrapper(task));
	}

	@Override
	public void shutdown() {
		this.stop = false;
		dispatchThread.interrupt();
	}

	@Override
	public int getParallelism() {
		return this.parallelism;
	}

	private final class  DispatchTask implements Runnable{
		@Override
		public void run() {
			while (!DelayQueueTaskExecutor.this.stop){
				try {
					TaskDelayWrapper<V> taskDelayWrapper = getNextTask();
					executeTask(taskDelayWrapper.getTask());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private TaskDelayWrapper<V> getNextTask() throws InterruptedException {
		return (TaskDelayWrapper)this.delayQueue.take();
	}

	private void executeTask(Task<V> task){
		this.threadPoolExecutor.execute(()->{
			getTaskHandler().handle(task);
		});
	}

}
