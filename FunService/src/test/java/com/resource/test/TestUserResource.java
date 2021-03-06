//package com.resource.test;
//
//import java.util.UUID;
//
//import com.model.DeviceAccount;
//import junit.framework.TestCase;
//
//import org.junit.Test;
//
//import com.model.AnonymousUser;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//public class TestUserResource extends TestCase { //extends JerseyTest {
//
//	protected static final String WEB_APP_NAME = "FunService";
//	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
//	//protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
//	protected static final String REST_URI = BASE_URI + "/" + "rest";
//
//	protected ClientConfig getDefaultClientConfig() {
//		ClientConfig cc = new DefaultClientConfig();
//		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//		return cc;
//	}
//
//	@Test
//	public void testGetAnonymousUser() throws Exception {
//		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
//		//Start the DB as well: mongod
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		String key = String.valueOf(Math.random());
//		String deviceUUID = String.valueOf(UUID.randomUUID());
//
//		AnonymousUser au = webResource
//				.path("users/anonymous/"+key+"/"+deviceUUID)
//				.type("application/json")
//				.get(AnonymousUser.class);
//
//		assertTrue(au != null);
//
//		AnonymousUser au2 = new AnonymousUser();
//		au2.deviceAccount = new DeviceAccount();
//		au2.deviceAccount.deviceUUID = au.deviceAccount.deviceUUID;
//		au2.deviceAccount.key = au.deviceAccount.key;
//
//		au2 = webResource
//				.path("users/anonymous/"+au2.deviceAccount.key+"/"+au2.deviceAccount.deviceUUID)
//				.type("application/json")
//				.get(AnonymousUser.class);
//
//		assertEquals(au2.joinedTimeStamp, au.joinedTimeStamp);
//	}
//}
