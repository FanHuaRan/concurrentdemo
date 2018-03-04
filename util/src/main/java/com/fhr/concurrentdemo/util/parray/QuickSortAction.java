package com.fhr.concurrentdemo.util.parray;

import java.util.concurrent.RecursiveAction;
/**
 * Quick-sort action base Recursive
 * @author HuaRanFan
 *
 */
public class QuickSortAction extends RecursiveAction {
	private static final long serialVersionUID = 94235895539994898L;

	// 并行计算阈值
	private static final int THRESHOLD = 100;

	private final int low;

	private final int high;

	private final int[] data;

	public QuickSortAction(int low, int high, int[] data) {
		super();
		this.low = low;
		this.high = high;
		this.data = data;
	}

	@Override
	protected void compute() {
		// 终止条件是低位大于等于高位
		if (low < high) {
			// 分割
			int mid = partition(data, low, high);
			// 是否并行计算
			boolean shouldFork = (high - low) >= THRESHOLD;
			if (shouldFork) {
				QuickSortAction leftTask = new QuickSortAction(low, mid - 1, data);
				QuickSortAction rightTask = new QuickSortAction(mid + 1, high, data);
				// 并行计算左边
				leftTask.fork();
				// 并行计算右边
				rightTask.fork();
				// 阻塞获取结果
				leftTask.join();
				rightTask.join();
			} else {
				// 排序前一部分
				quickSortCore(data, low, mid - 1);
				// 排序后一部分
				quickSortCore(data, mid + 1, high);
			}
		}
	}

	/**
	 * 快排递归
	 * 
	 * @param data
	 * @param low
	 * @param high
	 */
	static void quickSortCore(int[] data, int low, int high) {
		// 终止条件是低位大于等于高位
		if (low < high) {
			// 分割
			int mid = partition(data, low, high);
			// 排序前一部分
			quickSortCore(data, low, mid - 1);
			// 排序后一部分
			quickSortCore(data, mid + 1, high);
		}
	}

	/**
	 * 快速排序的分割函数 目的： 小于data[low]的放在前边 大于等于data[low]的放在后边 返回data[low]最后所在位置的索引
	 * 
	 * @param data
	 * @param low
	 * @param high
	 * @return
	 */
	private static int partition(int[] data, int low, int high) {
		int i = low, j = high;
		int tmp = data[low];
		while (i < j) {
			// 从右向左找第一个小于x的数放到坐标i的位置 然后i+1
			while (i < j && data[j] >= tmp) {
				j--;
			}
			if (i < j) {
				data[i++] = data[j];
			}
			// 从左向右找第一个大于等于x的数放到右边j的位置 然后j-1
			while (i < j && data[i] < tmp) {
				i++;
			}
			if (i < j) {
				data[j--] = data[i];
			}
		}
		data[i] = tmp;// 将最中间的数的值设置为基准量
		return i;
	}
}
