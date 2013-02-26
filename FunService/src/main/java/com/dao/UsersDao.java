package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.core.Response;

import com.db.Database;
import com.model.Fun;
import com.model.User;

public class UsersDao 
{
	private static final String GET_USER_SQL = "SELECT * FROM Fun.Users WHERE id = @id;";
	private static final String GET_USERS_SQL = "SELECT * FROM Fun.Users ORDER BY id;";
	private static final String UPLOAD_USER_SQL = "INSERT INTO Fun.Users (`UserName`, `Password`, `Email`) VALUES ('@UserName', '@Password', '@Email');";

	public Response NewUser(User user) throws Exception {
		String sql = UPLOAD_USER_SQL.replace("@UserName", user.UserName);
		sql = sql.replace("@Password", user.Password);
		sql = sql.replace("@Email", user.Email);
		return New(sql);
	}

	public User GetUser(int id) throws Exception {
		String sql = GET_USER_SQL.replace("@id", Integer.toString(id));
		Fun fun = GetResults(sql);
		return (fun == null) ? null : fun.Users.get(0);
	}

	public Fun GetUsers() throws Exception {
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

	private Fun GetResults(String sql) throws Exception {
		Database database= new Database();
		PreparedStatement ps = null;
		try {
			Connection connection = database.Get_Connection();
			Fun fun = new Fun();

			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				User user = PopulateUsersModel(rs);
				fun.Users.add(user);
			}
			ps.close();
			return fun;
		} catch(Exception e)
		{
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			throw e;
		}
	}

	private User PopulateUsersModel(ResultSet rs) throws Exception {
		User user = new User();				
		user.Id = rs.getInt("id");
		user.UserName = rs.getString("UserName");
		user.Password = rs.getString("Password");
		user.Email = rs.getString("Email");
		user.JoinedTimeStamp = rs.getTimestamp("JoinedTimeStamp");
		user.RidesAddedCount = rs.getInt("RidesAddedCount");
		user.RidesJoinedCount = rs.getInt("RidesJoinedCount");
		user.ReadTipsForRideLeaders = rs.getBoolean("ReadTipsForRideLeaders");
		user.ReadTermsOfAgreement = rs.getBoolean("ReadTermsOfAgreement");
		return user;
	}
}