package com.db;

import java.net.UnknownHostException;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public abstract class MongoDatabase {

	public static MongoClient mongoClient;
	private static final String DB_SERVER = "localhost";
	private static final int 	DB_PORT = 27017;
	private static final String DB_NAME = "Fun";

	public static enum MONGO_COLLECTIONS {
		BIKERIDES, LOCATIONS, TRACKING, USERS
	};

	/**
	 * Most none Geo db cals.
	 * @param collection
	 * @return
	 * @throws Exception
	 */
	public static MongoCollection Get_DB_Collection(MONGO_COLLECTIONS collection)
			throws Exception {
		return Get_DB_Collection(collection, false);
	}

	/**
	 * 
	 * @param collection
	 * @param geoLoc
	 * @return
	 * @throws Exception
	 */
	public static MongoCollection Get_DB_Collection(MONGO_COLLECTIONS collection, boolean geoLoc)
			throws Exception {
		MongoCollection coll;
		try {
			Jongo jongo = new Jongo(mongoClient.getDB(DB_NAME));
			coll = jongo.getCollection(collection.name());
			if (geoLoc) { coll.ensureIndex("{GeoLoc: '2d'}"); }
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return coll;
	}

	public static DB Get_DB() throws Exception {
		DB db;
		try {
			db = mongoClient.getDB(DB_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return db;
	}

	public static void ConnectToDb() throws UnknownHostException {
		mongoClient = new MongoClient(DB_SERVER, DB_PORT);
	}
}
