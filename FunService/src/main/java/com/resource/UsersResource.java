package com.resource;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dao.UsersDao;
import com.google.gson.Gson;
import com.model.Users;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class UsersResource {

	@POST
	@Path("/new")
	public Response newUser(Users userObject) {
		try {
			System.out.println("Received PUT XML/JSON Request: " + userObject.toString());

			UsersDao userDao = new UsersDao();
			Response response = userDao.NewUser(userObject);
			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return Response.status(404).build();
	}

	@GET
	@Path("{id}")
	public String getUser(@PathParam("id") int id) {
		String users = null;
		try 
		{
			ArrayList<Users> userObjects = null;
			UsersDao userDao = new UsersDao();
			userObjects = userDao.GetUser(id);
			Gson gson = new Gson();
			users = gson.toJson(userObjects);
			System.out.println(users);
		}
		catch (Exception e)
		{
			System.out.println("Exception Error: " + e.getMessage()); 
		}
		return users;
	}

	@GET
	public String getUsers() {
		String users = null;
		try 
		{
			ArrayList<Users> userObjects = null;
			UsersDao userDao = new UsersDao();
			userObjects = userDao.GetUsers();
			Gson gson = new Gson();
			users = gson.toJson(userObjects);
			System.out.println(users);
		}
		catch (Exception e)
		{
			System.out.println("Exception Error: " + e.getMessage());
		}
		return users;
	}
}
