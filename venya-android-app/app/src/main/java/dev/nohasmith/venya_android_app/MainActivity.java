package dev.nohasmith.venya_android_app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;

import java.util.HashMap;

class MainActivity extends AppCompatActivity {
    Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        usernameInput = (EditText)findViewById(R.id.usernameInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        submitButton = (Button)findViewById(R.id.signinButton);

        errorsView = (TextView)findViewById(R.id.errorsView);
        errorsView.setText("");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                if ( username.length() == 0 || password.length() == 0 ) {
                    String missingField = ( username.length() == 0 ) ? "username" : "password";
                    //Log.d("MainActivity.onClick","username: " + username + "(" + username.length() + ")");
                    //Log.d("MainActivity.onClick","password: " + password + "(" + password.length() + ")");
                    errorsView.setText(Parsing.formatMessage(new String[]{missingField, getResources().getString(R.string.errors_required)}));
                } else if ( username.equals(getResources().getString(R.string.default_username)) ) {
            /*
            if he username entered is the system's default, send error
            that means that the subscriber did not create/activate the account
             */
                        // Call signin fragment with error message
                        errorsView.setText(R.string.errors_notregistered);
                } else {
                    // send request to the server to check the credentials
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) + "/getCustomer?action=login&username=" + username + "&password=" + password;
                    //String response = Parsing.getHttpResponse(appContext,reqUrl);
                    //reqUrl = "http://www.google.com";
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.d("[MainActivity","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("[MainActivity.OnClick]","NULL response from server");
                    } else {
                        Log.d("MainActivity","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            errorsView.setText(getResources().getString(Parsing.getResId(appContext, errormessage)));
                        } else {

                            String action = (String) parsedResponse.get("action");
                            CustomerSettings customer = (CustomerSettings) parsedResponse.get("customer");

                            if ( ! customer.getSessionid().equals(getResources().getString(R.string.sessionclosed))) {
                                String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                errorsView.setText(errormessage);
                            } else {
                                String id = customer.getId();
                                String sessionid = Parsing.randomSessionID(id);
                                errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext,id,sessionid,"customer");
                                if ( updatedSessionid == null ) {
                                    errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if ( ! sessionid.equals(updatedSessionid) ) {
                                    errorsView.setText(getResources().getString(Parsing.getResId(appContext,updatedSessionid)));
                                } else {
                                    // call home fragment with action and sessionid
                                    Context intentContext = MainActivity.this;
                                    Intent intent = new Intent(intentContext, Home.class);
                                    intent.putExtra("sessionid", sessionid);
                                    intent.putExtra("customer", customer);
                                    intentContext.startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
