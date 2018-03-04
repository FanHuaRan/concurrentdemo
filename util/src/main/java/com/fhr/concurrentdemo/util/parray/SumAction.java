package com.fhr.concurrentdemo.util.parray;

import java.util.concurrent.RecursiveTask;

/**
 * compute sum by fork-join pool
 * 
 * @author HuaRanFan
 *
 */
public class SumAction extends RecursiveTask<Integer> {
	private static final long serialVersionUID = 5204016123460205576L;

	// 阈值
	private static final int THRESHOLD = 2;

	private final int left;

	private final int right;

	private final int[] data;

	public SumAction(int left, int right, int[] data) {
		super();
		this.left = left;
		this.right = right;
		this.data = data;
	}

	@Override
	protected Integer compute() {
		boolean shouldFork = (left - right) > THRESHOLD;
		if (shouldFork) {
			int mid = (left + right) >> 1;
			SumAction leftAction = new SumAction(left, mid, data);
			SumAction rightAction = new SumAction(mid + 1, right, data);
			leftAction.fork();
			rightAction.fork();
			int leftSum = leftAction.join();
			int rightSum = rightAction.join();
			return leftSum + rightSum;
		} else {
			int sum = 0;
			for (int i = left; i <= right; i++) {
				sum += data[i];
			}
			return sum;
		}
	}

}
