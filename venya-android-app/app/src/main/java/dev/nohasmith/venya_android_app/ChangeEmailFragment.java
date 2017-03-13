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
public class ChangeEmailFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldEmailInput;
    EditText newEmailInput;
    EditText confirmEmailInput;

    TextView errorsView;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeEmailFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateEmailListener {
        void updateEmailClicked(FullCustomerSettings customer);
    }

    private UpdateEmailListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }

    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_email_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateEmailListener) {
            updateListener = (UpdateEmailListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateEmailListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@Signin) must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldEmailInput = (EditText) view.findViewById(R.id.oldEmail);
        newEmailInput = (EditText) view.findViewById(R.id.newEmail);
        confirmEmailInput = (EditText) view.findViewById(R.id.confirmEmail);

        Parsing.setFormHint(appContext, oldEmailInput, R.string.form_oldemail);
        Parsing.setFormHint(appContext, newEmailInput, R.string.form_newemail);
        Parsing.setFormHint(appContext, confirmEmailInput, new int[]{R.string.form_confirm, R.string.form_newemail});

        errorsView = (TextView) view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView) view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("ChangeEmailFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button) view.findViewById(R.id.changeEmailButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldEmail = oldEmailInput.getText().toString().toLowerCase();
                String newEmail = newEmailInput.getText().toString().toLowerCase();
                String confirmEmail = confirmEmailInput.getText().toString().toLowerCase();
                String response = null;

                if (!Parsing.checkRequired(appContext, oldEmail)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.form_oldemail, R.string.errors_required});
                } else if (!Parsing.checkEmailFormat(appContext, oldEmail)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.errors_badformat, R.string.form_oldemail});
                } else if (!Parsing.checkRequired(appContext, newEmail)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.form_newemail, R.string.errors_required});
                } else if (!Parsing.checkEmailFormat(appContext, newEmail)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.errors_badformat, R.string.form_newemail});
                } else if (!confirmEmail.equals(newEmail)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.form_old, R.string.form_and, R.string.form_newemail, R.string.errors_notmatch});
                } else {
                    // send request to the server to check the credentials
                    String id = (String) customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=email" +
                            "&newvalue=" + newEmail;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeEmailFragment", "Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if (response == null) {
                        Log.e("ChangeEmail.OnClick]", "NULL response from server");
                    } else {
                        //Log.d("ChangeEmailFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if (!status.equals(getResources().getString(R.string.success_status))) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                Parsing.displayTextView(appContext, errorsView, "errors_" + errormessage);
                            } catch (Exception e) {
                                Parsing.displayTextView(appContext, errorsView, R.string.errors_unknwon);
                            }
                        } else {
                            // update the app customer object with the new email
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedEmail = updatedCustomer.getEmail();
                            int fix = customer.getEmail().getFix(); //customer is the global variable customer
                            customer.setEmail(new CustomerField("String", updatedEmail, fix));
                            //Log.d("ChangeEmailFragment","ID = " + customer.getId());
                            updateListener.updateEmailClicked(customer);
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
public class ChangeEmailFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldEmailInput;
    EditText newEmailInput;
    EditText confirmEmailInput;

    TextView errorsView;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeEmailFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateEmailListener {
        void updateEmailClicked(FullCustomerSettings customer);
    }
    private UpdateEmailListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_email_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateEmailListener) {
            updateListener = (UpdateEmailListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateEmailListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldEmailInput = (EditText)view.findViewById(R.id.oldEmail);
        newEmailInput = (EditText)view.findViewById(R.id.newEmail);
        confirmEmailInput = (EditText)view.findViewById(R.id.confirmEmail);
        
        Parsing.setFormHint(appContext,oldEmailInput,R.string.form_oldemail);
        Parsing.setFormHint(appContext,newEmailInput,R.string.form_newemail);
        Parsing.setFormHint(appContext,confirmEmailInput,new int[] {R.string.form_confirm,R.string.form_newemail});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeEmailFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeEmailButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldEmail = oldEmailInput.getText().toString().toLowerCase();
                String newEmail = newEmailInput.getText().toString().toLowerCase();
                String confirmEmail = confirmEmailInput.getText().toString().toLowerCase();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,oldEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_oldemail,R.string.errors_required});
                } else if ( ! Parsing.checkEmailFormat(appContext,oldEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_oldemail});
                }
                else if ( ! Parsing.checkRequired(appContext,newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_newemail,R.string.errors_required});
                } else if ( ! Parsing.checkEmailFormat(appContext,newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_newemail});
                }
                else if ( ! confirmEmail.equals(newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.form_and,R.string.form_newemail,R.string.errors_notmatch});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=email" +
                            "&newvalue=" + newEmail;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeEmailFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("ChangeEmail.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeEmailFragment","HTTP response from server: " + response);
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
                            // update the app customer object with the new email
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedEmail = updatedCustomer.getEmail();
                            int fix = customer.getEmail().getFix(); //customer is the global variable customer
                            customer.setEmail(new CustomerField("String",updatedEmail,fix));
                            //Log.d("ChangeEmailFragment","ID = " + customer.getId());
                            updateListener.updateEmailClicked(customer);
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
public class ChangeEmailFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldEmailInput;
    EditText newEmailInput;
    EditText confirmEmailInput;

    TextView errorsView;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangeEmailFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdateEmailListener {
        void updateEmailClicked(FullCustomerSettings customer);
    }
    private UpdateEmailListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_email_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateEmailListener) {
            updateListener = (UpdateEmailListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateEmailListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        oldEmailInput = (EditText)view.findViewById(R.id.oldEmail);
        newEmailInput = (EditText)view.findViewById(R.id.newEmail);
        confirmEmailInput = (EditText)view.findViewById(R.id.confirmEmail);
        
        Parsing.setFormHint(appContext,oldEmailInput,R.string.form_oldemail);
        Parsing.setFormHint(appContext,newEmailInput,R.string.form_newemail);
        Parsing.setFormHint(appContext,confirmEmailInput,new int[] {R.string.form_confirm,R.string.form_newemail});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeEmailFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeEmailButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldEmail = oldEmailInput.getText().toString().toLowerCase();
                String newEmail = newEmailInput.getText().toString().toLowerCase();
                String confirmEmail = confirmEmailInput.getText().toString().toLowerCase();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,oldEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_oldemail,R.string.errors_required});
                } else if ( ! Parsing.checkEmailFormat(appContext,oldEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_oldemail});
                }
                else if ( ! Parsing.checkRequired(appContext,newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_newemail,R.string.errors_required});
                } else if ( ! Parsing.checkEmailFormat(appContext,newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_newemail});
                }
                else if ( ! confirmEmail.equals(newEmail) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.form_and,R.string.form_newemail,R.string.errors_notmatch});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=email" +
                            "&newvalue=" + newEmail;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeEmailFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("ChangeEmail.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeEmailFragment","HTTP response from server: " + response);
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
                            // update the app customer object with the new email
                            CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                            String updatedEmail = updatedCustomer.getEmail();
                            int fix = customer.getEmail().getFix(); //customer is the global variable customer
                            customer.setEmail(new CustomerField("String",updatedEmail,fix));
                            //Log.d("ChangeEmailFragment","ID = " + customer.getId());
                            updateListener.updateEmailClicked(customer);
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
