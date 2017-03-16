package dev.nohasmith.venya_android_app;

import java.util.Date;

/**
 * Created by arturo on 16/03/2017.
 */

public class Appointment {
    private String customerid;
    private String providerid;
    private Date date;
    private String status;
    private int delay;

    public Appointment(String customerid, String providerid, Date date) {
        this.customerid = customerid;
        this.providerid = providerid;
        this.date = date;

        this.status = "future";
        this.delay = 0;
    }

    // GETTERS

    public String getCustomerid() {
        return customerid;
    }

    public String getProviderid() {
        return providerid;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getDelay() {
        return delay;
    }

    // SETTERS


    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
