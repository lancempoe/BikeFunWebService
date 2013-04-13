package com.tools;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccounts;
import com.model.User;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

public class SecurityTools {

	/*
	 * Used to validate that a valid user is being used.
	 */
	public static boolean isValidUser(String userId, String key, String deviceUUID) {

		try {
			//Check if anonymousUser 1st.
			MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			AnonymousUser anonymousUser = auCollection.findOne(new ObjectId(userId)).as(AnonymousUser.class);
			if (anonymousUser != null && anonymousUser.deviceAccounts.key.equals(key) && anonymousUser.deviceAccounts.deviceUUID.equals(deviceUUID)) {
				return true;
			}

			//check if user next.
			MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
			User user = userCollection.findOne(new ObjectId(userId)).as(User.class);
			if (user != null) {
				for (DeviceAccounts deviceAccounts : user.deviceAccounts) {
					if (deviceAccounts.key.equals(key) && deviceAccounts.deviceUUID.contains(deviceUUID)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isValidOwnerOfRide(String userId, String rideLeaderId) {
		return userId.equals(rideLeaderId);
	}
}
