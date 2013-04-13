package com.resource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.imageio.stream.ImageInputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("/images")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class ImageResource {

	private static final Logger LOG = Logger.getLogger(ImageResource.class.getCanonicalName());

    private static final int IMG_WIDTH = 144;
    private static final int IMG_HEIGHT = 144;

    private static final String url = "www.BikeFunFinder.com";
    private static final String localAddress = System.getProperty("catalina.base") + "/webapps";
    private static final String BikeRideImagePath = "/BikeFunFinderImages/BikeRides/";
    private static final String UserImagePath = "/BikeFunFinderImages/Users/";

    public static final String BikeRideImageLocation = localAddress + BikeRideImagePath;
    public static final String UserImageLocation = localAddress + UserImagePath;
    public static final String BikeRideImageUrl = url + BikeRideImagePath;
    public static final String UserImageUrl = url + UserImagePath;


	@POST
	@Path("bikerides/upload/{photoURL}")
	public Response newBikeRideImage(@PathParam("photoURL") String photoURL,
                                     @FormDataParam("file") InputStream uploadedInputStream,
                                     @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String uploadedFileLocation = BikeRideImageLocation + photoURL;
        return saveImage(uploadedInputStream, uploadedFileLocation);
	}

    @POST
    @Path("users/upload/{photoURL}")
    public Response newUserImage(@PathParam("photoURL") String photoURL,
                                 @FormDataParam("file") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String uploadedFileLocation = UserImageLocation + photoURL;
        return saveImage(uploadedInputStream, uploadedFileLocation);
    }

    private Response saveImage(InputStream uploadedInputStream, String uploadedFileLocation) {
        Response response;
        try {
            LOG.log(Level.FINEST, "Received POST XML/JSON Request. Upload image request");

            //Resize Image to the standard size.
            BufferedImage originalImage = ImageIO.read(uploadedInputStream);
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = resizeImage(originalImage, type);

            //Save the resized image
            int i = uploadedFileLocation.lastIndexOf('.');
            String extention = uploadedFileLocation.substring(i+1);
            ImageIO.write(resizedImage, extention, new File(uploadedFileLocation));

            String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;

            response = Response.status(Response.Status.OK).entity(output).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE,  e.getMessage());
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity(e.getMessage()).build();
        }
        return response;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

}
