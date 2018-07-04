package com.fhr.delaytask.simple.scheduledexecutor;

import com.fhr.delaytask.simple.DelayTaskExecutor;
import com.fhr.delaytask.simple.Task;
import com.fhr.delaytask.simple.TaskHandler;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:基于schedule线程池的延迟任务
 */
public class ScheduledTaskExecutor<V extends Serializable> implements DelayTaskExecutor<V> {

	private final TaskHandler<V> taskHandler;

	private final ScheduledExecutorService scheduledExecutorService;

	private final int parallelism;

	public ScheduledTaskExecutor(TaskHandler<V> taskHandler, int parallelism) {
		this.taskHandler = taskHandler;
		this.parallelism = parallelism;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(parallelism);
	}

	@Override
	public TaskHandler<V> getTaskHandler() {
		return taskHandler;
	}

	@Override
	public void submit(Task<V> task) {
		scheduledExecutorService.schedule(() -> {
			getTaskHandler().handle(task);
		}, task.getDelay(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() {
		scheduledExecutorService.shutdownNow();
	}

	@Override
	public int getParallelism() {
		return parallelism;
	}
}
