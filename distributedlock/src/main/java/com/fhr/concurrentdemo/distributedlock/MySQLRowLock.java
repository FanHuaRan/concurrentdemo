package com.fhr.concurrentdemo.distributedlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * 使用MySQL行锁来实现分布式锁 依靠了事务，所以如果想没有侵入性，需要使用ThreadLocal来构建全局变量。
 * 
 * @author HuaRanFan
 *
 */
public class MySQLRowLock {
	private static DataSource dataSource;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// String url =
		// "jdbc:mysql://10.0.0.212:3308/dbwww_lock?user=lock_admin&password=lock123";
		// datasource初始化逻辑
	}

	private static ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<Connection>() {
		@Override
		protected Connection initialValue() {
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
	};

	private Connection getConnection() throws SQLException {
		return threadLocalConnection.get();
	}

	/**
	 * 超时获取锁
	 * 
	 * @param lockID
	 * @param timeOuts
	 * @return
	 * @throws InterruptedException
	 */
	public boolean acquireByUpdate(String lockID, long timeOuts) throws InterruptedException, SQLException {
		String sql = "SELECT id from test_lock where id = ? for UPDATE ";

		long futureTime = System.currentTimeMillis() + timeOuts;
		long ranmain = timeOuts;
		long timerange = 500;
		getConnection().setAutoCommit(false);
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
	public void unlockforUpdtate(String lockID) throws SQLException {
		getConnection().commit();
	}

}
