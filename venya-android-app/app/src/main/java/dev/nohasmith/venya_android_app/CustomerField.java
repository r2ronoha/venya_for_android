package dev.nohasmith.venya_android_app;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by arturo on 08/03/2017.
 */

public class CustomerField implements Serializable {
    private String field;
    private String type;
    private Object value;
    private int fix;

    public CustomerField(String type, Object value, int fix) {
        //this.field = field;
        this.type = type;
        this.value = value;
        this.fix = fix;
    }

    // GETTERS

    public String getField() {
        return field;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getFix() {
        return fix;
    }

    // SETTERS

    public void setField(String field) {
        this.field = field;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setFix(int fix) {
        this.fix = fix;
    }
}
