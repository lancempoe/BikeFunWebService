//package com.resource.test;
//
//import java.util.logging.Logger;
//
//import org.junit.Test;
//
//import junit.framework.TestCase;
//
//import com.shared.PopulateDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//public class TestDBHeavyLoad extends TestCase {
//
//	private static final Logger LOG = Logger.getLogger(TestDBHeavyLoad.class.getCanonicalName());
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
//	public void testLoadTestMongoDBSave() throws Exception {
//
//		int count = 10;
//		Thread[] threads = new Thread[count];
//		try {
//			for(int i = 0; i< count; i++) {
//				threads[i] = new SaveToMongoDB();
//				threads[i].start();
//			}
//			for (int i = 0; i < threads.length; i++) {
//			    try {
//			       threads[i].join();
//			    } catch (InterruptedException ignore) {
//			    	ignore.printStackTrace();
//			    }
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LOG.fine("Success");
//	}
//
//	class SaveToMongoDB extends Thread {
//		public void run () {
//			Client client = Client.create(getDefaultClientConfig());
//			client.addFilter(new LoggingFilter());
//			WebResource webResource = client.resource(REST_URI);
//			int count = 10;
//
//			PopulateDB populate = new PopulateDB();
//			try {
//				for(int i = 0; i< count; i++) {
//					populate.populateDB(webResource);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}	
//		}
//	}
//}
