package com.fhr.delaytask.simple.delayqueue;

import com.fhr.delaytask.simple.Task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:Task的包装器
 */
public class TaskDelayWrapper<V extends Serializable> implements Delayed {
	private final long expireMilliSecond;

	private final Task<V> task;

	public TaskDelayWrapper(Task<V> task) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, task.getDelay());

		this.expireMilliSecond = cal.getTimeInMillis();
		this.task = task;
	}

	public int compareTo(Delayed o) {
		long d = (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
		return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
	}

	public long getDelay(TimeUnit unit) {
		Calendar cal = Calendar.getInstance();
		return expireMilliSecond - (cal.getTimeInMillis());
	}

	public long getExpireMilliSecond() {
		return expireMilliSecond;
	}

	public Task<V> getTask() {
		return task;
	}
}
