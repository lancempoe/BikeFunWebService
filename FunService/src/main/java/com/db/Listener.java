package com.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * 
 * @author lancepoehler
 *
 */
public class Listener implements ServletContextListener {

	private static final Logger LOG = Logger.getLogger(Listener.class.getCanonicalName());

	public void contextDestroyed(ServletContextEvent arg0) {
		if (MongoDatabase.mongoClient != null) { 
			MongoDatabase.mongoClient.close();
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			MongoDatabase.ConnectToDb();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LOG.severe(e.getMessage());
		}
	}
}
