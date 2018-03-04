package com.fhr.concurrentdemo.util.parray;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

public class ParrallelArrays {

	private ParrallelArrays() {

	}

	private static final int DEFAULT_PARALLELISM = Runtime.getRuntime().availableProcessors() + 1;

	public int[] sort(int[] data) {
		return sort(data, DEFAULT_PARALLELISM);
	}

	public int[] sort(int[] data, int parallelism) {
		if (data == null || data.length < 2) {
			return data;
		}
		// 任务提交
		@SuppressWarnings("unchecked")
		Future<Void> future = (Future<Void>) submitInPool(parallelism, new QuickSortAction(0, data.length - 1, data));
		// 阻塞获取结果，如果出现错误则抛出自定义异常
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException("并行计算出错", e);
		}
		return data;
	}

	/**
	 * 提交任务
	 * 
	 * @param parallelism
	 * @param futureTask
	 * @return
	 */
	private Future<?> submitInPool(int parallelism, ForkJoinTask<?> futureTask) {
		// 对于CPU密集型任务，工作线程数等于cpu核数或者核数+1，速度最佳
		ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism == 0 ? DEFAULT_PARALLELISM : parallelism);
		// 提交根任务
		return forkJoinPool.submit(futureTask);
	}

	public int max(int[] data) {
		return max(data, DEFAULT_PARALLELISM);
	}

	public int max(int[] data, int parallelism) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("data must not be empty");
		}
		// 任务提交
		@SuppressWarnings("unchecked")
		Future<Integer> future = (Future<Integer>) submitInPool(parallelism,
				new CompareExtremeAction(0, data.length - 1, data, (o1, o2) -> o1 - o2));
		// 阻塞获取结果，如果出现错误则抛出自定义异常
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException("并行计算出错", e);
		}
	}

	public int min(int[] data) {
		return max(data, DEFAULT_PARALLELISM);
	}

	public int min(int[] data, int parallelism) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("data must not be empty");
		}
		// 任务提交
		@SuppressWarnings("unchecked")
		Future<Integer> future = (Future<Integer>) submitInPool(parallelism,
				new CompareExtremeAction(0, data.length - 1, data, (o1, o2) -> o2 - o1));
		// 阻塞获取结果，如果出现错误则抛出自定义异常
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException("并行计算出错", e);
		}
	}

	public int sum(int[] data) {
		return max(data, DEFAULT_PARALLELISM);
	}

	public int sum(int[] data, int parallelism) {
		if (data == null||data.length == 0) {
			throw new IllegalArgumentException("data must not be empty");
		}
		// 任务提交
		@SuppressWarnings("unchecked")
		Future<Integer> future = (Future<Integer>) submitInPool(parallelism, new SumAction(0, data.length -1, data));
		// 阻塞获取结果，如果出现错误则抛出自定义异常
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException("并行计算出错", e);
		}
	}

}
