package com.fhr.qps.reducefreq;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:适用于本地的降频策略，基于Guava缓存实现
 * 优点是简单暴力，而且基本可以解决问题
 * 缺点如下：
 * 1.无法在集群条件下使用，只是单机版（无法维持集群间的一致性）
 * 2.服务器重启，会丢失信息从而造成重发
 * 3.有竞态条件，当多个id同时就都会通过
 */
public class GuavaCacheReduceFrequencyStrategy implements IReduceFrequencyStrategy {
	/**
	 * 缺省间隙时间，1000S
	 */
	private static final int DEFAULT_INTERVEL_TIME = 1000 * 1000;
	/**
	 * 间隙时间
	 */
	private final int intervelTime;
	/**
	 * 缓存容器，key为id,value全部为Boolean.true,实际上就是一个标志位
	 */
	private final Cache<Long, Boolean> actionCache;

	public GuavaCacheReduceFrequencyStrategy(){
		this(DEFAULT_INTERVEL_TIME);
	}

	public GuavaCacheReduceFrequencyStrategy(int intervelTime) {
		Preconditions.checkNotNull(intervelTime,"intervelTime must no be null");

		this.intervelTime = intervelTime;
		actionCache = CacheBuilder.newBuilder()
				.expireAfterWrite(intervelTime, TimeUnit.MILLISECONDS)
				.build();
	}

	@Override
	public boolean shouldAction(long id) {
		Preconditions.checkNotNull(id,"id must not be null");

		// id过期则可以发生动作，也有竞态条件
		if (actionCache.getIfPresent(id) == null) {
			actionCache.put(id, Boolean.TRUE);
			return true;
		}

		return false;
	}
}
