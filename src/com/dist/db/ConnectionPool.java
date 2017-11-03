package com.dist.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * Singleton class for pooling jdbc connections using the c3p0 library
 * @author Dean Gaffney
 */

public class ConnectionPool {

	private ComboPooledDataSource comboPooledDataSource;
	
	//Database Details
	private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	private static final String SERVER_NAME = "localhost";
	private static final int PORT_NUMBER = 3306;
	private static final String DB_NAME = "gradedatabase";
	public static final String DB_URL ="jdbc:mysql://" + SERVER_NAME + ":" + PORT_NUMBER + "/" + DB_NAME;
	public static final String DB_USER = "root";
	public static final String DB_PASS = "mysql";

	private ConnectionPool() {
		try {
			comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setDriverClass(DRIVER_NAME);
			comboPooledDataSource.setJdbcUrl(DB_URL);
			comboPooledDataSource.setUser(DB_USER);
			comboPooledDataSource.setPassword(DB_PASS);
		}catch (PropertyVetoException ex1) {
			ex1.printStackTrace();
		}
	}
	
	/**
	 * Holder forces the singleton pattern to be thread safe, see here - https://stackoverflow.com/questions/16106260/thread-safe-singleton-class
	 * @author Dean Gaffney
	 */
	private static class Holder{
		private static final ConnectionPool INSTANCE = new ConnectionPool();
	}

	/**
	 * Gets the singleton instance
	 * @return ConnectionPool instance
	 */
	public static ConnectionPool getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * Gets a connection from the pool
	 * @return A JDBC connection
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			con = comboPooledDataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}

