package com.fhr.concurrentdemo.distributedlock.redis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

/**
 * 基于Redis缓存实现的锁
 * 核心是setNx方法，setNX是Redis提供的一个原子操作，
 * 如果指定key存在，那么setNX失败，如果不存在会进行Set操作并返回成功。
 * 我们可以利用这个来实现一个分布式的锁，主要思路就是：
 * set成功表示获取锁，set失败表示获取失败，失败后需要重试。
 * 
 * @author HuaRanFan
 *
 */
public class RedisLock2 {
	private Jedis jedisCli = new Jedis("localhost", 6381);

	private int expireTime = 1;

	/**
	 * 获取锁
	 * 
	 * @param lockID
	 * @return
	 */
	public boolean lock(String lockID) {
		while (true) {
			long returnFlag = jedisCli.setnx(lockID, "1");
			if (returnFlag == 1) {
				System.out.println(Thread.currentThread().getName() + " get lock....");
				return true;
			}
			System.out.println(Thread.currentThread().getName() + " is trying lock....");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * 超时获取锁
	 * 
	 * @param lockID
	 * @param timeOuts
	 * @return
	 */
	public boolean lock(String lockID, long timeOuts) {
		long current = System.currentTimeMillis();
		long future = current + timeOuts;
		long timeStep = 500;
		CountDownLatch latch = new CountDownLatch(1);
		
		while (future > current) {
			long returnFlag = jedisCli.setnx(lockID, "1");
			if (returnFlag == 1) {
				System.out.println(Thread.currentThread().getName() + " get lock....");
				jedisCli.expire(lockID, expireTime);
				return true;
			}
			System.out.println(Thread.currentThread().getName() + " is trying lock....");
			try {
				latch.await(timeStep, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			current = current + timeStep;
		}
		
		return false;
	}

	public void unlock(String lockId) {
		long flag = jedisCli.del(lockId);
		
		if (flag > 0) {
			System.out.println(Thread.currentThread().getName() + " release lock....");
		} else {
			System.out.println(Thread.currentThread().getName() + " release lock fail....");
		}
	}
}
