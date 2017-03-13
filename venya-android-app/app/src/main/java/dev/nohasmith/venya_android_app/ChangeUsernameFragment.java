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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeUsernameFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldUsernameInput;
    EditText newUsernameInput;
    EditText confirmUsernameInput;
    EditText passwordInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeUsernameFragment() {
        // Required empty public constructor
    }

    public ChangeUsernameFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateUsernameListener {
        void updateUsernameClicked(FullCustomerSettings customer);
    }
    private UpdateUsernameListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_username_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateUsernameListener) {
            updateListener = (UpdateUsernameListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateUsernameListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldUsernameInput = (EditText)view.findViewById(R.id.oldUsername);
        newUsernameInput = (EditText)view.findViewById(R.id.newUsername);
        confirmUsernameInput = (EditText)view.findViewById(R.id.confirmUsername);
        passwordInput = (EditText)view.findViewById(R.id.passwordChangeUsername);
        
        Parsing.setFormHint(appContext,oldUsernameInput,new int[] {R.string.form_old,R.string.customer_username});
        Parsing.setFormHint(appContext,newUsernameInput,new int[] {R.string.form_new,R.string.customer_username});
        Parsing.setFormHint(appContext,confirmUsernameInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_username});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeUsernameFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeUsernameButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldUsername = oldUsernameInput.getText().toString();
                String newUsername = newUsernameInput.getText().toString();
                String confirmUsername = confirmUsernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_username});
                } 
                else if ( ! Parsing.checkRequired(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_username});
                }
                else if ( newUsername.equals(oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_sameprevious  });
                }
                else if ( ! confirmUsername.equals(newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_username,R.string.errors_notmatch});
                }
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&password=" + password +
                            "&field=username" +
                            "&oldvalue=" + oldUsername +
                            "&newvalue=" + newUsername;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeUsernameFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeUsernameFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                Parsing.displayTextView(appContext, errorsView, "errors_" + errormessage);
                            } catch (Exception e) {
                                Parsing.displayTextView(appContext, errorsView, R.string.errors_unknwon);
                            }
                        } else {
                            // update the app customer object with the new address
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedUsername = updatedCustomer.getUsername();
                            int fix = customer.getUsername().getFix();
                            //customer.setUsername(new CustomerField("String",updatedUsername,fix));
                            customer.setField("username",updatedUsername);
                            //Log.d("ChangeUsernameFragment","ID = " + customer.getId());
                            updateListener.updateUsernameClicked(customer);
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeUsernameFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldUsernameInput;
    EditText newUsernameInput;
    EditText confirmUsernameInput;
    EditText passwordInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeUsernameFragment() {
        // Required empty public constructor
    }

    public ChangeUsernameFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateUsernameListener {
        void updateUsernameClicked(FullCustomerSettings customer);
    }
    private UpdateUsernameListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_username_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateUsernameListener) {
            updateListener = (UpdateUsernameListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateUsernameListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldUsernameInput = (EditText)view.findViewById(R.id.oldUsername);
        newUsernameInput = (EditText)view.findViewById(R.id.newUsername);
        confirmUsernameInput = (EditText)view.findViewById(R.id.confirmUsername);
        passwordInput = (EditText)view.findViewById(R.id.passwordChangeUsername);
        
        Parsing.setFormHint(appContext,oldUsernameInput,new int[] {R.string.form_old,R.string.customer_username});
        Parsing.setFormHint(appContext,newUsernameInput,new int[] {R.string.form_new,R.string.customer_username});
        Parsing.setFormHint(appContext,confirmUsernameInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_username});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeUsernameFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeUsernameButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldUsername = oldUsernameInput.getText().toString();
                String newUsername = newUsernameInput.getText().toString();
                String confirmUsername = confirmUsernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_username});
                } 
                else if ( ! Parsing.checkRequired(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_username});
                }
                else if ( newUsername.equals(oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_sameprevious  });
                }
                else if ( ! confirmUsername.equals(newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_username,R.string.errors_notmatch});
                }
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&password=" + password +
                            "&field=username" +
                            "&oldvalue=" + oldUsername +
                            "&newvalue=" + newUsername;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeUsernameFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeUsernameFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                Parsing.displayTextView(appContext, errorsView, "errors_" + errormessage);
                            } catch (Exception e) {
                                Parsing.displayTextView(appContext, errorsView, R.string.errors_unknwon);
                            }
                        } else {
                            // update the app customer object with the new address
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedUsername = updatedCustomer.getUsername();
                            int fix = customer.getUsername().getFix();
                            //customer.setUsername(new CustomerField("String",updatedUsername,fix));
                            customer.setField("username",updatedUsername);
                            //Log.d("ChangeUsernameFragment","ID = " + customer.getId());
                            updateListener.updateUsernameClicked(customer);
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeUsernameFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldUsernameInput;
    EditText newUsernameInput;
    EditText confirmUsernameInput;
    EditText passwordInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeUsernameFragment() {
        // Required empty public constructor
    }

    public ChangeUsernameFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateUsernameListener {
        void updateUsernameClicked(FullCustomerSettings customer);
    }
    private UpdateUsernameListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_username_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateUsernameListener) {
            updateListener = (UpdateUsernameListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateUsernameListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangeUsername) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldUsernameInput = (EditText)view.findViewById(R.id.oldUsername);
        newUsernameInput = (EditText)view.findViewById(R.id.newUsername);
        confirmUsernameInput = (EditText)view.findViewById(R.id.confirmUsername);
        passwordInput = (EditText)view.findViewById(R.id.passwordChangeUsername);
        
        Parsing.setFormHint(appContext,oldUsernameInput,new int[] {R.string.form_old,R.string.customer_username});
        Parsing.setFormHint(appContext,newUsernameInput,new int[] {R.string.form_new,R.string.customer_username});
        Parsing.setFormHint(appContext,confirmUsernameInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_username});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeUsernameFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeUsernameButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldUsername = oldUsernameInput.getText().toString();
                String newUsername = newUsernameInput.getText().toString();
                String confirmUsername = confirmUsernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_username});
                } 
                else if ( ! Parsing.checkRequired(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_username});
                }
                else if ( newUsername.equals(oldUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_username,R.string.errors_sameprevious  });
                }
                else if ( ! confirmUsername.equals(newUsername) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_username,R.string.errors_notmatch});
                }
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_password});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&password=" + password +
                            "&field=username" +
                            "&oldvalue=" + oldUsername +
                            "&newvalue=" + newUsername;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeUsernameFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeUsernameFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                Parsing.displayTextView(appContext, errorsView, "errors_" + errormessage);
                            } catch (Exception e) {
                                Parsing.displayTextView(appContext, errorsView, R.string.errors_unknwon);
                            }
                        } else {
                            // update the app customer object with the new address
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedUsername = updatedCustomer.getUsername();
                            int fix = customer.getUsername().getFix();
                            //customer.setUsername(new CustomerField("String",updatedUsername,fix));
                            customer.setField("username",updatedUsername);
                            //Log.d("ChangeUsernameFragment","ID = " + customer.getId());
                            updateListener.updateUsernameClicked(customer);
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
