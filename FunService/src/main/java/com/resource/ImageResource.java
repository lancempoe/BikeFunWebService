package com.resource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final String url = "www.BikeFunFinder.com";
    public static final String BikeRideImageLocation = url + "/FunService/Images/BikeRides/";
    public static final String UserImageLocation = url + "/FunService/Images/Users/";

	@POST
	@Path("bikerides/upload")
	public Response newBikeRideImage(@FormDataParam("file") InputStream uploadedInputStream,
                             @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadedFileLocation = BikeRideImageLocation + fileDetail.getFileName();

        return saveImage(uploadedInputStream, uploadedFileLocation);
	}

    @POST
    @Path("users/upload")
    public Response newUserImage(@FormDataParam("file") InputStream uploadedInputStream,
                                     @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadedFileLocation = UserImageLocation + fileDetail.getFileName();

        return saveImage(uploadedInputStream, uploadedFileLocation);
    }

    private Response saveImage(InputStream uploadedInputStream, String uploadedFileLocation) {
        Response response;
        try {
            LOG.log(Level.FINEST, "Received POST XML/JSON Request. Upload image request");

            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();

            String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;

            response = Response.status(Response.Status.OK).entity(output).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE,  e.getMessage());
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity(e.getMessage()).build();
        }
        return response;
    }
}
