package dev.nohasmith.venya_android_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;
    Context appContext;

    public SigninFragment() {
        // Required empty public constructor
    }

    interface SigninListener {
        void signinClicked(String sessionid, FullCustomerSettings customer);
    }

    private SigninListener listener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signin_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SigninListener) {
            listener = (SigninListener)context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameInput);
        passwordInput = (EditText)view.findViewById(R.id.passwordInput);
        submitButton = (Button)view.findViewById(R.id.signinButton);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
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
                    //String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) + "/getCustomer?action=login&username=" + username + "&password=" + password;
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/getFullSubscriberData?type=customer" +
                            "&action=login" +
                            "&username=" + username +
                            "&password=" + password;
                    //String response = Parsing.getHttpResponse(appContext,reqUrl);
                    //reqUrl = "http://www.google.com";
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.d("SigninFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("SigninFragmen.OnClick]","NULL response from server");
                    } else {
                        Log.d("SigninFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetFullCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            errorsView.setText(getResources().getString(Parsing.getResId(appContext, errormessage)));
                        } else {

                            String action = (String) parsedResponse.get("action");
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("MAIN Activity","ID after parsing = " + customer.getId().getValue());

                            if ( ! customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                errorsView.setText(errormessage);
                            } else {
                                String id = (String)customer.getId().getValue();
                                //Log.d("MAIN Activity","ID = " + id);
                                String sessionid = Parsing.randomSessionID(id);
                                //Log.d("MAIN Activity","session id generated = " + sessionid);
                                //errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext,id,sessionid,"customer");
                                Log.d("MAIN Activity","session id updated = " + updatedSessionid);

                                if ( updatedSessionid == null ) {
                                    errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if ( ! sessionid.equals(updatedSessionid) ) {
                                    errorsView.setText(getResources().getString(Parsing.getResId(appContext,updatedSessionid)));
                                } else {
                                    //update customer with session id generated
                                    customer.setField("sessionid",sessionid);
                                    listener.signinClicked(sessionid,customer);
                                    /*
                                    // call home fragment with action and sessionid
                                    Context intentContext = MainActivity.this;
                                    Intent intent = new Intent(intentContext, Home.class);
                                    intent.putExtra("sessionid", sessionid);
                                    intent.putExtra("customer", customer);
                                    intentContext.startActivity(intent);
                                    */
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
