package com.fhr.concurrentdemo.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 * 
 * @author HuaRanFan
 *
 */
public interface IDistributedLock {

	/**
	 * 获取锁，不响应中断
	 * 
	 * @param lockId
	 */
	void lock(String lockId);

	/**
	 * 获取锁，响应中断
	 * 
	 * @param lockId
	 * @throws InterruptedException
	 */
	void lockInterruptibly(String lockId) throws InterruptedException;

	/**
	 * 尝试获取锁，不阻塞
	 * 
	 * @param lockId
	 * @return
	 */
	boolean tryLock(String lockId);

	/**
	 * 尝试获取锁，带超时机制
	 * 
	 * @param lockId
	 * @param time
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	boolean tryLock(String lockId, long time, TimeUnit unit) throws InterruptedException;

	/**
	 * 释放锁
	 * 
	 * @param lockId
	 */
	void unlock(String lockId);

}
