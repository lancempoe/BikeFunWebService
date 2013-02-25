package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import com.db.Database;
import com.model.Users;

public class UsersDao 
{
	private static final String GET_USER_SQL = "SELECT * FROM Fun.Users WHERE id = @id;";
	private static final String GET_USERS_SQL = "SELECT * FROM Fun.Users ORDER BY id;";
	private static final String UPLOAD_USER_SQL = "INSERT INTO Fun.Users (`UserName`, `Password`, `Email`) VALUES ('@UserName', '@Password', '@Email');";

	public Response NewUser(Users userObject) throws Exception {
		String sql = UPLOAD_USER_SQL.replace("@UserName", userObject.getUserName());
		sql = sql.replace("@Password", userObject.getPassword());
		sql = sql.replace("@Email", userObject.getEmail());
		return New(sql);
	}

	public ArrayList<Users> GetUser(int id) throws Exception {
		String sql = GET_USER_SQL.replace("@id", Integer.toString(id));
		return GetResults(sql);
	}

	public ArrayList<Users> GetUsers() throws Exception {
		return GetResults(GET_USERS_SQL);
	}

	private Response New(String sql) throws Exception {
		Database database= new Database();
		PreparedStatement ps = null;
		try {
			Connection connection = database.Get_Connection();
			ps = connection.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			return Response.status(200).build(); //success
		} catch(Exception e)
		{
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
		return Response.status(404).build();
	}

	private ArrayList<Users> GetResults(String sql) throws Exception {
		Database database= new Database();
		ArrayList<Users> users;
		PreparedStatement ps = null;
		try {
			Connection connection = database.Get_Connection();
			users = new ArrayList<Users>();

			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Users user = PopulateUsersModel(rs);
				users.add(user);
			}
			ps.close();
			return users;
		} catch(Exception e)
		{
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			throw e;
		}
	}

	private Users PopulateUsersModel(ResultSet rs) throws Exception {
		Users user = new Users();				
		user.setId(rs.getInt("id"));
		user.setUserName(rs.getString("UserName"));
		user.setPassword(rs.getString("Password"));
		user.setEmail(rs.getString("Email"));
		user.setJoinedTimeStamp(rs.getTimestamp("JoinedTimeStamp"));
		user.setRidesAddedCount(rs.getInt("RidesAddedCount"));
		user.setRidesJoinedCount(rs.getInt("RidesJoinedCount"));
		user.setReadTipsForRideLeaders(rs.getBoolean("ReadTipsForRideLeaders"));
		user.setReadTermsOfAgreement(rs.getBoolean("ReadTermsOfAgreement"));
		return user;
	}
}