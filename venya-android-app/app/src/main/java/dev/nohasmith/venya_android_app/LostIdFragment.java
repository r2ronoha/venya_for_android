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

import java.util.Arrays;
import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostIdFragment extends Fragment {
    Button submitButton;
    EditText surnameInput;
    EditText firstnameInput;
    EditText dobInput;
    TextView errorsView;
    TextView title;
    TextView toSignin;
    Context appContext;

    TextView toRegister;

    public LostIdFragment() {
        // Required empty public constructor
    }

    /*
    public LostIdFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface LostIdListener {
        void lostIdClicked(FullCustomerSettings customer);
    }
    private LostIdListener listener;

    interface ToSigninListener {
        void toSigninClicked(int position);
    }
    private ToSigninListener signinListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lost_id_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LostIdListener) {
            listener = (LostIdListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (SigninListener@Signin) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ToSigninListener) {
            signinListener = (ToSigninListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToSigninListener@Signin) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        Parsing.setLocale(appContext,"es");

        surnameInput = (EditText)view.findViewById(R.id.surnameLostId);
        firstnameInput = (EditText)view.findViewById(R.id.firstnameLostId);
        dobInput = (EditText)view.findViewById(R.id.dobLostId);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        //title = (TextView)view.findViewById(R.id.lostIdTitle);
        //title.setText(getResources().getString(R.string.form_enterdetails));

        toSignin = (TextView)view.findViewById(R.id.toSignin);
        String toSigninText = Parsing.formatMessage(new String[] {getResources().getString(R.string.signin_already),getResources().getString(R.string.signin_button)});
        toSignin.setText(toSigninText);
        toSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int registerFragmentPosition = Parsing.getIndexOf(signinOptions,"signin");
                //Log.d("LostIdFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                signinListener.toSigninClicked(registerFragmentPosition);
            }
        });

        submitButton = (Button)view.findViewById(R.id.lostIdButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String surname = surnameInput.getText().toString();
                String firstname = firstnameInput.getText().toString();
                String dob = dobInput.getText().toString().replaceAll("\\/","");
                String response = null;

                if ( ! Parsing.checkRequired(appContext,surname) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_surname), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkRequired(appContext,firstname) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_firstname), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkRequired(appContext,dob) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_dob), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkDateFormat(appContext,dob) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_dob)} ));
                } else {
                    // send request to the server to check the credentials
                    //String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) + "/getCustomer?action=login&username=" + username + "&password=" + password;
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/getFullSubscriberData?type=customer" +
                            "&action=getid" +
                            "&surname=" + surname +
                            "&firstname=" + firstname +
                            "&dob=" + dob;
                    //String response = Parsing.getHttpResponse(appContext,reqUrl);
                    //reqUrl = "http://www.google.com";
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.d("LostIdFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostIdFragmen.OnClick]","NULL response from server");
                    } else {
                        Log.d("LostIdFragment","HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetFullCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + errormessage)));
                            } catch (Exception e) {
                                errorsView.setText(getResources().getString(R.string.errors_unknwon));
                            }
                        } else {
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("LostIdFragment","ID = " + customer.getId().getValue());

                            if ( ! customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                String errormessage = Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_sessionopened)});
                                errorsView.setText(errormessage);
                            } else {
                                listener.lostIdClicked(customer);
                            }
                        }
                    }
                }
            }
        });
    }

}
