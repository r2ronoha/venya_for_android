package dev.nohasmith.venya_android_app;

import java.util.Date;

/**
 * Created by arturo on 24/03/2017.
 */

public class MyGoogleEvent {
    private String id;
    private String status;
    private String location;
    private GoogleOrganiser organiser;
    private Date start;
    private Date end;
    private String summary;
    private GoogleProvider provider; // who created the appointment

    private class GoogleOrganiser {
        public String id;
        public String email;
        public String displayName;
    }

    private class GoogleProvider {
    }
}
