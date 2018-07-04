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

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TaskHandler<T> taskHandler = (TaskHandler<T>) context.getJobDetail().getJobDataMap().get(QuartzDelayConstant.HANDLER_KEY);
		Task<T> task = (Task<T>) context.getJobDetail().getJobDataMap().get(QuartzDelayConstant.TASK_KEY);

		taskHandler.handle(task);
	}
}
