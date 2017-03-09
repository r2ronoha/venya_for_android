package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

import static dev.nohasmith.venya_android_app.MainActivity.booleanFields;
import static dev.nohasmith.venya_android_app.MainActivity.customerFields;
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

    //public SettingsFragment() {}

    public SettingsFragment(String sessionid, FullCustomerSettings customer) {
        this.customer = customer;
        this.sessionid = sessionid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TextView textView = new TextView(getActivity());
        //textView.setText(R.string.hello_blank_fragment);
        //customer = (CustomerSettings)savedInstanceState.getParcelable("customer");
        //sessionid = (String)savedInstanceState.get("sessionid");
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

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
                String field = customerFields[i];
                //if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) ) {
                if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) && customer.getField(field).getFix() == 0 ) {
                        row = new TableRow(getContext());
                    layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(layoutParams);

                    fieldCell = new TextView(getContext());
                    fieldCell.setText(Parsing.formatMessage(new String [] {field}));
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
                        boolean boolValue = (boolean)customer.getField(field).getValue();
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
                    row.addView(optionCell);

                    tableLayout.addView(row,rowCount++);
                }
            }
        }
    }

}
