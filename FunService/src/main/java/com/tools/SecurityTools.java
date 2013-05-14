package com.tools;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccount;
import com.model.User;
import com.settings.ForeignIdType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

public class SecurityTools {

    private static final Log LOG = LogFactory.getLog(SecurityTools.class);

    /*
	 * Used to validate that a valid AnonymousUser is being used.
	 */
	public static boolean isValidAnonymousUser(String userId, String key, String deviceUUID) {

		try {
			MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			AnonymousUser anonymousUser = auCollection.findOne(new ObjectId(userId)).as(AnonymousUser.class);
			if (anonymousUser != null && anonymousUser.deviceAccount.key.equals(key) && anonymousUser.deviceAccount.deviceUUID.equals(deviceUUID)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    public static boolean isValidUser(String userId, String deviceUUID) {
        try {
            MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
            User user = usersCollection.findOne(new ObjectId(userId)).as(User.class);

            if (user != null) {
                for (DeviceAccount deviceAccount : user.deviceAccounts) {
                    if (deviceAccount != null && deviceAccount.deviceUUID.equals(deviceUUID)) {
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

    public static boolean isLoggedIn(User submittedUser) {
        boolean loggedIn = false;
        try {
            if (submittedUser != null &&
                submittedUser.oAuth != null &&
                !StringUtils.isEmpty(submittedUser.oAuth.foreignId)  &&
                !StringUtils.isEmpty(submittedUser.oAuth.foreignIdType) &&
                !StringUtils.isEmpty(submittedUser.oAuth.accessToken)) {

                switch (ForeignIdType.fromName(submittedUser.oAuth.foreignIdType)) {
                    case FaceBook:
                        GoogleTokeninfoApiHelper tokenAPI = new GoogleTokeninfoApiHelper();
                        loggedIn = tokenAPI.isLoggedIn(submittedUser.oAuth);
                        break;
                    case Google:

                        break;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return loggedIn;
    }
}
