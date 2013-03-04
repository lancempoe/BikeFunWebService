//package com.resource.test;
//
//import java.util.Date;
//import java.util.logging.Logger;
//
//import junit.framework.TestCase;
//
//import org.junit.Test;
//
//import com.google.gson.Gson;
//import com.model.Root;
//import com.model.User;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//public class TestUserResource extends TestCase { //extends JerseyTest {
//
//	private static final Logger LOG = Logger.getLogger(TestUserResource.class.getCanonicalName());
//	protected static final String WEB_APP_NAME = "FunService";
//	//protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
//	protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
//	protected static final String REST_URI = BASE_URI + "/" + "rest";
//
//	//Used with testing
//	private static String userID;
//	private static String userName;
//	private static Date joinedTimeStamp;
//
//	protected ClientConfig getDefaultClientConfig() {
//		ClientConfig cc = new DefaultClientConfig();
//		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//		return cc;	
//	}
//
//	@Test
//	public void testFunServiceNewUser() {
//		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
//		//Start the DB as well: mongod
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		User user = new User();
//		TestUserResource.userName = "TestUser"+ Math.random();
//		TestUserResource.joinedTimeStamp = user.getJoinedTimeStamp();
//		user.setUserName(userName);
//		user.setPassword("PicklePassword");
//		user.setEmail("Pickle@the.cat");
//		
//		ClientResponse response = webResource
//				.path("users/new")
//				.type("application/json")
//				.post(ClientResponse.class, user);
//
//		assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
//	}
//
//	@Test
//	public void testFunServiceGetUsers() {
//		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
//		//Start the DB as well: mongod
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		Root root = webResource
//				.path("users")
//				.type("application/json")
//				.get(Root.class);
//
//		TestUserResource.userID = root.Users.get(root.Users.size()-1).getId();
//
//		Gson gson = new Gson();
//		String usersAsJson = gson.toJson(root);
//		LOG.fine(usersAsJson);
//		
//		assertNotNull(TestUserResource.userID);
//		assertTrue(TestUserResource.joinedTimeStamp.equals(root.Users.get(root.Users.size()-1).getJoinedTimeStamp()));
//	}
//
//	@Test
//	public void testFunServiceGetUser() {
//		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
//		//Start the DB as well: mongod
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		User user = webResource
//				.path("users/" + userID)
//				.type("application/json")
//				.get(User.class);
//
//		Gson gson = new Gson();
//		String userAsJson = gson.toJson(user);
//		LOG.fine(userAsJson);
//
//		assertEquals(TestUserResource.userName, user.getUserName());
//	}
//
//	@Test
//	public void testFunServiceDeleteUser() {
//		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
//		//Start the DB as well: mongod
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		ClientResponse response = webResource
//				.path("users/" + userID + "/delete")
//				.type("application/json")
//				.delete(ClientResponse.class);
//		LOG.fine(response.toString());
//
//		assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
//	}
//
//}
