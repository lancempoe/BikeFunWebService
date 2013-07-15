//package com.resource.test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.*;
//import com.resource.DisplayBySearchResource;
//import com.settings.SharedStaticValues;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GoogleGeocoderApiHelper;
//import com.tools.TrackingHelper;
//import junit.framework.TestCase;
//
//import org.apache.commons.lang.StringUtils;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import com.shared.PopulateDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//import javax.ws.rs.core.Response;
//
///**
//* Web Service must be turned on: glassfish3/bin/asadmin start-domain or tomcat
//* Start the DB as well: mongod
//* @author lancepoehler
//*
//*/
//public class TestDisplayBySearchResource extends TestCase { //extends JerseyTest {
//
//	private static final Logger LOG = Logger.getLogger(TestDisplayBySearchResource.class.getCanonicalName());
//
//	protected static final String WEB_APP_NAME = "FunService";
//	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
//	//protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
//    //protected static final String BASE_URI = "http://service.bikefunfinder.com/" + WEB_APP_NAME; //prd
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
//	public void testDisplay_By_Search() throws Exception {
//
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		Location location = new Location();
//		location.streetAddress = ("1000 SE Main St.");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//        GeoLoc geoLoc = location.geoLoc;
//
//        Query query = new Query();
//        query.query = "hide";
//        query.city = " ";
//        query.targetAudience = "";
//
////        Root root  = webResource
////                .path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
////                .type("application/json")
////                .post(Root.class, query);
//
//        MongoDatabase.ConnectToDb();
//        try
//        {
//            DisplayBySearchResource displayBySearchResource = new DisplayBySearchResource();
//            Response response = displayBySearchResource.getDisplay(query,geoLoc.latitude, geoLoc.longitude);
//            String test = "";
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        MongoDatabase.mongoClient.close();
//
//	}
//}
