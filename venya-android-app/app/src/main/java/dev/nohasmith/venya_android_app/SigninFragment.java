<<<<<<< HEAD
<<<<<<< HEAD
package dev.nohasmith.venya_android_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;
    TextView signinTitle;
    Context appContext;
    private String usernameReceived;
    private String passwordReceived;

    TextView toRegister;
    TextView lostUsername;
    TextView lostPassword;

    public SigninFragment() {
        // Required empty public constructor
    }

    public SigninFragment(String field, String value) {
        switch(field) {
            case "username":
                this.usernameReceived = value;
                break;
            case "password":
                this.passwordReceived = value;
                break;
        }
    }

    /*
    public SigninFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface SigninListener {
        void signinClicked(String sessionid, FullCustomerSettings customer);
    }
    private SigninListener listener;

    interface ToRegisterListener {
        void toRegisterClicked(int position);
    }
    private ToRegisterListener regListener;

    interface ToLostUsernameListener {
        void toLostUsernameClicked(int position);
    }
    private ToLostUsernameListener usernameListener;

    interface ToLostPasswordListener {
        void toLostPasswordClicked(int position);
    }
    private ToLostPasswordListener passwordListener;
    
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
            throw new RuntimeException(context.toString() + " (SigninListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToRegisterListener) {
            regListener = (ToRegisterListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToREgisterListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostUsernameListener) {
            usernameListener = (ToLostUsernameListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostUsernameListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostPasswordListener) {
            passwordListener = (ToLostPasswordListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostPasswordListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameInput);
        passwordInput = (EditText)view.findViewById(R.id.passwordInput);

        if ( usernameReceived != null ) {
            usernameInput.setText(usernameReceived);
        }
        if ( passwordReceived != null ) {
            passwordInput.setText(passwordReceived);
        }

        submitButton = (Button)view.findViewById(R.id.signinButton);

        toRegister = (TextView)view.findViewById(R.id.registerLink) ;
        Parsing.displayTextView(appContext,toRegister,new int[] {R.string.signup_question,R.string.signup_link});

        lostUsername = (TextView)view.findViewById(R.id.lostUsernameLink);
        lostPassword = (TextView)view.findViewById(R.id.lostPAsswordLink);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        signinTitle = (TextView)view.findViewById(R.id.signinTitle);
        Parsing.displayTextView(appContext,signinTitle,R.string.signin_title);
        //signinTitle.setText(getResources().getString(R.string.signin_title));

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int registerFragmentPosition = Parsing.getIndexOf(signinOptions,"register");
                //Log.d("SigninFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                regListener.toRegisterClicked(registerFragmentPosition);
            }
        });

        lostUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int usernameFragmentPosition = Parsing.getIndexOf(signinOptions,"lostusername");
                Log.d("SigninFragment","index of \"lostusername\" in signinOptions = " + usernameFragmentPosition);
                usernameListener.toLostUsernameClicked(usernameFragmentPosition);
            }
        });

        lostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int passwordFragmentPosition = Parsing.getIndexOf(signinOptions,"lostpassword");
                Log.d("SigninFragment","index of \"lostpassword\" in signinOptions = " + passwordFragmentPosition);
                passwordListener.toLostPasswordClicked(passwordFragmentPosition);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                // check that username is not empty and is correctly formatted
                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_username,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_username), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_username)} ));
                }
                // check that password is not empty and is correctly formatted
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_password,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_password), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_password)} ));
                }
                // verify that the username is not the default
                else if ( username.equals(getResources().getString(R.string.default_username)) ) {
                    Parsing.displayTextView(appContext,errorsView,R.string.errors_notregistered);
                } else {
                    //Toast toast = new Toast(appContext);
                    //toast.makeText(appContext,getResources().getString(R.string.signin_connecting),Toast.LENGTH_SHORT).show();
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
                            String errormessage = "errors_" + (String) parsedResponse.get("errormessage");
                            Parsing.displayTextView(appContext,errorsView,errormessage);
                            //String displayTextView = Parsing.formatMessage(new String [] {getResources().getString(Parsing.getResId(appContext, errormessage))});
                            //errorsView.setText(displayTextView);
                        } else {

                            String action = (String) parsedResponse.get("action");
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("MAIN Activity","ID after parsing = " + customer.getId().getValue());

                            if ( ! customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                Parsing.displayTextView(appContext,errorsView,R.string.errors_sessionopened);
                                //String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                //errorsView.setText(errormessage);
                            } else {
                                String id = (String)customer.getId().getValue();
                                //Log.d("MAIN Activity","ID = " + id);
                                String sessionid = Parsing.randomSessionID(id);
                                //Log.d("MAIN Activity","session id generated = " + sessionid);
                                //errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext,id,sessionid,"customer");
                                Log.d("MAIN Activity","session id updated = " + updatedSessionid);

                                if ( updatedSessionid == null ) {
                                    Parsing.displayTextView(appContext,errorsView,R.string.errors_nullfromserver);
                                    //errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if ( ! sessionid.equals(updatedSessionid) ) {
                                    try {
                                        Parsing.displayTextView(appContext,errorsView,"errors_" + updatedSessionid);
                                        //errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + updatedSessionid)));
                                    } catch (Exception e) {
                                        Parsing.displayTextView(appContext,errorsView,R.string.errors_invalidsessionid);
                                        //errorsView.setText(getResources().getString(R.string.errors_invalidsessionid));
                                    }
                                } else {
                                    //update customer with session id generated
                                    customer.setField("sessionid",sessionid);
                                    listener.signinClicked(sessionid,customer);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
=======
package dev.nohasmith.venya_android_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;
    TextView signinTitle;
    Context appContext;
    private String usernameReceived;
    private String passwordReceived;

    TextView toRegister;
    TextView lostUsername;
    TextView lostPassword;

    public SigninFragment() {
        // Required empty public constructor
    }

    public SigninFragment(String field, String value) {
        switch(field) {
            case "username":
                this.usernameReceived = value;
                break;
            case "password":
                this.passwordReceived = value;
                break;
        }
    }

    /*
    public SigninFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface SigninListener {
        void signinClicked(String sessionid, FullCustomerSettings customer);
    }
    private SigninListener listener;

    interface ToRegisterListener {
        void toRegisterClicked(int position);
    }
    private ToRegisterListener regListener;

    interface ToLostUsernameListener {
        void toLostUsernameClicked(int position);
    }
    private ToLostUsernameListener usernameListener;

    interface ToLostPasswordListener {
        void toLostPasswordClicked(int position);
    }
    private ToLostPasswordListener passwordListener;
    
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
            throw new RuntimeException(context.toString() + " (SigninListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToRegisterListener) {
            regListener = (ToRegisterListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToREgisterListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostUsernameListener) {
            usernameListener = (ToLostUsernameListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostUsernameListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostPasswordListener) {
            passwordListener = (ToLostPasswordListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostPasswordListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameInput);
        passwordInput = (EditText)view.findViewById(R.id.passwordInput);

        if ( usernameReceived != null ) {
            usernameInput.setText(usernameReceived);
        }
        if ( passwordReceived != null ) {
            passwordInput.setText(passwordReceived);
        }

        submitButton = (Button)view.findViewById(R.id.signinButton);

        toRegister = (TextView)view.findViewById(R.id.registerLink) ;
        Parsing.displayTextView(appContext,toRegister,new int[] {R.string.signup_question,R.string.signup_link});

        lostUsername = (TextView)view.findViewById(R.id.lostUsernameLink);
        lostPassword = (TextView)view.findViewById(R.id.lostPAsswordLink);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        signinTitle = (TextView)view.findViewById(R.id.signinTitle);
        Parsing.displayTextView(appContext,signinTitle,R.string.signin_title);
        //signinTitle.setText(getResources().getString(R.string.signin_title));

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int registerFragmentPosition = Parsing.getIndexOf(signinOptions,"register");
                //Log.d("SigninFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                regListener.toRegisterClicked(registerFragmentPosition);
            }
        });

        lostUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int usernameFragmentPosition = Parsing.getIndexOf(signinOptions,"lostusername");
                Log.d("SigninFragment","index of \"lostusername\" in signinOptions = " + usernameFragmentPosition);
                usernameListener.toLostUsernameClicked(usernameFragmentPosition);
            }
        });

        lostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int passwordFragmentPosition = Parsing.getIndexOf(signinOptions,"lostpassword");
                Log.d("SigninFragment","index of \"lostpassword\" in signinOptions = " + passwordFragmentPosition);
                passwordListener.toLostPasswordClicked(passwordFragmentPosition);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                // check that username is not empty and is correctly formatted
                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_username,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_username), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_username)} ));
                }
                // check that password is not empty and is correctly formatted
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_password,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_password), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_password)} ));
                }
                // verify that the username is not the default
                else if ( username.equals(getResources().getString(R.string.default_username)) ) {
                    Parsing.displayTextView(appContext,errorsView,R.string.errors_notregistered);
                } else {
                    //Toast toast = new Toast(appContext);
                    //toast.makeText(appContext,getResources().getString(R.string.signin_connecting),Toast.LENGTH_SHORT).show();
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
                            String errormessage = "errors_" + (String) parsedResponse.get("errormessage");
                            Parsing.displayTextView(appContext,errorsView,errormessage);
                            //String displayTextView = Parsing.formatMessage(new String [] {getResources().getString(Parsing.getResId(appContext, errormessage))});
                            //errorsView.setText(displayTextView);
                        } else {

                            String action = (String) parsedResponse.get("action");
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("MAIN Activity","ID after parsing = " + customer.getId().getValue());

                            if ( ! customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                Parsing.displayTextView(appContext,errorsView,R.string.errors_sessionopened);
                                //String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                //errorsView.setText(errormessage);
                            } else {
                                String id = (String)customer.getId().getValue();
                                //Log.d("MAIN Activity","ID = " + id);
                                String sessionid = Parsing.randomSessionID(id);
                                //Log.d("MAIN Activity","session id generated = " + sessionid);
                                //errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext,id,sessionid,"customer");
                                Log.d("MAIN Activity","session id updated = " + updatedSessionid);

                                if ( updatedSessionid == null ) {
                                    Parsing.displayTextView(appContext,errorsView,R.string.errors_nullfromserver);
                                    //errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if ( ! sessionid.equals(updatedSessionid) ) {
                                    try {
                                        Parsing.displayTextView(appContext,errorsView,"errors_" + updatedSessionid);
                                        //errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + updatedSessionid)));
                                    } catch (Exception e) {
                                        Parsing.displayTextView(appContext,errorsView,R.string.errors_invalidsessionid);
                                        //errorsView.setText(getResources().getString(R.string.errors_invalidsessionid));
                                    }
                                } else {
                                    //update customer with session id generated
                                    customer.setField("sessionid",sessionid);
                                    listener.signinClicked(sessionid,customer);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
=======
package dev.nohasmith.venya_android_app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.TypedArrayUtils;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;
    TextView signinTitle;
    Context appContext;
    private String usernameReceived;
    private String passwordReceived;

    TextView toRegister;
    TextView lostUsername;
    TextView lostPassword;

    public SigninFragment() {
        // Required empty public constructor
    }

    public SigninFragment(String field, String value) {
        switch(field) {
            case "username":
                this.usernameReceived = value;
                break;
            case "password":
                this.passwordReceived = value;
                break;
        }
    }

    /*
    public SigninFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface SigninListener {
        void signinClicked(String sessionid, FullCustomerSettings customer);
    }
    private SigninListener listener;

    interface ToRegisterListener {
        void toRegisterClicked(int position);
    }
    private ToRegisterListener regListener;

    interface ToLostUsernameListener {
        void toLostUsernameClicked(int position);
    }
    private ToLostUsernameListener usernameListener;

    interface ToLostPasswordListener {
        void toLostPasswordClicked(int position);
    }
    private ToLostPasswordListener passwordListener;
    
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
            throw new RuntimeException(context.toString() + " (SigninListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToRegisterListener) {
            regListener = (ToRegisterListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToREgisterListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostUsernameListener) {
            usernameListener = (ToLostUsernameListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostUsernameListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToLostPasswordListener) {
            passwordListener = (ToLostPasswordListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToLostPasswordListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameInput);
        passwordInput = (EditText)view.findViewById(R.id.passwordInput);

        if ( usernameReceived != null ) {
            usernameInput.setText(usernameReceived);
        }
        if ( passwordReceived != null ) {
            passwordInput.setText(passwordReceived);
        }

        submitButton = (Button)view.findViewById(R.id.signinButton);

        toRegister = (TextView)view.findViewById(R.id.registerLink) ;
        Parsing.displayTextView(appContext,toRegister,new int[] {R.string.signup_question,R.string.signup_link});

        lostUsername = (TextView)view.findViewById(R.id.lostUsernameLink);
        lostPassword = (TextView)view.findViewById(R.id.lostPAsswordLink);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        signinTitle = (TextView)view.findViewById(R.id.signinTitle);
        Parsing.displayTextView(appContext,signinTitle,R.string.signin_title);
        //signinTitle.setText(getResources().getString(R.string.signin_title));

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int registerFragmentPosition = Parsing.getIndexOf(signinOptions,"register");
                //Log.d("SigninFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                regListener.toRegisterClicked(registerFragmentPosition);
            }
        });

        lostUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int usernameFragmentPosition = Parsing.getIndexOf(signinOptions,"lostusername");
                Log.d("SigninFragment","index of \"lostusername\" in signinOptions = " + usernameFragmentPosition);
                usernameListener.toLostUsernameClicked(usernameFragmentPosition);
            }
        });

        lostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int passwordFragmentPosition = Parsing.getIndexOf(signinOptions,"lostpassword");
                Log.d("SigninFragment","index of \"lostpassword\" in signinOptions = " + passwordFragmentPosition);
                passwordListener.toLostPasswordClicked(passwordFragmentPosition);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                // check that username is not empty and is correctly formatted
                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_username,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_username), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_username)} ));
                }
                // check that password is not empty and is correctly formatted
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[]{R.string.customer_password,R.string.errors_required});
                    //errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_password), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                    //errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_password)} ));
                }
                // verify that the username is not the default
                else if ( username.equals(getResources().getString(R.string.default_username)) ) {
                    Parsing.displayTextView(appContext,errorsView,R.string.errors_notregistered);
                } else {
                    //Toast toast = new Toast(appContext);
                    //toast.makeText(appContext,getResources().getString(R.string.signin_connecting),Toast.LENGTH_SHORT).show();
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
                            String errormessage = "errors_" + (String) parsedResponse.get("errormessage");
                            Parsing.displayTextView(appContext,errorsView,errormessage);
                            //String displayTextView = Parsing.formatMessage(new String [] {getResources().getString(Parsing.getResId(appContext, errormessage))});
                            //errorsView.setText(displayTextView);
                        } else {

                            String action = (String) parsedResponse.get("action");
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("MAIN Activity","ID after parsing = " + customer.getId().getValue());

                            if ( ! customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                Parsing.displayTextView(appContext,errorsView,R.string.errors_sessionopened);
                                //String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                //errorsView.setText(errormessage);
                            } else {
                                String id = (String)customer.getId().getValue();
                                //Log.d("MAIN Activity","ID = " + id);
                                String sessionid = Parsing.randomSessionID(id);
                                //Log.d("MAIN Activity","session id generated = " + sessionid);
                                //errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext,id,sessionid,"customer");
                                Log.d("MAIN Activity","session id updated = " + updatedSessionid);

                                if ( updatedSessionid == null ) {
                                    Parsing.displayTextView(appContext,errorsView,R.string.errors_nullfromserver);
                                    //errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if ( ! sessionid.equals(updatedSessionid) ) {
                                    try {
                                        Parsing.displayTextView(appContext,errorsView,"errors_" + updatedSessionid);
                                        //errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + updatedSessionid)));
                                    } catch (Exception e) {
                                        Parsing.displayTextView(appContext,errorsView,R.string.errors_invalidsessionid);
                                        //errorsView.setText(getResources().getString(R.string.errors_invalidsessionid));
                                    }
                                } else {
                                    //update customer with session id generated
                                    customer.setField("sessionid",sessionid);
                                    listener.signinClicked(sessionid,customer);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
