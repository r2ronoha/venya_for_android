package dev.nohasmith.venya_android_app;

/**
 * Created by arturo on 06/03/2017.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.model.Event;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.nohasmith.venya_android_app.MainActivity.addressFields;
import static dev.nohasmith.venya_android_app.MainActivity.appointmentFields;
import static dev.nohasmith.venya_android_app.MainActivity.customerConstructFields;
import static dev.nohasmith.venya_android_app.MainActivity.customerFields;
import static dev.nohasmith.venya_android_app.MainActivity.providerFields;
import static dev.nohasmith.venya_android_app.MainActivity.statusFields;
import static dev.nohasmith.venya_android_app.MainActivity.venyaUrl;
import static java.lang.Character.PARAGRAPH_SEPARATOR;
import static java.lang.Character.toUpperCase;

public class Parsing {
    
    private static final String TAG = Parsing.class.getSimpleName();

    public static String formatMessage(String [] messages){
        String myTag = TAG + ".formatMessage";
        String message = toUpperCase(messages[0].charAt(0)) + messages[0].substring(1);
        //String message = "";

        for (int i=1; i<messages.length; i++){
            //Log.d(myTag,"concatenating " + messages[i] + " to " + message);
            message = message.concat(" " + messages[i]);
        }
        return toUpperCase(message.charAt(0)) + message.substring(1);
    }

    public static String formatMessage(Context context, int [] messages){
        String myTag = TAG + ".formatMessage";
        String message = toUpperCase(context.getResources().getString(messages[0]).charAt(0)) + context.getResources().getString(messages[0]).substring(1);
        //String message = "";

        for (int i=1; i<messages.length; i++){
            //Log.d(myTag,"concatenating " + messages[i] + " to " + message);
            message = message.concat(" " + context.getResources().getString(messages[i]));
        }
        return toUpperCase(message.charAt(0)) + message.substring(1);
    }

    public static String formatName(String name){
        String myTag = TAG + ".formatName";
        String message = name;
        try {
            String[] messages = name.split("\\s+");
            message = toUpperCase(messages[0].charAt(0)) + messages[0].substring(1);
            //String message = "";

            for (int i = 1; i < messages.length; i++) {
                //Log.d(myTag,"concatenating " + messages[i] + " to " + message);
                String part = toUpperCase(messages[i].charAt(0)) + messages[i].substring(1);
                message = message.concat(" " + part);
            }
        } catch (Exception e) {
            Log.e(myTag,"Error formatting \"" + name + "\". Return unchanged");
            message = name;
        }
        return message;
        //return toUpperCase(message.charAt(0)) + message.substring(1);
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value,"UTF-8");
        } catch (Exception e) {
            Log.e("Parsing.encode","Failed to encode \"" + value + "\". Replacing white spaces with \"-\"");
            return value.replaceAll("\\s","-");
        }
    }

    public static String formatDate(String date) {
        return date.replaceAll("^([0-9]{2})([0-9]{2})([0-9]+)","$1/$2/$3");
    }

    public static int getResId(Context appContext, String name) {
        return appContext.getResources().getIdentifier(name,"string",appContext.getPackageName());
    }

    public static int getResId(Context appContext, String name, String folder) {
        return appContext.getResources().getIdentifier(name,folder,appContext.getPackageName());
    }

    public static String hideValue(String value) {
        return value.replaceAll(".","*");
    }

    public static String streamToString (InputStream is) {
        BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ( (line = reader.readLine()) != null ) {
                Log.d("[Parsing.streamToString]","Line from input stream: " + line);
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /*public static boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        InetAddress

        if ( activeInfo == null ) {
    }*/

    public static String getHttpResponse (Context appContext, String reqUrl) {
        String response = null;
        try {
            // Check networ connectivity
            ConnectivityManager cm = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = cm.getActiveNetworkInfo();

            if ( activeInfo == null || ! activeInfo.isConnected()) {
                Log.e("[Parsing.getHttpResponse]","No network connection");
                return  null;
            } else {
                Log.d("[Parsing.getHttpResponse]", "reqUrl: " + reqUrl);
                URL url = new URL(reqUrl);

                String dest = reqUrl.replaceAll("https*://","").replaceAll("/.*","");
                String host = dest.replaceAll(":.*","");
                Log.d("Parsing.getHttpResponse","HOST = " + host);
                /*try {
                    InetAddress ipAddr = InetAddress.getByName(host);
                    Log.d("Parsing.HttpResponse","IP : " + ipAddr);
                } catch (Exception e) {
                    Log.d("Parsing.Httpresponse","Could not get an ip for " + host);
                    return null;
                }*/

                if ( url == null ) {
                    Log.e("[Parsing.getHttpResponse]", "URL initialised is NULL");
                } else {
                    try {
                        //Log.d("[Parsing.getHttpResponse]", "Calling HttpURLConnection");
                        HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                        cnx.setConnectTimeout(5000);
                        cnx.setRequestMethod("GET");
                        if (cnx != null) {
                            int respCode = cnx.getResponseCode();
                            InputStream inputStream;
                            //Log.d("[Parsing.getHttpResponse]", "HTTP connection Response code: " + Integer.toString(respCode));
                            if (respCode != 200) {
                                Log.e("[Parsing.getHttpResponse]", "HTTP connection Response code: " + Integer.toString(respCode) + "getting ERROR stream");
                                inputStream = cnx.getErrorStream();
                            } else {
                                //Log.d("[Parsing.getHttpResponse]", "getting INPUT stream");
                                inputStream = cnx.getInputStream();
                            }
                            //Log.d("[Parsing.getHttpResponse]", "Parsing response from Input Stream");
                            response = streamToString(inputStream);
                        } else {
                            Log.e("[Parsing.getHttpResponse]", "NULL cnx after openConnection");
                        }
                    } catch (SocketTimeoutException timeout) {
                        Log.e(TAG,"HTTP Request Timeout");
                        timeout.printStackTrace();
                        return "{\"status\" : \"ERROR\", \"errormessage\" : \"httptimeout\" }";
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public static Provider getProviderDetails (Context context, String providerid) {
        String myTAG = TAG + ".getProviderDetails";
        MyHttpHandler httpHandler = new MyHttpHandler(context);
        String response = null;

        String url = venyaUrl +
                "/getProvider?" +
                "action=getproviderdetails" +
                "&id=" + providerid;

        try {
            response = httpHandler.execute(url).get();
        } catch (Exception e) {
            Log.e(myTAG,"Exception while running HTTP Async task");
            e.printStackTrace();
            return null;
        }

        if ( response == null ) {
            Log.e(myTAG,"NULL response from server");
            return null;
        } else {
            Log.d(myTAG,"Server response = " + response);
            HashMap<String,Object> responseParsed = parseGetProviderResponseJson(response, context);
            String id = (String)responseParsed.get("id");
            Provider provider = new Provider(id);

            for ( int i=0; i<providerFields.length; i++ ) {
                String field = providerFields[i];
                if ( ! field.equals("id") ) {
                    //Log.d(myTAG,"field \"" + field + "\"");
                    Object value = responseParsed.get(field);
                    //String isAddress = ( value instanceof Address ) ? "value is Address" : "value is NOT address";
                    //Log.d(myTAG,"field : \"" + field + "\". " + isAddress);
                    provider.setField(field,value);
                }
            }
            return provider;
        }
    }

    public static HashMap<String, Object> parseGetProviderResponseJson(String serverResponse, Context appContext) {
        /*
         parse a JSON response from the Nodejs server and return a HAsh Map with
         - action
         - status
         - errormessage
         - customer info (as CustomerSetting class
          */

        String myTAG = TAG + ".parseGetProviderResp";
        HashMap<String,Object> parsedResponse = new HashMap<String, Object>();
        Log.d(myTAG,serverResponse);
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);

            for (int i=0; i<statusFields.length; i++) {
                try {
                    String field = statusFields[i];
                    String value = jsonObject.getString(field);
                    parsedResponse.put(field,value);
                } catch (Exception e) {
                    Log.d(myTAG,"Unable to parse field " + statusFields[i]);
                }
            }

            if ( parsedResponse.get("status").equals(appContext.getResources().getString(R.string.success_status)) || parsedResponse.get("errormessage").equals("emailfailed")) {
                Iterator<String> providerKeys = jsonObject.keys();
                for ( int i=0; i<providerFields.length; i++) {
                    String field = providerFields[i];
                    Object value;
                    try {
                        if (field.equals("address")) {
                            //Log.d(myTAG,"Parsing Provider's address");
                            JSONObject addrJson = jsonObject.getJSONObject(field);
                            Address providerAddress = new Address();
                            for (int j = 0; j < addressFields.length; j++) {
                                String addrField = addressFields[j];
                                String addrValue = addrJson.getString(addrField);
                                providerAddress.setField(addrField, addrValue);
                            }
                            //value = providerAddress.formatAddress();
                            value = providerAddress;

                        } else {
                            value = (jsonObject.has(field)) ? jsonObject.get(field) : "n/a";
                        }
                        parsedResponse.put(field, value);
                    } catch (Exception e) {
                        Log.d(myTAG, "Unable to parse field " + field);
                        e.printStackTrace();
                    }
                }
            }
        } catch (final JSONException e) {
            Log.e(myTAG,"Json parsing error" + e.getMessage());
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static String randomSessionID(){
        int min = R.integer.sessionidMin;
        int max = R.integer.sessionidMax;
        double sessionidNum = Math.floor(Math.random()*(max - min + 1) + min);
        String sessionid = String.format("%.0f",sessionidNum);
        while (sessionid.length() < R.integer.sessionidLength ) {
            sessionid = "0" + sessionid;
        }
        return sessionid;
    }

    public static String randomSessionID (String id) {
        //Log.d(TAG,"id recibido : " + id);
        // Create a random session id based on the user id and a random suffix of 2 digits
        double suffix = Math.floor(Math.random()*(R.integer.sessionPlusIdMax - R.integer.sessionPlusIdMin + 1) + R.integer.sessionPlusIdMin);
        String suffixString = String.format("%.0f",suffix).replaceAll("/^([0-9])$/","0$1");
        suffixString = suffixString.substring(suffixString.length() - 2);
        String sessionid = id + suffixString;
        //Log.d(TAG,"id = " + id + " - suffix = " + suffix + " - suffixString = " + suffixString + " ==> sessionid = " + sessionid);
        return sessionid;
    }

    public static Locale getLocale(Configuration config) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }

    public static String getMapKeyFromValue(HashMap<String,String> map, String value ) {
        for ( String key : map.keySet() ) {
            if ( map.get(key).equals(value) ) {
                return key;
            }
        }
        return null;
    }

    public static int getIndexOf(String [] array, String value ){
        int position = -1;
        for ( int i=0; i<array.length; i++ ) {
            if ( array[i].equals(value) ) {
                position = i;
            }
        }
        return position;
    }

    public static CustomerSettings parseCustomerJson(String jsonStr, Context appContext) {
        // Parse a JSON response from Nodejs server and return the customer info as CustomerSettgin class
        /*
        customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        addressFields = appContext.getResources().getStringArray(R.array.addressFields);
        statusFields = appContext.getResources().getStringArray(R.array.statusFields);
        */

        CustomerSettings customer = null;
        Address customerAddress;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            //String status = jsonObject.getString("");
            //String action = jsonObject.getString("");
            String id = jsonObject.getString("id");
            String sessionid = jsonObject.getString("sessionid");
            String type = jsonObject.getString("type");
            String firstname = jsonObject.getString("firstname");
            String surname = jsonObject.getString("surname");
            String dob = jsonObject.getString("dob");
            String email = jsonObject.getString("email");
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");

            JSONObject addressJson = jsonObject.getJSONObject("address");
            String street = addressJson.getString("street");
            String postcode = addressJson.getString("postcode");
            String city = addressJson.getString("city");
            String country = addressJson.getString("country");
            customerAddress = new Address(street,postcode,city,country);

            String phone = jsonObject.getString("phone");
            String times = jsonObject.getString("times");
            String language = jsonObject.getString("language");

            boolean notifications = Boolean.parseBoolean(jsonObject.getString("notifications"));
            //String notifString = jsonObject.getString("notifications");
            //if (notifString == "0") { notifications = false; }
            boolean location = Boolean.parseBoolean(jsonObject.getString("location"));

            customer = new CustomerSettings(appContext,id,firstname,surname,dob);
            customer.setSessionid(sessionid);
            customer.setUsername(username);
            customer.setPassword(password);
            customer.setEmail(email);
            customer.setLanguage(language);
            customer.setNotifications(notifications);
            customer.setLocation(location);
            customer.setPhone(phone);
            customer.setTimes(times);

        } catch (final JSONException e) {
            Log.e(TAG,"Json parsing error" + e.getMessage());
        }

        return customer;
    }

    public static HashMap<String, Object> parseGetCustomerResponseJson(String serverResponse, Context appContext) {
        /*
         parse a JSON response from the Nodejs server and return a HAsh Map with
         - action
         - status
         - errormessage
         - customer info (as CustomerSetting class
          */
        String myTAG = TAG + ".parseGetCustomer";

        HashMap<String,Object> parsedResponse = new HashMap<String, Object>();
        CustomerSettings customer;
        HashMap<String,String> customerConstruct;
        // String [] customerConstructFields = appContext.getResources().getStringArray(R.array.customerCunstructFields);
        Address customerAddress;
        Log.d("[Parsing.parseServerResponse]",serverResponse);
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);

            for (int i=0; i<statusFields.length; i++) {
                try {
                    String field = statusFields[i];
                    String value = jsonObject.getString(field);
                    parsedResponse.put(field,value);
                } catch (Exception e) {
                    Log.d(myTAG,"Unable to parse field " + statusFields[i]);
                }
            }

            if ( parsedResponse.get("status").equals(appContext.getResources().getString(R.string.success_status)) || parsedResponse.get("errormessage").equals("emailfailed")) {
                customerConstruct = new HashMap<String, String>();
                for (int i = 0; i < customerConstructFields.length; i++) {
                    try {
                        String field = customerConstructFields[i];
                        //Log.d(TAG,"parsing cnstructor field: " + field);
                        String value = jsonObject.getString(field);
                        customerConstruct.put(field, value);
                    } catch (Exception e) {
                        Log.d(myTAG, "Failed to parse " + customerConstructFields[i]);
                        e.printStackTrace();
                    }
                }
                String id = customerConstruct.get("id");
                String firstname = customerConstruct.get("firstname");
                String surname = customerConstruct.get("surname");
                String dob = customerConstruct.get("dob");
                customer = new CustomerSettings(appContext, id, firstname, surname, dob);

                for (int i = 0; i < customerFields.length; i++) {
                    try {
                        String field = customerFields[i];
                        if (!customerConstruct.containsKey(field)) {
                            //Log.d(TAG,"parsing \"" + field + "\"");
                            if (field.equals("address")) {
                                //Log.d(TAG,"\"" + field + "\" is address");
                                customerAddress = new Address();
                                JSONObject myAddress = jsonObject.getJSONObject("address");
                                for (int j = 0; j < addressFields.length; j++) {
                                    String addrField = addressFields[j];
                                    String value = myAddress.getString(addrField);
                                    customerAddress.setField(addrField, value);
                                }
                                customer.setField(field, customerAddress);
                            } else if ( field.equals("providers") ) {
                                JSONObject myProviders = jsonObject.getJSONObject(("providers"));
                                Iterator<String> providersIds = myProviders.keys();

                                while ( providersIds.hasNext() ) {
                                    String providerid = providersIds.next();
                                    boolean isActve = myProviders.getBoolean(providerid);
                                    Provider providerDetails = Parsing.getProviderDetails(appContext,providerid);

                                    if ( providerDetails == null ) {
                                        Log.e(myTAG,"NULL response to getProvider " + providerid);
                                    } else {
                                        providerDetails.setActive(isActve);
                                        for (int k = 0; k < providerFields.length; k++) {
                                            String provField = providerFields[k];
                                            Object value = providerDetails.getField(provField);
                                            providerDetails.setField(provField,value);
                                        }
                                    }
                                    customer.addProvider(providerDetails);
                                }
                            } else {
                                String[] booleanFields = appContext.getResources().getStringArray(R.array.booleanFields);
                                if (Arrays.asList(booleanFields).contains(field)) {
                                    //Log.d(TAG,"\"" + field + "\" is boolean");
                                    boolean value = Boolean.parseBoolean(jsonObject.getString(field));
                                    customer.setField(field, value);
                                } else {
                                    //Log.d(TAG,"\"" + field + "\" is string");
                                    String value = jsonObject.getString(field);
                                    customer.setField(field, value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d(myTAG, "Unable to parse field " + customerFields[i]);
                        e.printStackTrace();
                    }
                }
                parsedResponse.put("customer",customer);
            }
        } catch (final JSONException e) {
            Log.e(myTAG,"Json parsing error" + e.getMessage());
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static HashMap<String, Object> parseGetFullCustomerResponseJson(String serverResponse, Context appContext) {
        /*
         parse a JSON response from the Nodejs server and return a HAsh Map with
         - action
         - status
         - errormessage
         - customer info (as CustomerSetting class
          */
        /*
        String [] customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        String [] addressFields = appContext.getResources().getStringArray(R.array.addressFields);
        String [] statusFields = appContext.getResources().getStringArray(R.array.statusFields);
        */
        String myTAG = TAG + ".parseGetFullCustomer";

        HashMap<String,Object> parsedResponse = new HashMap<String, Object>();
        FullCustomerSettings customer;
        HashMap<String,String> customerConstruct;
        // String [] customerConstructFields = appContext.getResources().getStringArray(R.array.customerCunstructFields);
        Address customerAddress;
        Log.d("[Parsing.parseGetFullCustomerResponseJson]",serverResponse);
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);

            for (int i=0; i<statusFields.length; i++) {
                try {
                    String field = statusFields[i];
                    String value = jsonObject.getString(field);
                    parsedResponse.put(field,value);
                } catch (Exception e) {
                    Log.d(myTAG,"Unable to parse field " + statusFields[i]);
                }
            }

            if ( parsedResponse.get("status").equals(appContext.getResources().getString(R.string.success_status)) ) {
                customerConstruct = new HashMap<String, String>();
                for (int i = 0; i < customerConstructFields.length; i++) {
                    try {
                        String field = customerConstructFields[i];
                        String value;

                        //Log.d(TAG,"parsing cnstructor field: " + field);
                        if ( field.equals("id") ) {
                            value = jsonObject.getString("_id");
                        } else {
                            JSONObject fullValue = jsonObject.getJSONObject(field);
                            value = fullValue.getString("value");
                        }
                        customerConstruct.put(field, value);
                    } catch (Exception e) {
                        Log.d(myTAG, "Failed to parse " + customerConstructFields[i]);
                        e.printStackTrace();
                    }
                }
                String id = customerConstruct.get("id");
                String firstname = customerConstruct.get("firstname");
                String surname = customerConstruct.get("surname");
                String dob = customerConstruct.get("dob");
                customer = new FullCustomerSettings(appContext, id, firstname, surname, dob);

                for (int i = 0; i < customerFields.length; i++) {
                    try {
                        String field = customerFields[i];
                        FullCustomerSettings customerSettings;
                        if (!customerConstruct.containsKey(field)) {
                            //Log.d(TAG,"parsing \"" + field + "\"");
                            JSONObject fieldJson = jsonObject.getJSONObject(field);
                            if (field.equals("address")) {
                                //Log.d(TAG,"\"" + field + "\" is address");
                                customerAddress = new Address();
                                JSONObject myAddress = fieldJson.getJSONObject("value");
                                for (int j = 0; j < addressFields.length; j++) {
                                    String addrField = addressFields[j];
                                    String value = myAddress.getString(addrField);
                                    customerAddress.setField(addrField, value);
                                }
                                customer.setField(field, customerAddress);
                            } else if ( field.equals("providers") ) {
                                JSONObject myProviders = fieldJson.getJSONObject("value");
                                Iterator<String> providersIds = myProviders.keys();

                                while ( providersIds.hasNext() ) {
                                    String providerid = providersIds.next();
                                    boolean isActve = myProviders.getBoolean(providerid);
                                    /*String url = venyaUrl +
                                            "/getProvider?" +
                                            "action=getproviderdetails" +
                                            "&id=" + providerid;*/
                                    Provider providerDetails = Parsing.getProviderDetails(appContext,providerid);

                                    if ( providerDetails == null ) {
                                        Log.e(myTAG,"NULL response to getProvider " + providerid);
                                    } else {
                                        providerDetails.setActive(isActve);
                                        for (int k = 0; k < providerFields.length; k++) {
                                            String provField = providerFields[k];
                                            Object value = providerDetails.getField(provField);
                                            providerDetails.setField(provField,value);
                                        }
                                    }
                                    customer.addProvider(providerDetails);
                                }
                            } else {
                                String[] booleanFields = appContext.getResources().getStringArray(R.array.booleanFields);
                                if (Arrays.asList(booleanFields).contains(field)) {
                                    //Log.d(TAG,"\"" + field + "\" is boolean");
                                    boolean value = Boolean.parseBoolean(fieldJson.getString("value"));
                                    customer.setField(field, value);
                                } else {
                                    //Log.d(TAG,"\"" + field + "\" is string");
                                    String value = fieldJson.getString("value");
                                    customer.setField(field, value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d(myTAG, "Unable to parse field " + customerFields[i]);
                        e.printStackTrace();
                    }
                }
                parsedResponse.put("customer",customer);
            }
        } catch (final JSONException e) {
            Log.e(myTAG,"Json parsing error" + e.getMessage());
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static String setSessionId(Context context, String id, String sessionid, String type) {
        String hostname = context.getResources().getString(R.string.venya_node_server);
        String port = context.getResources().getString(R.string.venya_node_port);
        String reqUrl = "http://" + hostname + ":" + port + "/updateSetting?" +
                "action=update" +
                "&type=" + type +
                "&id=" + id +
                "&field=sessionid" +
                "&newvalue=" + sessionid;
        String response;

        MyHttpHandler httpHandler = new MyHttpHandler(context);
        try {
            response = httpHandler.execute(reqUrl).get();
        } catch (Exception e) {
            Log.d(TAG,"Exception calling AsyncTask: " + e);
            e.printStackTrace();
            return context.getResources().getString(R.string.errors_httpexception);
        }
        if ( response == null ) {
            Log.e(TAG,"NULL response from server");
            return context.getResources().getString(R.string.errors_nullfromserver);
        } else {
            HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, context);
            String status = (String) parsedResponse.get("status");

            if ( ! status.equals(context.getResources().getString(R.string.success_status)) ) {
                String errormessage = (String) parsedResponse.get("errormessage");
                return errormessage;
            } else {
                Log.d(TAG,"Request succeeded. Session id " + ((CustomerSettings)parsedResponse.get("customer")).getSessionid());
                return ((CustomerSettings)parsedResponse.get("customer")).getSessionid();
            }
        }
    }

    public static String getBooleanValue(Boolean value) {
        return ( value ) ? "ON" : "OFF";
        //return convert;
    }

    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        //if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ){
            //context.createConfigurationContext(config);
        //} else {
                resources.updateConfiguration(config,resources.getDisplayMetrics());
        //}
    }

    public static boolean checkDateFormat(Context context, String dob) {
        String dobFormat = context.getResources().getString(R.string.dobFormat);
        Pattern p = Pattern.compile(dobFormat);
        Matcher m = p.matcher(dob);
        return m.matches();
    }

    public static boolean checkRequired(Context context, String field) {
        //Log.d("RegisterFragment.CheckRequired","field = \"" + field + "\"");
        return ! field.equals("") && field != null;
    }

    public static boolean checkUidFormat(Context context, String uid) {
        String uidFormat = context.getResources().getString(R.string.idFormat);
        Pattern p = Pattern.compile(uidFormat);
        Matcher m = p.matcher(uid);
        return m.matches();
    }

    public static boolean checkUsernameFormat(Context context, String username) {
        String usernameFormat = context.getResources().getString(R.string.usernameFormat);
        String defaultUsername = context.getResources().getString(R.string.default_username);

        Pattern p = Pattern.compile(usernameFormat);
        Matcher m = p.matcher(username);

        if ( username.equals(defaultUsername) || ! m.matches() ) {
            return false;
        }
        return true;
    }

    public static boolean checkPasswordFormat(Context context, String password) {
        String passwordFormat = context.getResources().getString(R.string.passwordFormat);
        //return password.matches(passwordFormat);
        Pattern p = Pattern.compile(passwordFormat);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static boolean checkEmailFormat(Context context, String email) {
        String emailFormat = context.getResources().getString(R.string.emailFormat);
        //return email.matches(emailFormat);
        Pattern p = Pattern.compile(emailFormat);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean checkPhoneFormat(Context context, String phone) {
        String phoneFormat = context.getResources().getString(R.string.phoneFormat);
        //return phone.matches(phoneFormat);
        Pattern p = Pattern.compile(phoneFormat);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static boolean checkPostCodeFormat(Context context, String postcode) {
        String postCodeFormat = context.getResources().getString(R.string.postcodeFormat);
        //return phone.matches(phoneFormat);
        Pattern p = Pattern.compile(postCodeFormat);
        Matcher m = p.matcher(postcode);
        return m.matches();
    }

    public static void displayTextView(Context context, TextView errorsView, int errorid) {
        String displayTextView = Parsing.formatMessage(new String [] {context.getResources().getString(errorid)});
        errorsView.setText(displayTextView);
    }

    public static void displayTextView(Context context, TextView errorsView, String errormessage) {
        String displayTextView;
        try {
            displayTextView = Parsing.formatMessage(new String[]{context.getResources().getString(Parsing.getResId(context, errormessage))});
        }catch (Exception e) {
            Log.e(TAG + ".displayTextView","Coudl not find id for errormessge \"" + errormessage + "\"");
            displayTextView = errormessage;
        }
        errorsView.setText(displayTextView);
    }

    public static void displayTextView(Context context, TextView errorsView, String [] errormessage) {
        String displayTextView = Parsing.formatMessage(errormessage);
        errorsView.setText(displayTextView);
    }

    public static void displayTextView(Context context, TextView errorsView, int [] errorids) {
        String [] errormessage = new String[errorids.length];
        for ( int i=0; i<errorids.length; i++ ) {
            errormessage[i] = context.getResources().getString(errorids[i]);
        }
        String displayTextView = Parsing.formatMessage(errormessage);
        errorsView.setText(displayTextView);
    }

    public static void setFormHint(Context context, TextView errorsView, int errorid) {
        String setFormHint = Parsing.formatMessage(new String [] {context.getResources().getString(errorid)});
        errorsView.setHint(setFormHint);
    }

    public static void setFormHint(Context context, TextView errorsView, String errormessage) {
        String setFormHint = Parsing.formatMessage(new String [] {context.getResources().getString(Parsing.getResId(context, errormessage))});
        errorsView.setHint(setFormHint);
    }

    public static void setFormHint(Context context, TextView errorsView, String [] errormessage) {
        String setFormHint = Parsing.formatMessage(errormessage);
        errorsView.setHint(setFormHint);
    }

    public static void setFormHint(Context context, TextView errorsView, int [] errorids) {
        String [] errormessage = new String[errorids.length];
        for ( int i=0; i<errorids.length; i++ ) {
            errormessage[i] = context.getResources().getString(errorids[i]);
        }
        String setFormHint = Parsing.formatMessage(errormessage);
        errorsView.setHint(setFormHint);
    }

    public static boolean unsubscribeProvider(Context appContext, String providerid, String sessionid) {
        String myTAG = TAG + ".unsubscribeProvider";
        MyHttpHandler handler = new MyHttpHandler(appContext);
        String response;

        String url = venyaUrl +
                "/updateSetting?" +
                "action=update" +
                "&type=customer" +
                "&sessionid=" + sessionid +
                "&field=providers" +
                "&newvalue=" + providerid + "=" + false;

        try {
            response = handler.execute(url).get();
        } catch (Exception e) {
            Log.e(myTAG,"Exception while running HTTP Async request");
            e.printStackTrace();
            return false;
        }

        HashMap<String,Object> parsedResponse = new HashMap<String,Object>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            for (int i = 0; i < statusFields.length; i++) {
                try {
                    String field = statusFields[i];
                    String value = jsonObject.getString(field);
                    parsedResponse.put(field, value);
                } catch (Exception e) {
                    Log.d(myTAG, "Unable to parse field " + statusFields[i]);
                    //return false;
                }
            }
            String status = (String)parsedResponse.get("status");
            if ( ! status.equals(appContext.getResources().getString(R.string.success_status)) ) {
                String errormessage = (String)parsedResponse.get("errormessage");
                Log.e(myTAG,"Provider unsubscription error: " + appContext.getResources().getString(Parsing.getResId(appContext,"errors_" + errormessage)));
                return false;
            } else {
                Log.d(myTAG,"Successful unsubscription");
                return true;
            }
        } catch (Exception e) {
            Log.e(myTAG,"Failed to parse HTTP server response");
            e.printStackTrace();
            return false;
        }
    }

    public static HashMap<String,Object> insertAppointment(Context context, Appointment appointment) {
        String myTAG = TAG + ".insertAppointment";
        String customerid = appointment.getCustomerid();
        String providerid = appointment.getProviderid();
        long date = appointment.getDate();
        HashMap<String,Object> insertResponse = new HashMap<String,Object>();
        JSONObject JSONresp;

        String url = venyaUrl +
                "/insertAppointment" +
                "?action=insertappointment" +
                "&customerid=" + customerid +
                "&providerid=" + providerid +
                "&date=" + date;

        MyHttpHandler httpHandler = new MyHttpHandler(context);
        String response = null;
        try {
            response = httpHandler.execute(url).get();
        } catch (Exception e) {
            Log.e(myTAG,"Exception while performing HTTP Async task");
        }

        if ( response == null ) {
            Log.e(myTAG,"NULL response from server");
            insertResponse.put("status","ERROR");
            insertResponse.put("errormessage","nullfromserver");
        } else {
            try {
                JSONresp = new JSONObject(response);
            } catch (Exception e) {
                Log.e(myTAG,"FAiled to parse response from server");
                insertResponse.put("status","ERROR");
                insertResponse.put("errormessage","repsonseparseerror");
                return insertResponse;
            }
            HashMap<String,String> status = new HashMap<String,String>();

            for ( int i=0; i<statusFields.length; i++ ) {
                String field = statusFields[i];
                try {
                    status.put(field,JSONresp.getString(field));
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to parse field " + field);
                }
            }

            if ( ! ((String)status.get("status")).equals(context.getResources().getString(R.string.success_status)) ) {
                insertResponse.put("status",status.get("status"));
                try {
                    insertResponse.put("errormessage",status.get("errormessage"));
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to get errormessage of unsuccessful insertion");
                }
            } else {
                String appointmentid = null;
                try {
                    appointmentid = JSONresp.getString("id");
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to get appointment id from successful insetion response");
                }
                insertResponse.put("status",context.getResources().getString(R.string.success_status));
                insertResponse.put("appointmentid",appointmentid);
            }
        }

        return insertResponse;
    }

    public static HashMap<String,Object> getCustomerAppointments(Context context,String customerid) {
        String myTAG = TAG + ".getCustomerAppointments";
        HashMap<String,Object> appointmentsList = new HashMap<String, Object>();

        MyHttpHandler httpHandler = new MyHttpHandler(context);
        String response = null;
        JSONObject JSONresp;

        String url = venyaUrl +
                "/getCustomerAppointments" +
                "?action=getcustomerappointments" +
                "&customerid=" + customerid;

        try {
            response = httpHandler.execute(url).get();
        } catch (Exception e) {
            Log.e(myTAG,"Exception while performing HTTP Async task");
        }

        if ( response == null ) {
            Log.e(myTAG,"NULL response from server");
        } else {
            try {
                JSONresp = new JSONObject(response);
            } catch (Exception e) {
                Log.e(myTAG,"FAiled to parse response from server");
                return null;
            }
            HashMap<String,String> status = new HashMap<String,String>();

            for ( int i=0; i<statusFields.length; i++ ) {
                String field = statusFields[i];
                try {
                    status.put(field,JSONresp.getString(field));
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to parse field " + field);
                }
            }

            if ( ! ((String)status.get("status")).equals(context.getResources().getString(R.string.success_status)) ) {
                appointmentsList.put("status",status.get("status"));
                try {
                    appointmentsList.put("errormessage",status.get("errormessage"));
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to get errormessage of unsuccessful insertion");
                }
            } else {
                try {
                    JSONObject appList = JSONresp.getJSONObject("appointments");
                    Iterator<String> appListKeys = appList.keys();
                    while ( appListKeys.hasNext() ) {
                        String appId = appListKeys.next();
                        Appointment appointment = new Appointment();
                        try {
                            JSONObject myApp = appList.getJSONObject(appId);
                            for ( int j=0; j<appointmentFields.length; j++ ) {
                                String appField = appointmentFields[j];
                                String respField = ( appField.equals("id")) ? "_id" : appField;
                                try {
                                    switch (appField) {
                                        case "date":
                                            long date = new Long((String)myApp.get(respField));
                                            appointment.setDate(date);
                                            break;
                                        case "delay":
                                            long delay = new Long((int)myApp.get(respField));
                                            appointment.setDelay(delay);
                                            break;
                                        default:
                                            appointment.setField(appField,myApp.get(respField));
                                    }
                                } catch (Exception fx) {
                                    Log.e(myTAG,"Failed to add field \"" + appField + "\" to appointment class");
                                    fx.printStackTrace();
                                }
                            }
                            appointmentsList.put(appId,appointment);
                        } catch (Exception ex) {
                            Log.e(myTAG,"failed to get details of appointment id " + appId);
                        }
                    }
                } catch (Exception e) {
                    Log.e(myTAG,"Failed to get appointments list from response");
                }
            }
        }

        return appointmentsList;
    }

    public static HashMap<String,Object> updateAppointment(Context context, FullCustomerSettings customer, Appointment appointment) {
        /*
         update DB with new apppointment.
         If update is successful, then update the customer. Otherwise return customer unchanged + errormessage
          */
        String myTAG = TAG + ".updateAppointment";

        HashMap<String,Object> updateReturn = new HashMap<String, Object>();
        updateReturn.put("customer",customer); // by default return unchanged customer. Will be overridden if the DB update is successful

        String url = venyaUrl + "/updateAppointment" +
                "?id=" + appointment.getId() +
                "&date=" + appointment.getDate() +
                "&status=" + appointment.getStatus() +
                "&delay=" + appointment.getDelay() +
                "&duration=" + appointment.getDuration() +
                "&googleId=" + appointment.getGoogleId();

        MyHttpHandler httpHandler = new MyHttpHandler(context);
        String response = null;
        try {
            response = httpHandler.execute(url).get();
        } catch (Exception e) {
            Log.e(myTAG,"Exception while performing HTTP async task");
            e.printStackTrace();
        }

        if ( response != null ) {
            try {
                JSONObject respJSON = new JSONObject(response);
                try {
                    String status = respJSON.getString("status");
                    updateReturn.put("status",status);
                    if (!status.equals(context.getResources().getString(R.string.success_status))) {
                        Log.e(myTAG,"unsuccesful update");
                        try {
                            String errormessage = respJSON.getString("errormessage");
                            updateReturn.put("errormessage",errormessage);
                        } catch (Exception getErrorEx) {
                            Log.e(myTAG,"Failed to get error message");
                            updateReturn.put("errormessage",context.getResources().getString(R.string.errors_unknwon));
                            getErrorEx.printStackTrace();
                        }
                    } else {
                        customer.addAppointment(appointment); // addAppointment performs "put", which will override existin appointment
                        updateReturn.put("customer",customer);
                    }
                } catch (Exception readEx) {
                    Log.e(myTAG,"Failed to get status from server response");
                    readEx.printStackTrace();
                    updateReturn.put("status","ERROR");
                    updateReturn.put("errormessage",context.getResources().getString(R.string.errors_unknwon));
                }
            } catch (Exception parseEx) {
                Log.e(myTAG,"Failed to parse response from server");
                parseEx.printStackTrace();
                updateReturn.put("status","ERROR");
                updateReturn.put("errormessage",context.getResources().getString(R.string.errors_unknwon));
            }
        }
        return updateReturn;
    }

    public static void setCellFormat(Context context, TextView cell, TableRow.LayoutParams lp, String text, int padding, int color) {
        cell.setLayoutParams(lp);
        cell.setText(text);
        cell.setPadding(padding,padding,padding,padding);
        cell.setBackgroundColor(context.getColor(color));
    }
}
