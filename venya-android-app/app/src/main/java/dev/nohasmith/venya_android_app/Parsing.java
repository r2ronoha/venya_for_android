package dev.nohasmith.venya_android_app;

/**
 * Created by arturo on 06/03/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

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

    public static int getResId(Context appContext, String name) {
        return appContext.getResources().getIdentifier(name,"string",appContext.getPackageName());
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
                Log.d("[Parsing.getHttpResponse]", "reqUrl from Main Activiy: " + reqUrl);
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
                    Log.d("[Parsing.getHttpResponse]", "URL initialised is NULL");
                } else {
                    Log.d("[Parsing.getHttpResponse]", "Calling HttpURLConnection");
                    HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                    cnx.setRequestMethod("GET");
                    if (cnx != null) {
                        int respCode = cnx.getResponseCode();
                        InputStream inputStream;
                        Log.d("[Parsing.getHttpResponse]", "HTTP connection Response code: " + Integer.toString(respCode));
                        if ( respCode != 200 ){
                            Log.d("[Parsing.getHttpResponse]", "getting ERROR stream");
                            inputStream = cnx.getErrorStream();
                        } else {
                            Log.d("[Parsing.getHttpResponse]", "getting INPUT stream");
                            inputStream = cnx.getInputStream();
                        }
                        Log.d("[Parsing.getHttpResponse]", "Parsing response from Input Stream");
                        response = streamToString(inputStream);
                    } else {
                        Log.d("[Parsing.getHttpResponse]", "NULL cnx after openConnection");
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
        // Create a random session id based on the user id and a random suffix of 2 digits
        double suffix = Math.floor(Math.random()*(R.integer.sessionPlusIdMax - R.integer.sessionPlusIdMin + 1) + R.integer.sessionPlusIdMin);
        String suffixString = String.format("%.0f",suffix).replaceAll("/^([0-9])$/","0$1");
        suffixString = suffixString.substring(suffixString.length() - 2);
        String sessionid = id + suffixString;
        //Log.d(TAG,"id = " + id + " - suffix = " + suffix + " - suffixString = " + suffixString + " ==> sessionid = " + sessionid);
        return sessionid;
    }

    public static CustomerSettings parseCustomerJson(String jsonStr, Context appContext) {
        // Parse a JSON response from Nodejs server and return the customer info as CustomerSettgin class
        String [] customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        String [] addressFields = appContext.getResources().getStringArray(R.array.addressFields);
        String [] statusFields = appContext.getResources().getStringArray(R.array.statusFields);

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

            customer = new CustomerSettings(appContext,id,firstname,surname);
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
        String [] customerFields = appContext.getResources().getStringArray(R.array.customerFields);
        String [] addressFields = appContext.getResources().getStringArray(R.array.addressFields);
        String [] statusFields = appContext.getResources().getStringArray(R.array.statusFields);

        HashMap<String,Object> parsedResponse = new HashMap<String, Object>();
        CustomerSettings customer;
        HashMap<String,String> customerConstruct;
        String [] customerConstructFields = appContext.getResources().getStringArray(R.array.customerCunstructFields);
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
                    Log.d(TAG,"Unable to parse field " + statusFields[i]);
                }
            }

            if ( parsedResponse.get("status").equals(appContext.getResources().getString(R.string.success_status)) ) {
                customerConstruct = new HashMap<String, String>();
                for (int i = 0; i < customerConstructFields.length; i++) {
                    try {
                        String field = customerConstructFields[i];
                        //Log.d(TAG,"parsing cnstructor field: " + field);
                        String value = jsonObject.getString(field);
                        customerConstruct.put(field, value);
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to parse " + customerConstructFields[i]);
                        e.printStackTrace();
                    }
                }
                String id = customerConstruct.get("id");
                String firstname = customerConstruct.get("firstname");
                String surname = customerConstruct.get("surname");
                customer = new CustomerSettings(appContext, id, firstname, surname);

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
                        Log.d(TAG, "Unable to parse field " + customerFields[i]);
                        e.printStackTrace();
                    }
                }
                parsedResponse.put("customer",customer);
            }
        } catch (final JSONException e) {
            Log.e(TAG,"Json parsing error" + e.getMessage());
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
            HashMap<String, Object> parsedResponse = Parsing.parseServerResponseJson(response, context);
            String status = (String) parsedResponse.get("status");

            if ( ! status.equals(context.getResources().getString(R.string.success_status)) ) {
                Log.d(TAG,"status " + status + " is not " + context.getResources().getString(R.string.success_status));
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
}
