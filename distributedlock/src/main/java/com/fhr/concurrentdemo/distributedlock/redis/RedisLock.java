package com.fhr.concurrentdemo.distributedlock.redis;

import java.util.concurrent.TimeUnit;

import com.fhr.concurrentdemo.distributedlock.IDistributedLock;

import redis.clients.jedis.Jedis;

public class RedisLock implements IDistributedLock {
	
	private static final int DEFAULT_EXPIRED_SECOND = 100;
	
	private final Jedis jedis;
	
	private final int lockId;
	
	private final int expiredSecond;
	
	public RedisLock(Jedis jedis, int lockId, int expiredSecond) {
		super();
		this.jedis = jedis;
		this.lockId = lockId;
		this.expiredSecond = expiredSecond;
	}

	@Override
	public void lock() {
		while (true) {
			long returnFlag = jedis.setnx(String.valueOf(lockId), "1");
			if (returnFlag == 1) {
				System.out.println(Thread.currentThread().getName() + " get lock....");
			}
			System.out.println(Thread.currentThread().getName() + " is trying lock....");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub

	}

}
