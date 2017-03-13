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
public class ChangeAddressFragment extends Fragment {
    FullCustomerSettings customer;

    EditText newStreet;
    EditText newPostcode;
    EditText newCity;
    EditText newCountry;
    
    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;
    
    Context appContext;

    public ChangeAddressFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    /*
    public ChangeAddressFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface UpdateAddressListener {
        void updateAddressClicked(FullCustomerSettings customer);
    }
    private UpdateAddressListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_address_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateAddressListener) {
            updateListener = (UpdateAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateAddressListener@Signin) must implement OnFragmentInteractionListener");
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

        newStreet = (EditText)view.findViewById(R.id.changeStreet);
        newPostcode = (EditText)view.findViewById(R.id.changePostcode);
        newCity = (EditText)view.findViewById(R.id.changeCity);
        newCountry = (EditText)view.findViewById(R.id.changeCountry);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeAddressFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeAddressButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String street = newStreet.getText().toString().toLowerCase();
                String postcode = newPostcode.getText().toString().toLowerCase();
                String city = newCity.getText().toString().toLowerCase();
                String country = newCountry.getText().toString().toLowerCase();
                String response = null;

                Address newAddress = new Address(street,postcode,city,country);

                String newAddressStr = "street=" + newAddress.getStreet() + ";" +
                        "postcode=" + newAddress.getPostcode() + ";" +
                        "city=" + newAddress.getCity() + ";" +
                        "country=" + newAddress.getCountry();

                try {
                    newAddressStr = URLEncoder.encode(newAddressStr,"UTF-8");
                } catch (Exception e) {
                    Log.e("ChangeAddress","Failed to encode address " + newAddressStr);
                }

                if ( ! Parsing.checkRequired(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.address_postcode,R.string.errors_required});
                } else if ( ! Parsing.checkPostCodeFormat(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.address_postcode});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=address" +
                            "&newvalue=" + newAddressStr;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeAddressFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeAddressFragment","HTTP response from server: " + response);
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
                            Address updatedAddress = updatedCustomer.getAddress();
                            int fix = customer.getAddress().getFix();
                            customer.setAddress(new CustomerField("address",updatedAddress,fix));
                            //Log.d("ChangeAddressFragment","ID = " + customer.getId());
                            updateListener.updateAddressClicked(customer);
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
public class ChangeAddressFragment extends Fragment {
    FullCustomerSettings customer;

    EditText newStreet;
    EditText newPostcode;
    EditText newCity;
    EditText newCountry;
    
    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;
    
    Context appContext;

    public ChangeAddressFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    /*
    public ChangeAddressFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface UpdateAddressListener {
        void updateAddressClicked(FullCustomerSettings customer);
    }
    private UpdateAddressListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_address_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateAddressListener) {
            updateListener = (UpdateAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateAddressListener@Signin) must implement OnFragmentInteractionListener");
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

        newStreet = (EditText)view.findViewById(R.id.changeStreet);
        newPostcode = (EditText)view.findViewById(R.id.changePostcode);
        newCity = (EditText)view.findViewById(R.id.changeCity);
        newCountry = (EditText)view.findViewById(R.id.changeCountry);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeAddressFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeAddressButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String street = newStreet.getText().toString().toLowerCase();
                String postcode = newPostcode.getText().toString().toLowerCase();
                String city = newCity.getText().toString().toLowerCase();
                String country = newCountry.getText().toString().toLowerCase();
                String response = null;

                Address newAddress = new Address(street,postcode,city,country);

                String newAddressStr = "street=" + newAddress.getStreet() + ";" +
                        "postcode=" + newAddress.getPostcode() + ";" +
                        "city=" + newAddress.getCity() + ";" +
                        "country=" + newAddress.getCountry();

                try {
                    newAddressStr = URLEncoder.encode(newAddressStr,"UTF-8");
                } catch (Exception e) {
                    Log.e("ChangeAddress","Failed to encode address " + newAddressStr);
                }

                if ( ! Parsing.checkRequired(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.address_postcode,R.string.errors_required});
                } else if ( ! Parsing.checkPostCodeFormat(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.address_postcode});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=address" +
                            "&newvalue=" + newAddressStr;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeAddressFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeAddressFragment","HTTP response from server: " + response);
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
                            Address updatedAddress = updatedCustomer.getAddress();
                            int fix = customer.getAddress().getFix();
                            customer.setAddress(new CustomerField("address",updatedAddress,fix));
                            //Log.d("ChangeAddressFragment","ID = " + customer.getId());
                            updateListener.updateAddressClicked(customer);
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
public class ChangeAddressFragment extends Fragment {
    FullCustomerSettings customer;

    EditText newStreet;
    EditText newPostcode;
    EditText newCity;
    EditText newCountry;
    
    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;
    
    Context appContext;

    public ChangeAddressFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    /*
    public ChangeAddressFragment(Context context) {
        // Required empty public constructor
        appContext = context;
    }*/

    interface UpdateAddressListener {
        void updateAddressClicked(FullCustomerSettings customer);
    }
    private UpdateAddressListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_address_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdateAddressListener) {
            updateListener = (UpdateAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateAddressListener@Signin) must implement OnFragmentInteractionListener");
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

        newStreet = (EditText)view.findViewById(R.id.changeStreet);
        newPostcode = (EditText)view.findViewById(R.id.changePostcode);
        newCity = (EditText)view.findViewById(R.id.changeCity);
        newCountry = (EditText)view.findViewById(R.id.changeCountry);

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangeAddressFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changeAddressButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String street = newStreet.getText().toString().toLowerCase();
                String postcode = newPostcode.getText().toString().toLowerCase();
                String city = newCity.getText().toString().toLowerCase();
                String country = newCountry.getText().toString().toLowerCase();
                String response = null;

                Address newAddress = new Address(street,postcode,city,country);

                String newAddressStr = "street=" + newAddress.getStreet() + ";" +
                        "postcode=" + newAddress.getPostcode() + ";" +
                        "city=" + newAddress.getCity() + ";" +
                        "country=" + newAddress.getCountry();

                try {
                    newAddressStr = URLEncoder.encode(newAddressStr,"UTF-8");
                } catch (Exception e) {
                    Log.e("ChangeAddress","Failed to encode address " + newAddressStr);
                }

                if ( ! Parsing.checkRequired(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.address_postcode,R.string.errors_required});
                } else if ( ! Parsing.checkPostCodeFormat(appContext,postcode) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.address_postcode});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&field=address" +
                            "&newvalue=" + newAddressStr;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangeAddressFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangeAddressFragment","HTTP response from server: " + response);
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
                            Address updatedAddress = updatedCustomer.getAddress();
                            int fix = customer.getAddress().getFix();
                            customer.setAddress(new CustomerField("address",updatedAddress,fix));
                            //Log.d("ChangeAddressFragment","ID = " + customer.getId());
                            updateListener.updateAddressClicked(customer);
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
