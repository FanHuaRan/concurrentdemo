package com.fhr.delaytask.simple;

import java.io.Serializable;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description: 任务处理组件
 */
public interface TaskHandler<V extends Serializable> {
	void handle(Task<V> task);
}
