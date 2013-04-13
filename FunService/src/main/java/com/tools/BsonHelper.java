package com.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.LazyWriteableDBObject;
import de.undercouch.bson4jackson.BsonFactory;
import org.bson.LazyBSONCallback;

import java.io.ByteArrayOutputStream;

/**
 * The following is an example of how to use this method.  Jongo's back end is similar but much more readable.
 * 
 * //Convert Java object to BSON object
 * DBObject dbo = BsonHelper.bsonMarshall(user);
 * 
 * //Save the BSON object
 * DBCollection coll = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
 * coll.save(dbo);
 * 
 * @author lancepoehler
 */
public class BsonHelper {

	private static final ObjectMapper bsonMapper = new ObjectMapper(new BsonFactory());

	public static DBObject bsonMarshall(Object obj) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bsonMapper.writer().writeValue(output, obj);
		return new LazyWriteableDBObject(output.toByteArray(), new LazyBSONCallback());
	}

}


