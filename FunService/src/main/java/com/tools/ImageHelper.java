package com.tools;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lancepoehler
 * Date: 4/12/13
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageHelper {

    private static final Logger LOG = Logger.getLogger(ImageHelper.class.getCanonicalName());
    public static final String defaultBikeRideImage = "defaultBikeRide.jpg";
    public static final String defaultUserImage = "defaultUser.jpg";

    public boolean deleteImage(String fileLocation) {

        //Do not delete the default images
        if (StringUtils.isBlank(fileLocation) ||
            fileLocation.contains(defaultBikeRideImage) ||
            fileLocation.contains(defaultUserImage)) { return true; }

        boolean success = false;
        try {
            LOG.log(Level.FINEST, "Received Request. Delete image request");

            File file = new File(fileLocation);

            if(file.delete()){
                System.out.println(file.getName() + " is deleted!");
                success = true;
            }else{
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,  e.getMessage());
            e.printStackTrace();
        }
        return success;
    }

}
