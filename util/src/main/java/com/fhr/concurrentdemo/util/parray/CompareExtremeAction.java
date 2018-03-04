package com.fhr.concurrentdemo.util.parray;

import java.util.Comparator;
import java.util.concurrent.RecursiveTask;

/**
 * compute sum by fork-join pool
 * 
 * @author HuaRanFan
 *
 */
public class CompareExtremeAction extends RecursiveTask<Integer> {
	private static final long serialVersionUID = 5204016123460205576L;

	// 阈值
	private static final int THRESHOLD = 2;

	private final int left;

	private final int right;

	private final int[] data;

	private final Comparator<Integer> comparator;

	public CompareExtremeAction(int left, int right, int[] data, Comparator<Integer> comparator) {
		super();
		this.left = left;
		this.right = right;
		this.data = data;
		this.comparator = comparator;
	}

	@Override
	protected Integer compute() {
		boolean shouldFork = (left - right) > THRESHOLD;
		if (shouldFork) {
			int mid = (left + right) >> 1;
			CompareExtremeAction leftAction = new CompareExtremeAction(left, mid, data, comparator);
			CompareExtremeAction rightAction = new CompareExtremeAction(mid + 1, right, data, comparator);
			leftAction.fork();
			rightAction.fork();
			int leftExtreme = leftAction.join();
			int rightExtreme = rightAction.join();
			return leftExtreme > rightExtreme ? leftExtreme : rightExtreme;
		} else {
			int extreme = data[left];
			for (int i = left + 1; i <= right; i++) {
				if (comparator.compare(extreme, data[i]) < 0) {
					extreme = data[i];
				}
			}
			return extreme;
		}
	}

}
