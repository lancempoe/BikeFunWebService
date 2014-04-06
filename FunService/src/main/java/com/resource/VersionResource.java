package com.resource;

import com.db.MongoDatabase;
import com.google.common.collect.Lists;
import com.model.Location;
import com.model.ServiceVersion;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/*
 * @author: tneuwerth
 * @created 7/16/13 8:11 PM
 */

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {
    private static final String apiVersion = "1.0.3";
    private static final String releaseDate = Long.toString(DateTime.now().getMillis());
    private static final String versionIdentifier = apiVersion + "-" + releaseDate;

    @GET
    @Path("/version")
    public Response getVersionIdentifier() throws Exception {
        ServiceVersion serviceVersion = new ServiceVersion();
        serviceVersion.version = versionIdentifier;
        return Response.status(Response.Status.OK).entity(serviceVersion).build();
    }
}
