package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import static dev.nohasmith.venya_android_app.MainActivity.menuOptionsTags;
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

    interface ChangePhoneListener {
        void changePhoneClicked(FullCustomerSettings customer);
    }
    private ChangePhoneListener phoneListener;

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
        return inflater.inflate(R.layout.settings_fragment, container, false);
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

        if (context instanceof ChangePhoneListener) {
            phoneListener = (ChangePhoneListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (ChangePhoneListener@Settings) must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        appContext = getContext();

        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.customer_table);
        tableLayout.setBackgroundColor(appContext.getColor(R.color.colorPrimaryDark));
        TableRow row;
        TableRow.LayoutParams layoutParams;
        TextView fieldCell = new TextView(getContext());
        TextView valueCell = new TextView(getContext());
        TextView optionCell = new TextView(getContext());

        row = new TableRow(getContext());
        layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TableRow.LayoutParams fieldLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        fieldLP.width = 0;
        fieldLP.weight = 7;
        fieldLP.setMargins(1,1,1,1);
        Parsing.setCellFormat(appContext,fieldCell,fieldLP,getResources().getString(R.string.settings_setting).toUpperCase(),10,R.color.venya_table_title_cell);

        TableRow.LayoutParams valueLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        valueLP.width = 0;
        valueLP.weight = 11;
        valueLP.setMargins(1,1,1,1);
        Parsing.setCellFormat(appContext,valueCell,valueLP,getResources().getString(R.string.settings_value).toUpperCase(),10,R.color.venya_table_title_cell);

        TableRow.LayoutParams optionLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        optionLP.width = 0;
        optionLP.weight = 4;
        optionLP.setMargins(1,1,1,1);
        Parsing.setCellFormat(appContext,optionCell,optionLP,getResources().getString(R.string.settings_option).toUpperCase(),10,R.color.venya_table_title_cell);

        row.addView(fieldCell);
        row.addView(valueCell);
        row.addView(optionCell);

        int rowCount = 0;
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
                    Parsing.setCellFormat(appContext,fieldCell,fieldLP,Parsing.formatMessage(new String [] {getResources().getString(langFieldId)}),10,R.color.venya_table_value_cell);
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
                    Parsing.setCellFormat(appContext,valueCell,valueLP,value,10,R.color.venya_table_value_cell);
                    row.addView(valueCell);

                    if ( Arrays.asList(listFields).contains(field) ) {
                        option = getResources().getString(R.string.settings_listoption);
                    }else if ( Arrays.asList(booleanFields).contains(field) ) {
                        option = getResources().getString(R.string.settings_booleanoption);
                    } else {
                        option = getResources().getString(R.string.settings_stringoption);
                    }
                    Parsing.setCellFormat(appContext,optionCell,optionLP,option.toUpperCase(),10,R.color.venya_table_value_cell);

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
                                    //languageListener.changeLanguageClicked(customer);
                                    DialogFragment fragment = new ChangeLanguageDialog();
                                    Bundle args = fragment.getArguments();
                                    if ( args == null ) { args = new Bundle(); }
                                    args.putParcelable("customer",customer);
                                    args.putInt("currentPosition",Parsing.getIndexOf(menuOptionsTags,"settings"));
                                    fragment.setArguments(args);

                                    fragment.show(getActivity().getSupportFragmentManager(),"change language options menu");
                                }
                            });
                            break;
                        case "phone":
                            optionCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    phoneListener.changePhoneClicked(customer);
                                }
                            });
                            break;
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
