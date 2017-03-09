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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FullCustomerSettings customer;
    String sessionid;

    public HomeFragment(String sessionid, FullCustomerSettings customer) {
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
        TextView fieldCell;
        TextView valueCell;
        int rowCount = 0;

        if ( customer != null ) {
            String [] fields = getResources().getStringArray(R.array.customerFields);
            String [] privateFields = getResources().getStringArray(R.array.privateFields);
            String [] booleanFields = getResources().getStringArray(R.array.booleanFields);

            for ( int i=fields.length-1; i>=0; i-- ) {
                String field = fields[i];
                if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) ) {
                    row = new TableRow(getContext());
                    layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(layoutParams);
                    fieldCell = new TextView(getContext());
                    fieldCell.setText(field);
                    row.addView(fieldCell);

                    CustomerField fullField = customer.getField(field);
                    valueCell = new TextView(getContext());
                    if ( field.equals("address") ) {
                        // iterate through address fields
                        Address address = (Address)fullField.getValue();
                        String addrStr = address.formatAddress();
                        valueCell.setText(addrStr);
                    } else if ( Arrays.asList(booleanFields).contains(field) ) {
                        boolean value = (boolean)fullField.getValue();
                        valueCell.setText(Parsing.getBooleanValue(value));
                    } else {
                        String value = (String)fullField.getValue();
                        valueCell.setText(value);
                    }
                    valueCell.setPadding(5,5,5,5);
                    row.addView(valueCell);
                    tableLayout.addView(row,rowCount);
                }
            }
        }
    }

}
