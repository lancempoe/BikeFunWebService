package com.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dao.UsersDao;
import com.model.Fun;
import com.model.User;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class UsersResource {

	@POST
	@Path("/new")
	public Response newUser(User user) {
		try {
			System.out.println("Received PUT XML/JSON Request: " + user.toString());

			UsersDao userDao = new UsersDao();
			Response response = userDao.NewUser(user);
			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return Response.status(404).build();
	}

	@GET
	@Path("{id}")
	public User getUser(@PathParam("id") int id) {
		User user = null;
		try 
		{
			UsersDao userDao = new UsersDao();
			user = userDao.GetUser(id);
			System.out.println(user);
		}
		catch (Exception e)
		{
			System.out.println("Exception Error: " + e.getMessage()); 
		}
		return user;
	}

	@GET
	public Fun getUsers() {
		Fun users = null;
		try 
		{
			UsersDao userDao = new UsersDao();
			users = userDao.GetUsers();
			System.out.println(users);
		}
		catch (Exception e)
		{
			System.out.println("Exception Error: " + e.getMessage());
		}
		return users;
	}
}
