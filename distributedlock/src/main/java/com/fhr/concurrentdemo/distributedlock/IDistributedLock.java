package com.fhr.concurrentdemo.distributedlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁接口
 * 
 * @author HuaRanFan
 *
 */
public interface IDistributedLock extends Lock {

	/**
	 * 获取锁，不响应中断
	 * 
	 */
	void lock();

	/**
	 * 获取锁，响应中断
	 * 
	 * @throws InterruptedException
	 */
	void lockInterruptibly() throws InterruptedException;

	/**
	 * 尝试获取锁，不阻塞
	 * 
	 * @return
	 */
	boolean tryLock();

	/**
	 * 尝试获取锁，带超时机制
	 * 
	 * @param time
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

	/**
	 * 释放锁
	 * 
	 */
	void unlock();
	
	/**
	 * 条件队列
	 * @return
	 */
	default Condition newCondition() {
		throw new UnsupportedOperationException("condition is not implement ! ");
	}
}
