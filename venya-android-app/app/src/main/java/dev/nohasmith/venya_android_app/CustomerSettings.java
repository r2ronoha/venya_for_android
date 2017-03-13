<<<<<<< HEAD
<<<<<<< HEAD
package dev.nohasmith.venya_android_app;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by arturo on 27/02/2017.
 */

public class CustomerSettings implements Parcelable{
//public class CustomerSettings implements Serializable{

    private String id;
    private final static String type = "customer";
    private String firstname;
    private String surname;

    private String dob;
    private String sessionid;
    private String username;
    private String password;
    private String email;
    private Address address;
    private String phone;
    private String times;
    private String language;
    private boolean notifications;
    private boolean location;

    public CustomerSettings(Context appContext) {
        this.id = "N/A";
        this.sessionid = "N/A";
        this.firstname = "N/A";
        this.surname = "N/A";

        this.dob = "ddmmyyyy";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    public CustomerSettings(Context appContext, String id, String firstname, String surname, String dob) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;

        this.dob = dob;
        this.sessionid = "closed";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    // SETTERS

    public void setSessionid (String sessionid) {
        this.sessionid = sessionid;
    }

    public void setLanguage (String language) {
        this.language = language;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress (Address address) {
        this.address = address;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotifications (boolean notifications) {
        this.notifications = notifications;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    // GETTERS

    public String getId () {
        return this.id;
    }

    public String getSurname () {
        return this.surname;
    }

    public String getFirstname () {
        return this.firstname;
    }

    public String getDOB () {
        return this.dob;
    }

    public String getType () {
        return this.type;
    }

    public String getSessionid () {
        return this.sessionid;
    }

    public String getLanguage () {
        return this.language;
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public Address getAddress () {
        return this.address;
    }

    public String getTimes() {
        return this.times;
    }

    public String setPhone() {
        return this.phone;
    }
    public boolean getNotifications () {
        return this.notifications;
    }

    public boolean getlocation () {
        return this.location;
    }

    public boolean verifySessionId (String sessionid) {
        if ( this.sessionid == sessionid ) {
            return true;
        } else {
            return false;
        }
    }

    public void setField(String field, Object value) {
        switch(field) {
            case "sessionid":
                this.sessionid = (String)value;
                break;
            case "username":
                this.username = (String)value;
                break;
            case "password":
                this.password = (String)value;
                break;
            case "language":
                this.language = (String)value;
                break;
            case "email":
                this.email = (String)value;
                break;
            case "phone":
                this.phone = (String)value;
                break;
            case "address":
                this.address = (Address)value;
                break;
            case "notifications":
                this.notifications = (boolean)value;
                break;
            case "location":
                this.location = (boolean)value;
                break;
            case "times":
                this.times = (String)value;
                break;
        }
    }

    public CustomerSettings (Parcel in) {
        this.id = in.readString();
        //this.type = in.readString();
        this.sessionid = in.readString();
        this.firstname = in.readString();
        this.surname = in.readString();
        this.dob = in.readString();
        this.email = in.readString();
        this.language = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.address = (Address)in.readSerializable();
        this.phone = in.readString();
        this.times = in.readString();
        this.notifications = Boolean.parseBoolean(in.readString());
        this.location = Boolean.parseBoolean(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        //dest.writeString(type);
        dest.writeString(sessionid);
        dest.writeString(firstname);
        dest.writeString(surname);
        dest.writeString(dob);
        dest.writeString(email);
        dest.writeString(language);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeSerializable(address);
        dest.writeString(phone);
        dest.writeString(times);
        dest.writeString(Boolean.toString(notifications));
        dest.writeString(Boolean.toString(location));
    }

    public static final Parcelable.Creator<CustomerSettings> CREATOR = new Creator<CustomerSettings>() {
        @Override
        public CustomerSettings createFromParcel(Parcel source) {
            return new CustomerSettings(source);
        }

        @Override
        public CustomerSettings[] newArray(int size) {
            return new CustomerSettings[size];
        }
    };

    public Object getField(String field) {
        switch(field) {
            case "id":
                return this.getId();
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
}
=======
package dev.nohasmith.venya_android_app;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by arturo on 27/02/2017.
 */

public class CustomerSettings implements Parcelable{
//public class CustomerSettings implements Serializable{

    private String id;
    private final static String type = "customer";
    private String firstname;
    private String surname;

    private String dob;
    private String sessionid;
    private String username;
    private String password;
    private String email;
    private Address address;
    private String phone;
    private String times;
    private String language;
    private boolean notifications;
    private boolean location;

    public CustomerSettings(Context appContext) {
        this.id = "N/A";
        this.sessionid = "N/A";
        this.firstname = "N/A";
        this.surname = "N/A";

        this.dob = "ddmmyyyy";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    public CustomerSettings(Context appContext, String id, String firstname, String surname, String dob) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;

        this.dob = dob;
        this.sessionid = "closed";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    // SETTERS

    public void setSessionid (String sessionid) {
        this.sessionid = sessionid;
    }

    public void setLanguage (String language) {
        this.language = language;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress (Address address) {
        this.address = address;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotifications (boolean notifications) {
        this.notifications = notifications;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    // GETTERS

    public String getId () {
        return this.id;
    }

    public String getSurname () {
        return this.surname;
    }

    public String getFirstname () {
        return this.firstname;
    }

    public String getDOB () {
        return this.dob;
    }

    public String getType () {
        return this.type;
    }

    public String getSessionid () {
        return this.sessionid;
    }

    public String getLanguage () {
        return this.language;
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public Address getAddress () {
        return this.address;
    }

    public String getTimes() {
        return this.times;
    }

    public String setPhone() {
        return this.phone;
    }
    public boolean getNotifications () {
        return this.notifications;
    }

    public boolean getlocation () {
        return this.location;
    }

    public boolean verifySessionId (String sessionid) {
        if ( this.sessionid == sessionid ) {
            return true;
        } else {
            return false;
        }
    }

    public void setField(String field, Object value) {
        switch(field) {
            case "sessionid":
                this.sessionid = (String)value;
                break;
            case "username":
                this.username = (String)value;
                break;
            case "password":
                this.password = (String)value;
                break;
            case "language":
                this.language = (String)value;
                break;
            case "email":
                this.email = (String)value;
                break;
            case "phone":
                this.phone = (String)value;
                break;
            case "address":
                this.address = (Address)value;
                break;
            case "notifications":
                this.notifications = (boolean)value;
                break;
            case "location":
                this.location = (boolean)value;
                break;
            case "times":
                this.times = (String)value;
                break;
        }
    }

    public CustomerSettings (Parcel in) {
        this.id = in.readString();
        //this.type = in.readString();
        this.sessionid = in.readString();
        this.firstname = in.readString();
        this.surname = in.readString();
        this.dob = in.readString();
        this.email = in.readString();
        this.language = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.address = (Address)in.readSerializable();
        this.phone = in.readString();
        this.times = in.readString();
        this.notifications = Boolean.parseBoolean(in.readString());
        this.location = Boolean.parseBoolean(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        //dest.writeString(type);
        dest.writeString(sessionid);
        dest.writeString(firstname);
        dest.writeString(surname);
        dest.writeString(dob);
        dest.writeString(email);
        dest.writeString(language);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeSerializable(address);
        dest.writeString(phone);
        dest.writeString(times);
        dest.writeString(Boolean.toString(notifications));
        dest.writeString(Boolean.toString(location));
    }

    public static final Parcelable.Creator<CustomerSettings> CREATOR = new Creator<CustomerSettings>() {
        @Override
        public CustomerSettings createFromParcel(Parcel source) {
            return new CustomerSettings(source);
        }

        @Override
        public CustomerSettings[] newArray(int size) {
            return new CustomerSettings[size];
        }
    };

    public Object getField(String field) {
        switch(field) {
            case "id":
                return this.getId();
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
}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
=======
package dev.nohasmith.venya_android_app;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by arturo on 27/02/2017.
 */

public class CustomerSettings implements Parcelable{
//public class CustomerSettings implements Serializable{

    private String id;
    private final static String type = "customer";
    private String firstname;
    private String surname;

    private String dob;
    private String sessionid;
    private String username;
    private String password;
    private String email;
    private Address address;
    private String phone;
    private String times;
    private String language;
    private boolean notifications;
    private boolean location;

    public CustomerSettings(Context appContext) {
        this.id = "N/A";
        this.sessionid = "N/A";
        this.firstname = "N/A";
        this.surname = "N/A";

        this.dob = "ddmmyyyy";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    public CustomerSettings(Context appContext, String id, String firstname, String surname, String dob) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;

        this.dob = dob;
        this.sessionid = "closed";
        this.email = appContext.getResources().getString(R.string.default_email);
        this.language = appContext.getResources().getString(R.string.default_lang);
        this.username = appContext.getResources().getString(R.string.default_username);
        this.password = appContext.getResources().getString(R.string.default_password);
        this.address = new Address();
        this.phone = "N/A";
        this.times = "N/A";
        this.notifications = true;
        this.location = true;
    }

    // SETTERS

    public void setSessionid (String sessionid) {
        this.sessionid = sessionid;
    }

    public void setLanguage (String language) {
        this.language = language;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress (Address address) {
        this.address = address;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNotifications (boolean notifications) {
        this.notifications = notifications;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    // GETTERS

    public String getId () {
        return this.id;
    }

    public String getSurname () {
        return this.surname;
    }

    public String getFirstname () {
        return this.firstname;
    }

    public String getDOB () {
        return this.dob;
    }

    public String getType () {
        return this.type;
    }

    public String getSessionid () {
        return this.sessionid;
    }

    public String getLanguage () {
        return this.language;
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public Address getAddress () {
        return this.address;
    }

    public String getTimes() {
        return this.times;
    }

    public String setPhone() {
        return this.phone;
    }
    public boolean getNotifications () {
        return this.notifications;
    }

    public boolean getlocation () {
        return this.location;
    }

    public boolean verifySessionId (String sessionid) {
        if ( this.sessionid == sessionid ) {
            return true;
        } else {
            return false;
        }
    }

    public void setField(String field, Object value) {
        switch(field) {
            case "sessionid":
                this.sessionid = (String)value;
                break;
            case "username":
                this.username = (String)value;
                break;
            case "password":
                this.password = (String)value;
                break;
            case "language":
                this.language = (String)value;
                break;
            case "email":
                this.email = (String)value;
                break;
            case "phone":
                this.phone = (String)value;
                break;
            case "address":
                this.address = (Address)value;
                break;
            case "notifications":
                this.notifications = (boolean)value;
                break;
            case "location":
                this.location = (boolean)value;
                break;
            case "times":
                this.times = (String)value;
                break;
        }
    }

    public CustomerSettings (Parcel in) {
        this.id = in.readString();
        //this.type = in.readString();
        this.sessionid = in.readString();
        this.firstname = in.readString();
        this.surname = in.readString();
        this.dob = in.readString();
        this.email = in.readString();
        this.language = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.address = (Address)in.readSerializable();
        this.phone = in.readString();
        this.times = in.readString();
        this.notifications = Boolean.parseBoolean(in.readString());
        this.location = Boolean.parseBoolean(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        //dest.writeString(type);
        dest.writeString(sessionid);
        dest.writeString(firstname);
        dest.writeString(surname);
        dest.writeString(dob);
        dest.writeString(email);
        dest.writeString(language);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeSerializable(address);
        dest.writeString(phone);
        dest.writeString(times);
        dest.writeString(Boolean.toString(notifications));
        dest.writeString(Boolean.toString(location));
    }

    public static final Parcelable.Creator<CustomerSettings> CREATOR = new Creator<CustomerSettings>() {
        @Override
        public CustomerSettings createFromParcel(Parcel source) {
            return new CustomerSettings(source);
        }

        @Override
        public CustomerSettings[] newArray(int size) {
            return new CustomerSettings[size];
        }
    };

    public Object getField(String field) {
        switch(field) {
            case "id":
                return this.getId();
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
}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
