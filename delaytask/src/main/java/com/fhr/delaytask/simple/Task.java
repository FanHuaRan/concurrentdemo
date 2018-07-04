package com.fhr.delaytask.simple;

import java.io.Serializable;

/**
 * Created by Huaran Fan on 2018/7/3
 *
 * @description:任务接口
 * @param <T>
 */
public interface Task<T extends Serializable> {

	long getTaskId();

	T getData();

	int getDelay();
}
