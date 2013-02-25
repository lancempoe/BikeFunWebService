package com.resource.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gson.Gson;
import com.model.Users;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class TestFunService extends TestCase { //extends JerseyTest {

	protected static final String WEB_APP_NAME = "FunService";
	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME;
	protected static final String REST_URI = BASE_URI + "/" + "rest";

	protected ClientConfig getDefaultClientConfig() {
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return cc;	
	}

	@Test
	public void testFunServiceNewUser() {
		//Web Service must be turned on.
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		Users userObject = new Users();
		userObject.setUserName("TestUser"+ Math.random());
		userObject.setPassword("PicklePassword");
		userObject.setEmail("Pickle@the.cat");
		Gson gson = new Gson();
		String userObjectAsString = gson.toJson(userObject);

		ClientResponse response = webResource
				.path("users/new")
				.type("application/json")
				.post(ClientResponse.class, userObjectAsString);
		System.out.println(response.toString());

	}
}
