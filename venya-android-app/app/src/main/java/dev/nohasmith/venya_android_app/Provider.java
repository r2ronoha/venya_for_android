package dev.nohasmith.venya_android_app;

import java.io.Serializable;

/**
 * Created by arturo on 15/03/2017.
 */

public class Provider implements Serializable{
    private String id;
    private String name;
    private String email;
    private Address address;
    private String phone;
    private boolean active;

    public Provider(String id) {
        this.id = id;
        this.name = "n/a";
        this.email = "n/a";
        this.address = new Address();
        this.phone = "n/a";
        this.active = true;
    }

    public Provider(String id, String name, String email, Address address, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.active =  true;
    }

    public Provider(String id, String name, String email, Address address, String phone, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.active =  active;
    }

    // GETTERS

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return active;
    }

    public Object getField(String field) {
        switch (field) {
            case "id":
                return this.id;
            case "name":
                return this.name;
            case "email":
                return this.email;
            case "address":
                return this.address;
            case "phone":
                return this.phone;
            case "active":
                return this.active;
            default:
                return null;
        }
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setField(String field, Object value) {
        switch (field) {
            case "name":
                this.name = (String)value;
                break;
            case "email":
                this.email = (String)value;
                break;
            case "address":
                this.address = (Address)value;
                break;
            case "phone":
                this.phone = (String)value;
                break;
            case "active":
                this.active = (boolean)value;
                break;
        }
    }
}
