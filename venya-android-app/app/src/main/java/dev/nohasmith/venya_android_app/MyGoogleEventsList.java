package dev.nohasmith.venya_android_app;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Created by arturo on 24/03/2017.
 */

public class MyGoogleEventsList implements Serializable{
    private List<Event> events;

    public MyGoogleEventsList (List<Event> events) {
        this.events = events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents () {
        return events;
    }
}
