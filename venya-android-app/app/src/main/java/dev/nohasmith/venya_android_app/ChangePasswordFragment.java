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
public class ChangePasswordFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldPasswordInput;
    EditText newPasswordInput;
    EditText confirmPasswordInput;
    EditText usernameInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public ChangePasswordFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdatePasswordListener {
        void updatePasswordClicked(FullCustomerSettings customer);
    }
    private UpdatePasswordListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdatePasswordListener) {
            updateListener = (UpdatePasswordListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdatePasswordListener@ChangePassword) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangePassword) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameChangePassword);
        oldPasswordInput = (EditText)view.findViewById(R.id.oldPasswordChange);
        newPasswordInput = (EditText)view.findViewById(R.id.newPasswordChange);
        confirmPasswordInput = (EditText)view.findViewById(R.id.confirmPasswordChange);

        Parsing.setFormHint(appContext,oldPasswordInput,new int[] {R.string.form_old,R.string.customer_password});
        Parsing.setFormHint(appContext,newPasswordInput,new int[] {R.string.form_new,R.string.customer_password});
        Parsing.setFormHint(appContext,confirmPasswordInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_password});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangePasswordFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changePasswordButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                String username = usernameInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                }
                else if ( ! Parsing.checkRequired(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_password});
                }
                else if ( ! Parsing.checkRequired(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_password});
                }
                else if ( newPassword.equals(oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_sameprevious  });
                }
                else if ( ! confirmPassword.equals(newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_password,R.string.errors_notmatch});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&username=" + username +
                            "&field=password" +
                            "&oldvalue=" + oldPassword +
                            "&newvalue=" + newPassword;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangePasswordFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangePasswordFragment","HTTP response from server: " + response);
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
                            String updatePassword = updatedCustomer.getPassword();
                            int fix = customer.getPassword().getFix();
                            customer.setPassword(new CustomerField("String",updatePassword,fix));
                            //Log.d("ChangePasswordFragment","ID = " + customer.getId());
                            updateListener.updatePasswordClicked(customer);
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
public class ChangePasswordFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldPasswordInput;
    EditText newPasswordInput;
    EditText confirmPasswordInput;
    EditText usernameInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public ChangePasswordFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdatePasswordListener {
        void updatePasswordClicked(FullCustomerSettings customer);
    }
    private UpdatePasswordListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdatePasswordListener) {
            updateListener = (UpdatePasswordListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdatePasswordListener@ChangePassword) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangePassword) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameChangePassword);
        oldPasswordInput = (EditText)view.findViewById(R.id.oldPasswordChange);
        newPasswordInput = (EditText)view.findViewById(R.id.newPasswordChange);
        confirmPasswordInput = (EditText)view.findViewById(R.id.confirmPasswordChange);

        Parsing.setFormHint(appContext,oldPasswordInput,new int[] {R.string.form_old,R.string.customer_password});
        Parsing.setFormHint(appContext,newPasswordInput,new int[] {R.string.form_new,R.string.customer_password});
        Parsing.setFormHint(appContext,confirmPasswordInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_password});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangePasswordFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changePasswordButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                String username = usernameInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                }
                else if ( ! Parsing.checkRequired(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_password});
                }
                else if ( ! Parsing.checkRequired(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_password});
                }
                else if ( newPassword.equals(oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_sameprevious  });
                }
                else if ( ! confirmPassword.equals(newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_password,R.string.errors_notmatch});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&username=" + username +
                            "&field=password" +
                            "&oldvalue=" + oldPassword +
                            "&newvalue=" + newPassword;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangePasswordFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangePasswordFragment","HTTP response from server: " + response);
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
                            String updatePassword = updatedCustomer.getPassword();
                            int fix = customer.getPassword().getFix();
                            customer.setPassword(new CustomerField("String",updatePassword,fix));
                            //Log.d("ChangePasswordFragment","ID = " + customer.getId());
                            updateListener.updatePasswordClicked(customer);
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
public class ChangePasswordFragment extends Fragment {
    FullCustomerSettings customer;

    EditText oldPasswordInput;
    EditText newPasswordInput;
    EditText confirmPasswordInput;
    EditText usernameInput;

    TextView errorsView;
    TextView title;

    Button submitButton;
    TextView cancelLink;

    Context appContext;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public ChangePasswordFragment(FullCustomerSettings customer) {
        // Required empty public constructor
        this.customer = customer;
    }

    interface UpdatePasswordListener {
        void updatePasswordClicked(FullCustomerSettings customer);
    }
    private UpdatePasswordListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    private CancelListener cancelListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UpdatePasswordListener) {
            updateListener = (UpdatePasswordListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdatePasswordListener@ChangePassword) must implement OnFragmentInteractionListener");
        }

        if (context instanceof CancelListener) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (CancelListener@ChangePassword) must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        appContext = view.getContext();
        //Parsing.setLocale(appContext,"es");

        usernameInput = (EditText)view.findViewById(R.id.usernameChangePassword);
        oldPasswordInput = (EditText)view.findViewById(R.id.oldPasswordChange);
        newPasswordInput = (EditText)view.findViewById(R.id.newPasswordChange);
        confirmPasswordInput = (EditText)view.findViewById(R.id.confirmPasswordChange);

        Parsing.setFormHint(appContext,oldPasswordInput,new int[] {R.string.form_old,R.string.customer_password});
        Parsing.setFormHint(appContext,newPasswordInput,new int[] {R.string.form_new,R.string.customer_password});
        Parsing.setFormHint(appContext,confirmPasswordInput,new int[] {R.string.form_confirm,R.string.form_new,R.string.customer_password});

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        cancelLink = (TextView)view.findViewById(R.id.cancelLink);
        cancelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("ChangePasswordFragment","index of \"register\" in signinOptions = " + registerFragmentPosition);
                cancelListener.cancelClicked(customer);
            }
        });

        submitButton = (Button)view.findViewById(R.id.changePasswordButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorsView.setText("");
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                String username = usernameInput.getText().toString();
                String response = null;

                if ( ! Parsing.checkRequired(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.customer_username,R.string.errors_required});
                } else if ( ! Parsing.checkUsernameFormat(appContext,username) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.customer_username});
                }
                else if ( ! Parsing.checkRequired(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_old,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_old,R.string.customer_password});
                }
                else if ( ! Parsing.checkRequired(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_required});
                } else if ( ! Parsing.checkPasswordFormat(appContext,newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.errors_badformat,R.string.form_new,R.string.customer_password});
                }
                else if ( newPassword.equals(oldPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.customer_password,R.string.errors_sameprevious  });
                }
                else if ( ! confirmPassword.equals(newPassword) ) {
                    Parsing.displayTextView(appContext,errorsView,new int[] {R.string.form_new,R.string.form_and,R.string.form_confirm,R.string.customer_password,R.string.errors_notmatch});
                }
                else {
                    // send request to the server to check the credentials
                    String id = (String)customer.getId().getValue();
                    String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                            "/updateSetting?type=customer" +
                            "&action=update" +
                            "&id=" + id +
                            "&username=" + username +
                            "&field=password" +
                            "&oldvalue=" + oldPassword +
                            "&newvalue=" + newPassword;
                    MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                    try {
                        response = httpHandler.execute(reqUrl).get();
                    } catch (Exception e) {
                        Log.e("ChangePasswordFragment","Exception calling AsyncTask: " + e);
                        e.printStackTrace();
                    }
                    if ( response == null ) {
                        Log.e("LostPasswordFragmen.OnClick]","NULL response from server");
                    } else {
                        //Log.d("ChangePasswordFragment","HTTP response from server: " + response);
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
                            String updatePassword = updatedCustomer.getPassword();
                            int fix = customer.getPassword().getFix();
                            customer.setPassword(new CustomerField("String",updatePassword,fix));
                            //Log.d("ChangePasswordFragment","ID = " + customer.getId());
                            updateListener.updatePasswordClicked(customer);
                        }
                    }
                }
            }
        });
    }

}
>>>>>>> 9891fe3813093e98677be2d856817ab32684daf5
