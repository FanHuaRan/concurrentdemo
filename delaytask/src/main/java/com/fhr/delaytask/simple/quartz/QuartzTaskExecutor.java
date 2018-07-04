package com.fhr.delaytask.simple.quartz;

import com.fhr.delaytask.simple.DelayTaskExecutor;
import com.fhr.delaytask.simple.Task;
import com.fhr.delaytask.simple.TaskHandler;
import org.quartz.*;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Created by Huaran Fan on 2018/7/4
 *
 * @description:基于Quartz的延时任务
 */
public class QuartzTaskExecutor<V extends Serializable> implements DelayTaskExecutor<V> {
	private final TaskHandler<V> taskHandler;

	private final SchedulerFactory schedulerFactory;

	private final int parallelism;

	private final Scheduler scheduler;

	public QuartzTaskExecutor(TaskHandler<V> taskHandler, int parallelism) throws SchedulerException {
		this.taskHandler = taskHandler;
		this.parallelism = parallelism;
		this.schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
		this.scheduler = this.schedulerFactory.getScheduler();
		this.scheduler.start();
	}

	@Override
	public TaskHandler<V> getTaskHandler() {
		return this.taskHandler;
	}

	@Override
	public void submit(Task<V> task) {
		try {
			JobDetail job = JobBuilder.newJob(DelayJob.class)
					.withIdentity(String.format("job.task.%ld", task.getTaskId()))
					.build();

			job.getJobDataMap().put(QuartzDelayConstant.TASK_KEY, task);
			job.getJobDataMap().put(QuartzDelayConstant.HANDLER_KEY, taskHandler);

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(String.format("trigger.task.%ld", task.getTaskId()))
					.startNow()
					.withSchedule(simpleSchedule()
							.withIntervalInMilliseconds(task.getDelay())
							.withRepeatCount(1))
					.build();

			scheduler.scheduleJob(job, trigger);
		} catch (Exception er) {
			er.printStackTrace();
			// 错误处理
		}
	}

	@Override
	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException er) {
			er.printStackTrace();
			// 错误处理
		}
	}

	@Override
	public int getParallelism() {
		return this.parallelism;
	}
}
