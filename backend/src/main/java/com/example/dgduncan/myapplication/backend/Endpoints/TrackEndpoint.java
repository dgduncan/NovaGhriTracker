package com.example.dgduncan.myapplication.backend.Endpoints;

import com.example.dgduncan.myapplication.backend.Models.Track;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;

import java.util.List;

import javax.inject.Named;

import static com.example.dgduncan.myapplication.backend.OfyHelper.ofy;


/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "trackApi",
        version = "v1",
        resource = "track",
        namespace = @ApiNamespace(
                ownerDomain = "Models.backend.myapplication.dgduncan.example.com",
                ownerName = "Models.backend.myapplication.dgduncan.example.com",
                packagePath = ""
        )
)
public class TrackEndpoint {
    /**
     * Returns the {@link Track} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Track} with the provided ID.
     */
    /*
    @ApiMethod(
            name = "get",
            path = "track/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Track get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Track with ID: " + id);
        Track track = ofy().load().type(Track.class).id(id).now();
        if (track == null) {
            throw new NotFoundException("Could not find Track with ID: " + id);
        }
        return track;
    }*/


    /**
     * List all entities.
     *
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "getTrack",
            path = "getTrack",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<Track> getTrack(@Named("name") String name) {

        return ofy().load().type(Track.class).filter("name", name).order("sequence").list();




    }
}