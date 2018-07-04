package com.fhr.delaytask.simple;

import java.io.Serializable;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:延迟任务执行池
 */
public interface DelayTaskExecutor<V extends Serializable> {

	TaskHandler<V>  getTaskHandler();

	void submit(Task<V> task);

	void shutdown();

	int getParallelism();
}
