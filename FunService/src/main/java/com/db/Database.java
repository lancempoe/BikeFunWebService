package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	public Connection Get_Connection() throws Exception
	{
		try
		{
			String connectionURL = "jdbc:mysql://localhost:3306/DBName";
			Connection connection = null;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "DB_ID", "");
			return connection;
		}
		catch (SQLException e)
		{
			throw e;	
		}
		catch (Exception e)
		{
			throw e;	
		}
	}

}
