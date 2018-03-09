package com.fhr.concurrentdemo.distributedlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * 利用MySQL行锁实现分布式锁
 * 
 * @author HuaRanFan
 *
 */
public class MySQLPrimaryLock {
	private static DataSource dataSource;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// String url = "jdbc:mysql://10.0.0.212:3308/dbwww_lock?user=lock_admin&password=lock123";
		// datasource初始化逻辑
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * 加锁
	 * 
	 * @param lockID
	 */
	public void lock(String lockID) {
		acquire(lockID);
	}

	/**
	 * 获取锁
	 * 
	 * @param lockID
	 * @return
	 */
	public boolean acquire(String lockID) {
		String sql = "insert into test_lock('id','count','thName','addtime') VALUES (?,?,?,?)";
		
		while (true) {
			try {
				PreparedStatement statement = getConnection().prepareStatement(sql);
				statement.setString(1, lockID);
				statement.setInt(2, 1);
				statement.setLong(1, System.currentTimeMillis());
				boolean ifsucess = statement.execute();// 如果成功，那么就是获取到了锁
				
				if (ifsucess)
					return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			continue;
		}
	}

	/**
	 * 超时获取锁
	 * 
	 * @param lockID
	 * @param timeOuts
	 * @return
	 * @throws InterruptedException
	 */
	public boolean acquire(String lockID, long timeOuts) throws InterruptedException {
		String sql = "insert into test_lock('id','count','thName','addtime') VALUES (?,?,?,?)";
		
		long futureTime = System.currentTimeMillis() + timeOuts;
		long ranmain = timeOuts;
		long timerange = 500;
		while (true) {
			CountDownLatch latch = new CountDownLatch(1);
			try {
				PreparedStatement statement = getConnection().prepareStatement(sql);
				statement.setString(1, lockID);
				statement.setInt(2, 1);
				statement.setLong(1, System.currentTimeMillis());
				boolean ifsucess = statement.execute();// 如果成功，那么就是获取到了锁
				if (ifsucess)
					return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			latch.await(timerange, TimeUnit.MILLISECONDS);
			ranmain = futureTime - System.currentTimeMillis();
			if (ranmain <= 0)
				break;
			if (ranmain < timerange) {
				timerange = ranmain;
			}
			continue;
		}
		return false;

	}

	/**
	 * 释放锁
	 * 
	 * @param lockID
	 * @return
	 * @throws SQLException
	 */
	public boolean unlock(String lockID) throws SQLException {
		String sql = "DELETE from test_lock where id = ?";
		PreparedStatement statement = getConnection().prepareStatement(sql);
		statement.setString(1, lockID);
		boolean ifsucess = statement.execute();
		if (ifsucess)
			return true;
		return false;

	}

}
