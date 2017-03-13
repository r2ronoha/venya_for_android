<<<<<<< HEAD
<<<<<<< HEAD
package dev.nohasmith.venya_android_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by arturo on 07/03/2017.
 */

public class MyHttpHandler extends AsyncTask<String, String, String> {
    private static final String TAG = MyHttpHandler.class.getSimpleName();
    private Context appContext;
    ProgressDialog progressDialog;

    public MyHttpHandler(Context context){
        appContext = context;
    }

    @Override
    protected String doInBackground(String... reqUrl) {
        //onProgressUpdate("Contacting server");
        String response = Parsing.getHttpResponse(appContext, reqUrl[0]);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
/*
    @Override
    protected void onProgressUpdate(String... text){
        TextView msgDisplay = (TextView)View.findViewById(R.id.errorsView);
        msgDisplay.setText(text[0]);
    }
*/
    /*
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(appContext,"ProgressDialog","accessing URL");
    }*/
}
=======
package dev.nohasmith.venya_android_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by arturo on 07/03/2017.
 */

public class MyHttpHandler extends AsyncTask<String, String, String> {
    private static final String TAG = MyHttpHandler.class.getSimpleName();
    private Context appContext;
    ProgressDialog progressDialog;

    public MyHttpHandler(Context context){
        appContext = context;
    }

    @Override
    protected String doInBackground(String... reqUrl) {
        //onProgressUpdate("Contacting server");
        String response = Parsing.getHttpResponse(appContext, reqUrl[0]);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
/*
    @Override
    protected void onProgressUpdate(String... text){
        TextView msgDisplay = (TextView)View.findViewById(R.id.errorsView);
        msgDisplay.setText(text[0]);
    }
*/
    /*
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(appContext,"ProgressDialog","accessing URL");
    }*/
}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
=======
package dev.nohasmith.venya_android_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by arturo on 07/03/2017.
 */

public class MyHttpHandler extends AsyncTask<String, String, String> {
    private static final String TAG = MyHttpHandler.class.getSimpleName();
    private Context appContext;
    ProgressDialog progressDialog;

    public MyHttpHandler(Context context){
        appContext = context;
    }

    @Override
    protected String doInBackground(String... reqUrl) {
        //onProgressUpdate("Contacting server");
        String response = Parsing.getHttpResponse(appContext, reqUrl[0]);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
/*
    @Override
    protected void onProgressUpdate(String... text){
        TextView msgDisplay = (TextView)View.findViewById(R.id.errorsView);
        msgDisplay.setText(text[0]);
    }
*/
    /*
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(appContext,"ProgressDialog","accessing URL");
    }*/
}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
