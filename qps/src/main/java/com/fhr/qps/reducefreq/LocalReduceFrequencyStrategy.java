package com.fhr.qps.reducefreq;

import com.google.common.base.Preconditions;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:适用于本地的降频策略，基于ConcurrentHashMap实现
 * 优点是简单暴力，而且基本可以解决问题
 * 缺点如下：
 * 1.无法在集群条件下使用，只是单机版（无法维持集群间的一致性）
 * 2.服务器重启，会丢失信息从而造成重发
 * 3.有竞态条件，当同一个id的两个操作在同一毫秒内完成就都会通过，不过这种可能性比较小
 * 4.轻微内存泄露，所以id数据不可以太多
 */
public class LocalReduceFrequencyStrategy implements IReduceFrequencyStrategy {
	/**
	 * 缺省间隙时间，1000S
	 */
	private static final int DEFAULT_INTERVEL_TIME = 1000 * 1000;
	/**
	 * 间歇时间
	 */
	private final int intervelTime;
	/**
	 * 存储上次发送时间，key为id,value为上次动作时间（毫秒）
	 */
	private final ConcurrentHashMap<Long, Long> lastActionTimes = new ConcurrentHashMap<>();

	public LocalReduceFrequencyStrategy(){
		this(DEFAULT_INTERVEL_TIME);
	}

	public LocalReduceFrequencyStrategy(int intervelTime) {
		Preconditions.checkNotNull(intervelTime,"intervelTime must no be null");
		this.intervelTime = intervelTime;
	}

	@Override
	public boolean shouldAction(long id) {
		Preconditions.checkNotNull(id,"id must not be null");

		// 先根据id得到动作时间
		Long lastActionTime = lastActionTimes.get(id);

		// 如果上次动作时间为null，证明未发送过
		if (lastActionTime == null) {
			// 使用put添加当前时间，如果返回的不是null，证明被其它线程抢走了工作，
			return lastActionTimes.put(id, System.currentTimeMillis()) == null;
		}

		// 如果上次动作时间不为null且尚未过期，返回false
		if ((System.currentTimeMillis() - lastActionTime) < intervelTime) {
			return false;
		}

		// 使用replace替换，如果替换成功，则可以进行动作，否则证明被其它线程抢走了工作
		return lastActionTimes.replace(id, lastActionTime, System.currentTimeMillis());
	}
}
