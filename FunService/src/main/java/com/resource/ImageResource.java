package com.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.model.Image;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class ImageResource {

	private static final Logger LOG = Logger.getLogger(ImageResource.class.getCanonicalName());

	@POST
	@Path("new")
	public Response newImage(Image image) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New User request");

			//TODO SAVE IMAGE TO THE SERVER

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@GET
	public List<Image> getImage(@PathParam("imageName") List<Image> images) throws Exception {
		try 
		{			
			//TODO SEND BACK THE IMAGES.
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
