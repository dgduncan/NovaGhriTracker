package com.example.dgduncan.myapplication.backend.Endpoints;

import com.example.dgduncan.myapplication.backend.Models.Animal;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
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

    static {
        ObjectifyService.register(Animal.class);
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
