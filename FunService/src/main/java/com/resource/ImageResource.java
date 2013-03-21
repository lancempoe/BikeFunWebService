package com.resource;

import com.model.Image;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.tools.ImageHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("/images")
@Produces(MediaType.MULTIPART_FORM_DATA)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class ImageResource {

	private static final Logger LOG = Logger.getLogger(ImageResource.class.getCanonicalName());
    private static final String BikeRideImageLocation = "FunService/Images/BikeRides/";
    private static final String UserImageLocation = "FunService/Images/Users/";

	@POST
	@Path("bikeRide/upload")
	public Response newBikeRideImage(@FormDataParam("file") InputStream uploadedInputStream,
                             @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadDirectory = System.getProperty("user.dir") + BikeRideImageLocation;
        String uploadedFileLocation = uploadDirectory + fileDetail.getFileName();

        return saveImage(uploadedInputStream, uploadedFileLocation);
	}

    @POST
    @Path("user/upload")
    public Response newUserImage(@FormDataParam("file") InputStream uploadedInputStream,
                                     @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadDirectory = System.getProperty("user.dir") + UserImageLocation;
        String uploadedFileLocation = uploadDirectory + fileDetail.getFileName();

        return saveImage(uploadedInputStream, uploadedFileLocation);
    }


    private Response saveImage(InputStream uploadedInputStream, String uploadedFileLocation) {
        Response response;
        try {
            LOG.log(Level.FINEST, "Received POST XML/JSON Request. Upload image request");

            // save it
            ImageHelper.writeToFile(uploadedInputStream, uploadedFileLocation);

            String output = "File uploaded to : " + uploadedFileLocation;

            response = Response.status(Response.Status.OK).entity(output).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE,  e.getMessage());
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity(e.getMessage()).build();
        }
        return response;
    }

	@GET
	public List<Image> getImage(@PathParam("imageName") List<Image> images) throws Exception {
		try
		{			
			//TODO WORK IN PROGRESS
			// http://stackoverflow.com/questions/9204287/how-to-return-a-png-image-from-jersey-rest-service-method-to-the-browser
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} 
		return images;
	}


}
