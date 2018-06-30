package com.fhr.qps.reducefreq;

import com.google.common.base.Preconditions;
import redis.clients.jedis.Jedis;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:适用于集群的降频策略，基于redis的setnx操作实现
 *  * 优点是可以在集群当中使用，服务器关机不丢失信息，没有竞态条件
 *  * 缺点如下：
 *  * 1.需要增加维护和服务器成本
 */
public class RedisReduceFrequencyStrategy implements IReduceFrequencyStrategy {
	/**
	 * 缺省间隙时间
	 */
	private static final int DEFAULT_INTERVEL_TIME = 1000 * 1000;
	/**
	 * 缺省缓存前缀
	 */
	private static final String DEFAULT_CACHE_PREFIX = "ReduceFrequency";
	/**
	 * redis客户端
	 */
	private final Jedis jedis;
	/**
	 * 间歇时间
	 */
	private final int intervelTime;
	/**
	 * 缓存前缀
	 */
	private final String cachePrefix;

	public RedisReduceFrequencyStrategy(Jedis jedis) {
		this(jedis,DEFAULT_INTERVEL_TIME , DEFAULT_CACHE_PREFIX);
	}

	public RedisReduceFrequencyStrategy(Jedis jedis,int intervelTime) {
		this(jedis, intervelTime, DEFAULT_CACHE_PREFIX);
	}

	public RedisReduceFrequencyStrategy(Jedis jedis,String cachePrefix) {
		this(jedis,DEFAULT_INTERVEL_TIME, cachePrefix);
	}

	public RedisReduceFrequencyStrategy( Jedis jedis,int intervelTime, String cachePrefix) {
		Preconditions.checkNotNull(jedis,"jedis must no be null");
		Preconditions.checkNotNull(intervelTime,"intervelTime must no be null");
		Preconditions.checkNotNull(cachePrefix,"cachePrefix must no be null");

		this.jedis = jedis;
		this.intervelTime = intervelTime;
		this.cachePrefix = cachePrefix;
	}

	@Override
	public boolean shouldAction(long id) {
		// setnx操作
		if ("OK".equals(jedis.set(cachePrefix + id, "1", "nx", "px", intervelTime))) {
			return true;
		}

		return false;
	}


	@Override
	public String toString() {
		return "RedisReduceFrequencyStrategy{" +
				"intervelTime=" + intervelTime +
				", jedis=" + jedis +
				", cachePrefix='" + cachePrefix + '\'' +
				'}';
	}
}
