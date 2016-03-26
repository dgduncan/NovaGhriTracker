package com.example.dgduncan.myapplication.backend;

import com.example.dgduncan.myapplication.backend.Models.Animal;
import com.example.dgduncan.myapplication.backend.Models.Track;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public final class OfyHelper {

    private OfyHelper() {
    }

    static {
        factory().register(Animal.class);
        factory().register(Track.class);
    }

    /**
     * Returns the Objectify service wrapper.
     * @return The Objectify service wrapper.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Returns the Objectify factory service.
     * @return The factory service.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

}
