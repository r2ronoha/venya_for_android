package dev.nohasmith.venya_android_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arturo on 27/02/2017.
 */

public class Address implements Serializable{
    private String street;
    private String postcode;
    private String city;
    private String country;

    public Address() {
        this.street = "n/a";
        this.postcode = "n/a";
        this.city = "n/a";
        this.country = "n/a";
    }

    public Address(String street, String postcode,String city, String country){
        this.street = street;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
    }

    public String formatAddress() {
        String formattedAddress;
        List<String> notEmpty = new ArrayList<String>();
        if ( ! street.toLowerCase().equals("n/a") ) {
            notEmpty.add(Parsing.formatName(street));
        }
        if ( ! postcode.toLowerCase().equals("n/a") ) {
            notEmpty.add(postcode.toUpperCase());
        }
        if ( ! city.toLowerCase().equals("n/a") ) {
            notEmpty.add(Parsing.formatName(city));
        }
        if ( ! country.toLowerCase().equals("n/a") ) {
            notEmpty.add(Parsing.formatName(country));
        }

        StringBuilder sb = new StringBuilder();
        for ( String s : notEmpty ) {
            sb.append(s);
            sb.append(", ");
        }
        formattedAddress = sb.toString().replace(",\\s*$","");
        return formattedAddress;
    }

    public void setField(String field, String value) {
        switch(field) {
            case "street":
                this.street = value;
                break;
            case "postcode":
                this.postcode = value;
                break;
            case "city":
                this.city = value;
                break;
            case "country":
                this.country = value;
                break;
        }
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet(){
        return this.street;
    }

    public String getPostcode(){
        return this.postcode;
    }

    public String getCity(){
        return this.city;
    }

    public String getCountry(){
        return this.country;
    }

    public String getField(String addrField) {
        switch(addrField) {
            case "street":
                return this.street;
            case "postcode":
                return this.postcode;
            case "country":
                return this.country;
            case "city":
                return this.city;
            default:
                return null;
        }
    }
}
