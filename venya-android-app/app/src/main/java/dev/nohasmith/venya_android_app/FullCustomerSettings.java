package dev.nohasmith.venya_android_app;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

/**
 * Created by arturo on 27/02/2017.
 */

public class FullCustomerSettings implements Parcelable{
//public class CustomerSettings implements Serializable{

    private CustomerField id;
    private final CustomerField type = new CustomerField("String","customer",1);
    private CustomerField firstname;
    private CustomerField surname;

    private CustomerField sessionid;
    private CustomerField username;
    private CustomerField password;
    private CustomerField dob;
    private CustomerField email;
    private CustomerField address;
    private CustomerField phone;
    private CustomerField times;
    private CustomerField language;
    private CustomerField notifications;
    private CustomerField location;

    private String [] customerFields;
    private String [] booleanFields;
    private String [] privateFields;
    private String [] secretFields;

    public FullCustomerSettings(Context appContext) {
        customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        booleanFields = appContext.getResources().getStringArray(R.array.booleanFields);
        privateFields = appContext.getResources().getStringArray(R.array.privateFields);
        secretFields = appContext.getResources().getStringArray(R.array.secretFields);

        this.id = new CustomerField("String","0a0a0a0a0a0a0a0a0a0a0a0a",1);
        this.sessionid = new CustomerField("String","closed",0);
        this.firstname = new CustomerField("String","N/A",1);
        this.surname = new CustomerField("String","N/A",1);

        this.email = new CustomerField("String",appContext.getResources().getString(R.string.default_email),0);
        this.language = new CustomerField("String",appContext.getResources().getString(R.string.default_lang),0);
        this.username = new CustomerField("String",appContext.getResources().getString(R.string.default_username),0);
        this.password = new CustomerField("String",appContext.getResources().getString(R.string.default_password),0);
        this.address = new CustomerField("Address",new Address(),0);
        this.phone = new CustomerField("String","N/A",0);
        this.times = new CustomerField("String","N/A",0);
        this.notifications = new CustomerField("boolean",true,0);
        this.location = new CustomerField("boolean",true,0);

        /*
        this.id = new CustomerField("id","String","0a0a0a0a0a0a0a0a0a0a0a0a",1);
        this.sessionid = new CustomerField("sessionid","String","closed",0);
        this.firstname = new CustomerField("firstname","String","N/A",1);
        this.surname = new CustomerField("surname","String","N/A",1);

        this.email = new CustomerField("email","String",appContext.getResources().getString(R.string.default_email),0);
        this.language = new CustomerField("language","String",appContext.getResources().getString(R.string.default_lang),0);
        this.username = new CustomerField("username","String",appContext.getResources().getString(R.string.default_username),0);
        this.password = new CustomerField("password","String",appContext.getResources().getString(R.string.default_password),0);
        this.address = new CustomerField("address","Address",new Address(),0);
        this.phone = new CustomerField("phone","String","N/A",0);
        this.times = new CustomerField("times","String","N/A",0);
        this.notifications = new CustomerField("notifications","boolean",true,0);
        this.location = new CustomerField("location","boolean",true,0);
        */
    }

    public FullCustomerSettings(Context appContext, String id, String firstname, String surname, String dob) {
        customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        booleanFields = appContext.getResources().getStringArray(R.array.booleanFields);
        privateFields = appContext.getResources().getStringArray(R.array.privateFields);
        secretFields = appContext.getResources().getStringArray(R.array.secretFields);

        this.id = new CustomerField("String",id,1);
        this.firstname = new CustomerField("String",firstname,1);
        this.surname = new CustomerField("String",surname,1);
        this.dob = new CustomerField("String",dob,1);

        sessionid = new CustomerField("String","closed",0);
        email = new CustomerField("String",appContext.getResources().getString(R.string.default_email),0);
        language = new CustomerField("String",appContext.getResources().getString(R.string.default_lang),0);
        username = new CustomerField("String",appContext.getResources().getString(R.string.default_username),0);
        password = new CustomerField("String",appContext.getResources().getString(R.string.default_password),0);
        address = new CustomerField("Address",new Address(),0);
        phone = new CustomerField("String","N/A",0);
        times = new CustomerField("String","N/A",0);
        notifications = new CustomerField("boolean",true,0);
        location = new CustomerField("boolean",true,0);
    }

    public void setField(String field, Object value) {
        switch(field) {
            case "sessionid":
                this.sessionid.setValue(value);
                break;
            case "username":
                this.username.setValue(value);
                break;
            case "password":
                this.password.setValue(value);
                break;
            case "language":
                this.language.setValue(value);
                break;
            case "email":
                this.email.setValue(value);
                break;
            case "phone":
                this.phone.setValue(value);
                break;
            case "address":
                this.address.setValue(value);
                break;
            case "notifications":
                this.notifications.setValue(value);
                break;
            case "location":
                this.location.setValue(value);
                break;
            case "times":
                this.times.setValue(value);
                break;
        }
    }

    public CustomerField getField(String field) {
        switch(field) {
            case "id":
                return getId();
            case "firstname":
                return this.getFirstname();
            case "surname":
                return this.getSurname();
            case "dob":
                return this.getDOB();
            case "type":
                return this.getType();
            case "sessionid":
                return this.sessionid;
            case "username":
                return this.username;
            case "password":
                return this.password;
            case "language":
                return this.language;
            case "email":
                return this.email;
            case "phone":
                return this.phone;
            case "address":
                return this.address;
            case "notifications":
                return this.notifications;
            case "location":
                return this.location;
            case "times":
                return this.times;
            default:
                return null;
        }
    }

    public Object getFieldElement(String field, String element) {
        CustomerField customerField;

        switch(field) {
            case "id":
                customerField = this.getId();
                break;
            case "firstname":
                customerField = this.getFirstname();
                break;
            case "surname":
                customerField = this.getSurname();
                break;
            case "dob":
                customerField = this.getDOB();
                break;
            case "type":
                customerField = this.getType();
                break;
            case "sessionid":
                customerField = this.sessionid;
                break;
            case "username":
                customerField = this.username;
                break;
            case "password":
                customerField = this.password;
                break;
            case "language":
                customerField = this.language;
                break;
            case "email":
                customerField = this.email;
                break;
            case "phone":
                customerField = this.phone;
                break;
            case "address":
                customerField = this.address;
                break;
            case "notifications":
                customerField = this.notifications;
                break;
            case "location":
                customerField = this.location;
                break;
            case "times":
                customerField = this.times;
                break;
            default:
                return null;
        }

        switch (element){
            case "value":
                return customerField.getValue();
            case "fix":
                return customerField.getField();
            case "type":
                return customerField.getType();
            case "field":
                return customerField.getField();
            default:
                return customerField;
        }
    }

    // SETTING REQUIRED TO SEND CUSTOMER INFOR BETWEEN ACTIVITIES

    public FullCustomerSettings (Parcel in) {
        this.id = (CustomerField)in.readSerializable();
        //this.type = in.readString();
        this.sessionid = (CustomerField)in.readSerializable();
        this.firstname = (CustomerField)in.readSerializable();
        this.surname = (CustomerField)in.readSerializable();
        this.dob = (CustomerField)in.readSerializable();
        this.email = (CustomerField)in.readSerializable();
        this.language = (CustomerField)in.readSerializable();
        this.username = (CustomerField)in.readSerializable();
        this.password = (CustomerField)in.readSerializable();
        this.address = (CustomerField)in.readSerializable();
        this.phone = (CustomerField)in.readSerializable();
        this.times = (CustomerField)in.readSerializable();
        this.notifications = (CustomerField)in.readSerializable();
        this.location = (CustomerField)in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        //dest.writeSerializable(type);
        dest.writeSerializable(sessionid);
        dest.writeSerializable(firstname);
        dest.writeSerializable(surname);
        dest.writeSerializable(dob);
        dest.writeSerializable(email);
        dest.writeSerializable(language);
        dest.writeSerializable(username);
        dest.writeSerializable(password);
        dest.writeSerializable(address);
        dest.writeSerializable(phone);
        dest.writeSerializable(times);
        dest.writeSerializable(notifications);
        dest.writeSerializable(location);
    }

    public static final Parcelable.Creator<FullCustomerSettings> CREATOR = new Creator<FullCustomerSettings>() {
        @Override
        public FullCustomerSettings createFromParcel(Parcel source) {
            return new FullCustomerSettings(source);
        }

        @Override
        public FullCustomerSettings[] newArray(int size) {
            return new FullCustomerSettings[size];
        }
    };

    // INDIVIDUAL SETTERS

    public void setSessionid (CustomerField sessionid) {
        this.sessionid = sessionid;
    }

    public void setLanguage (CustomerField language) {
        this.language = language;
    }

    public void setUsername (CustomerField username) {
        this.username = username;
    }

    public void setPassword (CustomerField password) {
        this.password = password;
    }

    public void setEmail(CustomerField email) {
        this.email = email;
    }

    public void setAddress (CustomerField address) {
        this.address = address;
    }

    public void setTimes(CustomerField times) {
        this.times = times;
    }

    public void setPhone(CustomerField phone) {
        this.phone = phone;
    }

    public void setNotifications (CustomerField notifications) {
        this.notifications = notifications;
    }

    public void setLocation(CustomerField location) {
        this.location = location;
    }

    // INDIVIDUAL GETTERS

    public CustomerField getId () {
        return this.id;
    }

    public CustomerField getSurname () {
        return this.surname;
    }

    public CustomerField getFirstname () {
        return this.firstname;
    }

    public CustomerField getDOB () {
        return this.dob;
    }

    public CustomerField getType () {
        return this.type;
    }

    public CustomerField getSessionid () {
        return this.sessionid;
    }

    public CustomerField getLanguage () {
        return this.language;
    }

    public CustomerField getUsername () {
        return this.username;
    }

    public CustomerField getPassword () {
        return this.password;
    }

    public CustomerField getEmail() {
        return this.email;
    }

    public CustomerField getAddress () {
        return this.address;
    }

    public CustomerField getTimes() {
        return this.times;
    }

    public CustomerField getPhone() {
        return this.phone;
    }

    public CustomerField getNotifications () {
        return this.notifications;
    }

    public CustomerField getlocation () {
        return this.location;
    }

    public boolean verifySessionId (String sessionid) {
        if ( this.sessionid.getValue().equals(sessionid) ) {
            return true;
        } else {
            return false;
        }
    }
}
