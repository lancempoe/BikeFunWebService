package com.tools;

import com.db.MongoDatabase;
import com.model.AnonymousUser;
import com.model.DeviceAccount;
import com.model.User;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

/**
 * Created with IntelliJ IDEA.
 * User: lancepoehler
 * Date: 5/10/13
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommonAnonymousAndUserCalls {

    public static String getAnonymousUserName(String userId) {

        try {
            MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.ANONYMOUS_USERS);
            AnonymousUser anonymousUser = auCollection.findOne(new ObjectId(userId)).as(AnonymousUser.class);
            if (anonymousUser != null) {
                return anonymousUser.userName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUserName(String userId) {
        try {
            MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.USERS);
            User user = usersCollection.findOne(new ObjectId(userId)).as(User.class);

            if (user != null) {
                return user.userName;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
