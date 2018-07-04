package com.fhr.delaytask.simple.quartz;

import com.fhr.delaytask.simple.Task;
import com.fhr.delaytask.simple.TaskHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.Serializable;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:job
 */
public class DelayJob<T extends Serializable> implements Job {

	private final Task<T> task;

	private final TaskHandler<T> taskHandler;

	public DelayJob(Task<T> task, TaskHandler<T> taskHandler) {
		this.task = task;
		this.taskHandler = taskHandler;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		taskHandler.handle(task);
	}
}
