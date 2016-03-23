package com.example.dgduncan.myapplication.backend.Endpoints;

import com.example.dgduncan.myapplication.backend.Models.Animal;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import java.util.List;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.myapplication.dgduncan.example.com",
    ownerName = "backend.myapplication.dgduncan.example.com",
    packagePath=""
  )
)
public class AnimalEndpoint {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    static {
        ObjectifyService.register(Animal.class);
    }

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public Animal sayHi(@Named("name") String name) {
        Animal response = new Animal(name);
        //response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "getAnimal", httpMethod = "GET")
    public Animal getAnimal(@Named("name") String name)
    {

        return ofy().load().type(Animal.class).filter("name", name).first().now();
    }

    @ApiMethod(name = "getAllAnimals", httpMethod = "GET")
    public List<Animal> getAllAnimals()
    {
        return ofy().load().type(Animal.class).list();
    }

}
