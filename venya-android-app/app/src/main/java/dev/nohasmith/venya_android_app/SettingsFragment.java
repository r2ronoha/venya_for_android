package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

import static dev.nohasmith.venya_android_app.MainActivity.booleanFields;
import static dev.nohasmith.venya_android_app.MainActivity.customerFields;
import static dev.nohasmith.venya_android_app.MainActivity.dateFields;
import static dev.nohasmith.venya_android_app.MainActivity.listFields;
import static dev.nohasmith.venya_android_app.MainActivity.nameFields;
import static dev.nohasmith.venya_android_app.MainActivity.privateFields;
import static dev.nohasmith.venya_android_app.MainActivity.secretFields;
import static dev.nohasmith.venya_android_app.MainActivity.upperCaseFields;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    FullCustomerSettings customer;
    String sessionid;
    Context appContext;
    //String field;

    //public SettingsFragment() {}

    public SettingsFragment(String sessionid, FullCustomerSettings customer) {
        this.customer = customer;
        this.sessionid = sessionid;
    }

    // Listeners for each setting update "button"

    interface ChangeAddressListener {
        void changeAddressClicked(FullCustomerSettings customer);
    }
    private ChangeAddressListener addressListener;

    interface ChangeEmailListener {
        void changeEmailClicked(FullCustomerSettings customer);
    }
    private ChangeEmailListener emailListener;

    interface ChangeUsernameListener {
        void changeUsernameClicked(FullCustomerSettings customer);
    }
    private ChangeUsernameListener usernameListener;

    interface ChangePasswordListener {
        void changePasswordClicked(FullCustomerSettings customer);
    }
    private ChangePasswordListener passwordListener;

    interface ChangeLanguageListener {
        void changeLanguageClicked(FullCustomerSettings customer);
    }
    private ChangeLanguageListener languageListener;

    interface UpdateBooleanListener {
        void updateBooleanClicked(FullCustomerSettings customer);
    }
    private UpdateBooleanListener booleanListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ChangeAddressListener) {
            addressListener = (ChangeAddressListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangeAddressListener@Settings) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ChangeEmailListener) {
            emailListener = (ChangeEmailListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangeEmailListener@Settings) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ChangeUsernameListener) {
            usernameListener = (ChangeUsernameListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangeUsernameListener@Settings) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ChangePasswordListener) {
            passwordListener = (ChangePasswordListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangePasswordListener@Settings) must implement OnFragmentInteractionListener");
        }

        if (context instanceof UpdateBooleanListener) {
            booleanListener = (UpdateBooleanListener) context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateBooleanListener@Settings) must implement OnFragmentInteractionListener");
        }

        if (context instanceof ChangeLanguageListener) {
            languageListener = (ChangeLanguageListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangeLanguageListener@Settings) must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        appContext = getContext();

        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.customer_table);
        TableRow row;
        TableRow.LayoutParams layoutParams;
        TextView fieldCell = new TextView(getContext());
        TextView valueCell = new TextView(getContext());
        TextView optionCell = new TextView(getContext());

        row = new TableRow(getContext());
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        int rowCount = 0;
        fieldCell.setText(getResources().getString(R.string.settings_setting).toUpperCase());
        valueCell.setText(getResources().getString(R.string.settings_value).toUpperCase());
        optionCell.setText(getResources().getString(R.string.settings_option).toUpperCase());

        fieldCell.setPadding(10,10,10,10);
        valueCell.setPadding(10,10,10,10);
        optionCell.setPadding(10,10,10,10);

        row.addView(fieldCell);
        row.addView(valueCell);
        row.addView(optionCell);

        tableLayout.addView(row,rowCount++);

        if ( customer != null ) {
            //for ( int i=customerFields.length-1; i>=0; i-- ) {
            for ( int i=0; i<customerFields.length; i++ ) {
                final String field = customerFields[i];
                //if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) ) {
                if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) && customer.getField(field).getFix() == 0 ) {
                        row = new TableRow(getContext());
                    layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(layoutParams);

                    fieldCell = new TextView(getContext());
                    int langFieldId = Parsing.getResId(getContext(),"customer_" + field);
                    fieldCell.setText(Parsing.formatMessage(new String [] {getResources().getString(langFieldId)}));
                    fieldCell.setPadding(10,10,10,10);
                    row.addView(fieldCell);

                    valueCell = new TextView(getContext());
                    optionCell = new TextView(getContext());
                    String value;
                    String option;

                    if ( field.equals("address") ) {
                        // iterate through address fields
                        Address address = (Address)customer.getField(field).getValue();
                        value = address.formatAddress();
                    } else if ( Arrays.asList(booleanFields).contains(field) ) {
                        boolean boolValue = (boolean) customer.getField(field).getValue();
                        value = (boolValue) ? getResources().getString(R.string.true_value) : getResources().getString(R.string.false_value);
                    } else {
                        value = (String)customer.getField(field).getValue();
                        if ( Arrays.asList(secretFields).contains(field) ) {
                            value = Parsing.hideValue(value);
                        }
                    }

                    if ( Arrays.asList(nameFields).contains(field) ) {
                        value = Parsing.formatName(value);
                    } else if ( Arrays.asList(upperCaseFields).contains(field) ) {
                        value = value.toUpperCase();
                    } else if ( Arrays.asList(dateFields).contains(field) ) {
                        value = Parsing.formatDate(value);
                    }
                    valueCell.setText(value);
                    valueCell.setPadding(10,10,10,10);
                    row.addView(valueCell);

                    if ( Arrays.asList(listFields).contains(field) ) {
                        option = getResources().getString(R.string.settings_listoption);
                    }else if ( Arrays.asList(booleanFields).contains(field) ) {
                        option = getResources().getString(R.string.settings_booleanoption);
                    } else {
                        option = getResources().getString(R.string.settings_stringoption);
                    }
                    optionCell.setText(option.toUpperCase());
                    optionCell.setPadding(10,10,10,10);

                    // attach listeners to option cell
                    switch(field) {
                        case "address":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addressListener.changeAddressClicked(customer);
                                }
                            });
                            break;
                        case "email":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    emailListener.changeEmailClicked(customer);
                                }
                            });
                            break;
                        case "username":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    usernameListener.changeUsernameClicked(customer);
                                }
                            });
                            break;
                        case "password":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    passwordListener.changePasswordClicked(customer);
                                }
                            });
                            break;
                        case "notifications":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateBoolean(customer,field);
                                }
                            });
                            break;
                        case "location":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateBoolean(customer,field);
                                }
                            });
                            break;
                        case "language":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    languageListener.changeLanguageClicked(customer);
                                }
                            });
                    }

                    row.addView(optionCell);

                    tableLayout.addView(row,rowCount++);
                }
            }
        }
    }

    public void updateBoolean(FullCustomerSettings customer, String field) {
        //Log.d("updateBoolean","field = " + field + "=" + (String)customer.getField(field).getValue());
        Boolean currentValue = (boolean)customer.getField(field).getValue();
        Boolean newValue = ! currentValue;

        String reqUrl = "http://" + getResources().getString(R.string.venya_node_server) + ":" + getResources().getString(R.string.venya_node_port) +
                "/updateSetting?type=customer&action=update" +
                "&id=" + (String)customer.getId().getValue() +
                "&field=" + field +
                "&newvalue=" + newValue.toString();
        MyHttpHandler httpHandler = new MyHttpHandler(appContext);
        String response = null;
        try {
            response = httpHandler.execute(reqUrl).get();
        } catch (Exception e) {
            Log.e("Settings.updateBoolean","Exception calling AsyncTask: " + e);
            e.printStackTrace();
        }
        if ( response == null ) {
            Log.e("Settings.updateBoolean]","NULL response from server");
        } else {
            customer.setField(field, newValue);
            booleanListener.updateBooleanClicked(customer);
        }
    }

}
