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
public class ChangePhoneFragment extends Fragment {
    FullCustomerSettings customer;

    EditText usernameInput;
    EditText newPhoneInput;
    EditText confirmPhoneInput;

    TextView errorsView;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangePhoneFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdatePhoneListener {
        void updatePhoneClicked(FullCustomerSettings customer);
    }

    private UpdatePhoneListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }

    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_phone_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdatePhoneListener) {
            updateListener = (UpdatePhoneListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdatePhoneListener@ChangePhone) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangePhone) must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText) view.findViewById(R.id.username);
        newPhoneInput = (EditText) view.findViewById(R.id.newPhone);
        confirmPhoneInput = (EditText) view.findViewById(R.id.confirmPhone);

        Parsing.setFormHint(appContext, usernameInput, new int [] {R.string.customer_username});
        Parsing.setFormHint(appContext, newPhoneInput, new int [] {R.string.form_new,R.string.customer_phone});
        Parsing.setFormHint(appContext, confirmPhoneInput, new int[]{R.string.form_confirm, R.string.form_new,R.string.customer_phone});

        errorsView = (TextView) view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView) view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("ChangePhoneFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button) view.findViewById(R.id.changePhoneButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String username = usernameInput.getText().toString().toLowerCase();
                String newPhone = newPhoneInput.getText().toString().toLowerCase();
                String confirmPhone = confirmPhoneInput.getText().toString().toLowerCase();
                String response = null;

                if (!Parsing.checkRequired(appContext, username)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.customer_username, R.string.errors_required});
                }
                else if (!Parsing.checkRequired(appContext, newPhone)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.form_new,R.string.customer_phone, R.string.errors_required});
                }
                else if (!Parsing.checkPhoneFormat(appContext, newPhone)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.errors_badformat, R.string.form_new,R.string.customer_phone});
                }
                else if (!confirmPhone.equals(newPhone)) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.form_old, R.string.form_and, R.string.form_new,R.string.customer_phone, R.string.errors_notmatch});
                }
                else if (!username.equals((String)customer.getUsername().getValue())) {
                    Parsing.displayTextView(appContext, errorsView, new int[]{R.string.errors_wrongcredentials});
                } else {
                    // send request to the server to check the credentials
                    String id = (String) customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=phone" +
                            "&newvalue=" + newPhone;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangePhoneFragment", "Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if (response == null) {
                        Log.e("ChangeEmail.OnClick]", "NULL response from server");
                    } else {
                        //Log.d("ChangePhoneFragment","HTTP response from server: " + response);
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
                            String updatedPhone = updatedCustomer.getPhone();
                            int fix = customer.getPhone().getFix(); //customer is the global variable customer
                            customer.setPhone(new CustomerField("String", updatedPhone, fix));
                            //Log.d("ChangePhoneFragment","ID = " + customer.getId());
                            updateListener.updatePhoneClicked(customer);
                        }
                    }
                }
            }
        });
    }

}
