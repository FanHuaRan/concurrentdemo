package com.fhr.qps.limiting;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:基于Guava的RateLimiting的限流实现
 */
public class RateLimitingAccessLimitService implements AccessLimitService {

	private final  RateLimiter rateLimiter;

	/**
	 * 每秒产生的令牌数量
	 * @param rateLimit
	 */
	public RateLimitingAccessLimitService(int rateLimit) {
		Preconditions.checkState(rateLimit>=0,"ratelimit must >=0");

		rateLimiter = RateLimiter.create(rateLimit);
	}

	@Override
	public boolean enter() {
		// 获取一个令牌
		return rateLimiter.tryAcquire();

	}
}
