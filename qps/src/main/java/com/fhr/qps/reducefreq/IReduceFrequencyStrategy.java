package com.fhr.qps.reducefreq;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:降频接口
 */
public interface IReduceFrequencyStrategy {

	/**
	 * 是否可以发生动作，降低频率的核心
	 * @param id
	 * @return
	 */
	boolean shouldAction(long id);

}
