package dev.nohasmith.venya_android_app;


import android.content.res.Configuration;
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
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.nohasmith.venya_android_app.MainActivity.languages_from_locale;
import static dev.nohasmith.venya_android_app.MainActivity.secretFields;
import static dev.nohasmith.venya_android_app.MainActivity.securityCheckFields;
import static dev.nohasmith.venya_android_app.MainActivity.signinOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    TextView errorsView;
    TextView registerTitle;

    EditText uidInput;
    EditText usernameInput;
    EditText surnameInput;
    EditText dobInput;
    EditText emailInput;
    EditText confirmEmailInput;
    EditText phoneInput;
    EditText passwordInput;
    EditText confirmPasswordInput;

    TextView toSignin;
    TextView getId;

    Button submitButton;

    Context appContext;

    FullCustomerSettings customer;

    //String lang = "eng";

    public RegisterFragment() {
        // Required empty public constructor
    }

    public RegisterFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            return inflater.inflate(R.layout.register_fragment, container, false);
    }

    interface RegisterListener {
        void registerClicked(String sessionid, FullCustomerSettings customer);
    }
    private RegisterListener listener;

    interface ToSigninListener {
        void toSigninClicked(int position);
    }
    private ToSigninListener signListener;

    interface GetIdListener {
        //void getIdClicked(String url);
        void getIdClicked();
    }
    private GetIdListener getidListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof RegisterListener ) {
            listener = (RegisterListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ResgisterListener@Register) must implement OnFragmentInteractionListener");
        }

        if ( context instanceof ToSigninListener ) {
            signListener = (ToSigninListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ToSigninListener@Register) must implement OnFragmentInteractionListener");
        }

        if ( context instanceof GetIdListener ) {
            getidListener = (GetIdListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (GetIdListener@Register) must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Configuration config = getResources().getConfiguration();
        final Locale locale = Parsing.getLocale(config);

        /*HashMap<String,String> languages_from_locale = new HashMap<String,String>();
        languages_from_locale.put("es","esp");
        languages_from_locale.put("en","esp");*/

        /*
        if (locale.getLanguage().equals(Parsing.getMapKeyFromValue(languages_from_locale,"esp")) {
            lang = "esp";
        }*/

        appContext = view.getContext();

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        registerTitle = (TextView)view.findViewById(R.id.registerTitle);
        registerTitle.setText(getResources().getString(R.string.form_enterdetails));

        uidInput = (EditText)view.findViewById(R.id.uidInput);
        usernameInput = (EditText)view.findViewById(R.id.usernameInput);
        surnameInput = (EditText)view.findViewById(R.id.dobInput);
        dobInput = (EditText)view.findViewById(R.id.emailInput);
        //dobInput.setHint(Parsing.formatMessage(new String[] {getResources().getString(R.string.customer_dob)," - ",getResources().getString(R.string.form_optional)}));
        emailInput = (EditText)view.findViewById(R.id.emailInput);
        confirmEmailInput = (EditText)view.findViewById(R.id.confirmEmail);
        phoneInput = (EditText)view.findViewById(R.id.phoneInput);
        phoneInput.setHint(Parsing.formatMessage(new String[] {getResources().getString(R.string.customer_phone)," - ",getResources().getString(R.string.form_optional)}));
        emailInput = (EditText)view.findViewById(R.id.emailInput);passwordInput = (EditText)view.findViewById(R.id.passwordInput);
        confirmPasswordInput = (EditText)view.findViewById(R.id.confirmPassword);

        if ( customer != null ) {
            for ( int i=0; i<securityCheckFields.length; i++ ) {
                String field = securityCheckFields[i];
                CustomerField customerField;
                try {
                    customerField = customer.getField(field);
                    String value = (String)customerField.getValue();
                    switch(field){
                        case "id":
                            uidInput.setText(value);
                            uidInput.setEnabled(false);
                            break;
                        case "surname":
                            surnameInput.setText(value);
                            surnameInput.setEnabled(false);
                            break;
                        case "dob":
                            dobInput.setText(Parsing.formatDate(value));
                            dobInput.setEnabled(false);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    Log.e("RegisterFragment","Unable to add \"" + field + "\" to the form");
                    e.printStackTrace();
                }
            }
        }

        toSignin = (TextView)view.findViewById(R.id.signinLink);
        String toSigninText = Parsing.formatMessage(new String[] {getResources().getString(R.string.signin_already),getResources().getString(R.string.signin_button)});
        toSignin.setText(toSigninText);
        toSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                signListener.toSigninClicked(Parsing.getIndexOf(signinOptions,"signin"));
            }
        });

        getId = (TextView)view.findViewById(R.id.forgottenId);
        getId.setOnClickListener(new View.OnClickListener() {
            /*
            @Override
            public void onClick(View v) {
                String url = "http://" + getResources().getString(R.string.venya_node_server) +
                        "/" + getResources().getString(R.string.pages_lostid);
                getidListener.getIdClicked(url);
            }
            */
            @Override
            public void onClick(View v) {
                //String url = "http://" + getResources().getString(R.string.venya_node_server) + "/" + getResources().getString(R.string.pages_lostid);
                getidListener.getIdClicked();
            }
        });

        submitButton = (Button)view.findViewById(R.id.registerButton);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                errorsView.setText("");

                String uid = uidInput.getText().toString();
                String username = usernameInput.getText().toString();
                String surname = surnameInput.getText().toString();
                String dob = dobInput.getText().toString().replaceAll("\\/","");
                String email = emailInput.getText().toString();
                String confirmEmail = confirmEmailInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                // Check user id is filled and correctly fomatted
                if ( ! Parsing.checkRequired(appContext,uid) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_uid), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkUidFormat(appContext,uid) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_uid)} ));
                }
                // Check username is filled and correctly fomatted
                else if ( ! Parsing.checkRequired(appContext,username) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_username), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_username)} ));
                }
                // Check surname is filled
                else if ( ! Parsing.checkRequired(appContext,surname) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_surname), getResources().getString(R.string.errors_required)}));
                }
                // Check date of birth is filled and correctly fomatted
                else if ( ! Parsing.checkRequired(appContext,dob) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_username), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkDateFormat(appContext,dob) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_dob)} ));
                }
                // Check email is filled and correctly fomatted and matches the cofirm email value
                else if ( ! Parsing.checkRequired(appContext,email) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_email), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkEmailFormat(appContext,email) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_email)} ));
                } else if ( ! confirmEmail.equals(email) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {"confirmEmail",getResources().getString(R.string.customer_email),getResources().getString(R.string.errors_notmatch)} ));
                }
                // Check password is filled and correctly fomatted and matches the cofirm email value
                else if ( ! Parsing.checkRequired(appContext,password) ) {
                    errorsView.setText(Parsing.formatMessage(new String[]{getResources().getString(R.string.customer_password), getResources().getString(R.string.errors_required)}));
                } else if ( ! Parsing.checkPasswordFormat(appContext,password) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_password)} ));
                } else if ( ! confirmPassword.equals(password) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {"confirmPassword",getResources().getString(R.string.customer_password),getResources().getString(R.string.errors_notmatch)} ));
                }
                // if engtered, check phone format is valid
                else if ( ! phone.equals("") && ! Parsing.checkPhoneFormat(appContext,phone) ) {
                    errorsView.setText(Parsing.formatMessage(new String [] {getResources().getString(R.string.errors_badformat), getResources().getString(R.string.customer_phone)} ));
                }

                if ( errorsView.getText().equals("") ) {
                    String response = null;
                    String lang = languages_from_locale.get(locale.getLanguage());
                    if ( lang == null ) {
                        lang = getResources().getString(R.string.default_lang);
                    }
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/register?type=customer" +
                            "&id=" + uid +
                            "&surname=" + surname +
                            "&dob=" + dob +
                            "&email=" + email +
                            "&phone=" + phone +
                            "&username=" + username +
                            "&password=" + password +
                            "&language=" + lang;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.d("RegisterFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("RegisterFragment.OnClick]","NULL response from server");
                    } else {
                        Log.d("RegisterFragment", "HTTP response from server: " + response);
                        HashMap<String, Object> parsedResponse = Parsing.parseGetFullCustomerResponseJson(response, appContext);
                        String status = (String) parsedResponse.get("status");

                        if (!status.equals(getResources().getString(R.string.success_status))) {
                            String errormessage = (String) parsedResponse.get("errormessage");
                            try {
                                errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + errormessage)));
                            } catch (Exception e) {
                                errorsView.setText(getResources().getString(R.string.errors_unknwon) + ": " + errormessage);
                            }
                        } else {

                            String action = (String) parsedResponse.get("action");
                            FullCustomerSettings customer = (FullCustomerSettings) parsedResponse.get("customer");
                            Log.d("MAIN Activity", "ID after parsing = " + customer.getId().getValue());

                            if (!customer.getSessionid().getValue().equals(getResources().getString(R.string.sessionclosed))) {
                                String errormessage = Parsing.formatMessage(new String[]{getResources().getString(R.string.errors_sessionopened)});
                                errorsView.setText(errormessage);
                            } else {
                                String id = (String) customer.getId().getValue();
                                //Log.d("MAIN Activity","ID = " + id);
                                String sessionid = Parsing.randomSessionID(id);
                                //Log.d("MAIN Activity","session id generated = " + sessionid);
                                //errorsView.setText(sessionid);

                                String updatedSessionid = Parsing.setSessionId(appContext, id, sessionid, "customer");
                                Log.d("RegisterFragment", "session id updated = " + updatedSessionid);

                                if (updatedSessionid == null) {
                                    errorsView.setText(getResources().getString(R.string.errors_nullfromserver));
                                } else if (!sessionid.equals(updatedSessionid)) {
                                    try {
                                        errorsView.setText(getResources().getString(Parsing.getResId(appContext, "errors_" + updatedSessionid)));
                                    } catch (Exception e) {
                                        errorsView.setText(getResources().getString(R.string.errors_invalidsessionid));
                                    }
                                } else {
                                    //update customer with session id generated
                                    customer.setField("sessionid", sessionid);
                                    listener.registerClicked(sessionid, customer);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
