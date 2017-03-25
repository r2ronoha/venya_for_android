package dev.nohasmith.venya_android_app;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;

import static dev.nohasmith.venya_android_app.MainActivity.venyaUrl;

/**
 * Created by arturo on 16/03/2017.
 */

public class Appointment implements Serializable {
    private String id;
    private String customerid;
    private String providerid;
    private long date;
    private String status;
    private long delay;
    private String googleId;
    private long duration;

    public Appointment() {
        this.id = "";
        this.customerid = "";
        this.providerid = "";
        this.date = new Date().getTime();
        this.duration = 30*60*1000; //30 in bu default (in milliseconds)
        this.status = "tentative";
        this.delay = 0;
        this.googleId = "";
    }

    public Appointment(String customerid, String providerid, long date) {
        this.customerid = customerid;
        this.providerid = providerid;
        this.date = date;
        this.duration = 30*60*1000; //30 in bu default (in milliseconds)

        this.id = "";
        this.status = "tentative";
        this.delay = 0;
        this.googleId = "";
    }

    // GETTERS

    public String getCustomerid() {
        return customerid;
    }

    public String getProviderid() {
        return providerid;
    }

    public long getDate() {
        return date;
    }

    public long getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }

    public long getDelay() {
        return delay;
    }

    public String getId() {
        return id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public Object getField(String field) {
        switch (field){
            case "id":
                return id;
            case "customerid":
                return customerid;
            case "provideris":
                return providerid;
            case "date":
                return date;
            case "duration":
                return duration;
            case "status":
                return status;
            case "delay":
                return delay;
            case "googleId":
                return googleId;
            default:
                return null;
        }
    }

    // SETTERS


    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setField(String field, Object value) {
        switch (field){
            case "id":
                this.id = (String)value;
                break;
            case "customerid":
                this.customerid = (String)value;
                break;
            case "providerid":
                this.providerid = (String)value;
                break;
            case "date":
                this.date = (long)value;
                break;
            case "duration":
                this.duration = (long)value;
                break;
            case "status":
                this.status = (String)value;
                break;
            case "delay":
                this.delay = (long)value;
                break;
            case "googleId":
                this.googleId = (String)value;
                break;
        }
    }
}
