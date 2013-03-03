package com.resource.test;

import java.util.Date;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gson.Gson;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Root;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * Web Service must be turned on: glassfish3/bin/asadmin start-domain
 * Start the DB as well: mongod
 * @author lancepoehler
 *
 */
public class TestBikeRideResource extends TestCase { //extends JerseyTest {

	private static final Logger LOG = Logger.getLogger(TestBikeRideResource.class.getCanonicalName());
	protected static final String WEB_APP_NAME = "FunService";
	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME;
	protected static final String REST_URI = BASE_URI + "/" + "rest";

	//Used with testing
	private static String bikeRideID;
	private static String bikeRideName;

	protected ClientConfig getDefaultClientConfig() {
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return cc;	
	}

	@Test
	public void testFunServiceNewBikeRide() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		BikeRide bikeRide = new BikeRide();

		bikeRide.setBikeRideName("TestBikeRide"+ Math.random());
		Date now = new Date();
		bikeRide.setStartTime(now);
		GeoLoc geoLoc = new GeoLoc();
		geoLoc.Latitude = 45.4793;
		geoLoc.Longitude = -122.6890;
		bikeRide.setGeoLoc(geoLoc);

		TestBikeRideResource.bikeRideName = bikeRide.getBikeRideName();

		ClientResponse response = webResource
				.path("bikerides/new")
				.type("application/json")
				.post(ClientResponse.class, bikeRide);

		assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
	}

	//TODO STILL IN PROGRESS

	@Test
	public void testFunServiceGetBikeRides() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		Root root = webResource
				.path("bikerides")
				.type("application/json")
				.get(Root.class);

		TestBikeRideResource.bikeRideID = root.BikeRides.get(root.BikeRides.size()-1).getId();

		Gson gson = new Gson();
		String usersAsJson = gson.toJson(root);
		LOG.fine(usersAsJson);

		assertNotNull(TestBikeRideResource.bikeRideID);
		assertEquals(TestBikeRideResource.bikeRideName, root.BikeRides.get(root.BikeRides.size()-1).getBikeRideName());
	}

	@Test
	public void testFunServiceGetBikeRide() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		BikeRide bikeRide = webResource
				.path("bikerides/" + bikeRideID)
				.type("application/json")
				.get(BikeRide.class);

		Gson gson = new Gson();
		String userAsJson = gson.toJson(bikeRide);
		LOG.fine(userAsJson);

		assertEquals(TestBikeRideResource.bikeRideName, bikeRide.getBikeRideName());
	}

	@Test
	public void testFunServiceSortByLocation() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		//Top entry
		//latitude: 45.4793;
		//Longitude = -122.6890;
		GeoLoc geoLoc = new GeoLoc();
		geoLoc.Latitude = 45.7793;
		geoLoc.Longitude = -122.4890;

		Root root = webResource
				.path("bikerides/sortBydistance")
				.type("application/json")
				.post(Root.class, geoLoc);

		Gson gson = new Gson();
		String usersAsJson = gson.toJson(root);
		LOG.fine(usersAsJson);
	}

	@Test
	public void testFunServiceSortByLocationWithMaxDistance() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		//Top entry
		//latitude: 45.4793;
		//Longitude = -122.6890;
		GeoLoc geoLoc = new GeoLoc();
		geoLoc.Latitude = 45.7793;
		geoLoc.Longitude = -122.4890;

		Root root = webResource
				.path("bikerides/sortBydistance/1")
				.type("application/json")
				.post(Root.class, geoLoc);

		Gson gson = new Gson();
		String usersAsJson = gson.toJson(root);
		LOG.fine(usersAsJson);
	}

	@Test
	public void testFunServiceDeleteBikeRide() {
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		ClientResponse response = webResource
				.path("bikerides/" + bikeRideID + "/delete")
				.type("application/json")
				.delete(ClientResponse.class);
		LOG.fine(response.toString());

		assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
	}
}
