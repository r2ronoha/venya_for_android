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

import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.providerFields;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerProvidersFragment extends Fragment{
    String sessionid;
    FullCustomerSettings customer;
    Context appContext;
    TextView errorsView;
    private final String TAG = "CustomerProvidersFragment";

    public CustomerProvidersFragment() {
        // Required empty public constructor
    }

    public CustomerProvidersFragment(String sessionid, FullCustomerSettings customer) {
        this.sessionid = sessionid;
        this.customer = customer;
    }

    /*
    interface UnsubscribeProviderListener {
        void unsubscribeProviderClicked(FullCustomerSettings customer);
    }
    UnsubscribeProviderListener unsubscribeListener;
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.providers_fragment, container, false);
    }
    /*
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if ( context instanceof UnsubscribeProviderListener ) {
            unsubscribeListener = (UnsubscribeProviderListener)context;
        } else {
            throw new RuntimeException (context.toString() + " (CustomerProvidersListener@home) must implement UnsubscribeProviderListener");
        }
    }
    */
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        appContext = getContext();

        errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        HashMap<String,Provider> customerProviders = (HashMap<String, Provider>) customer.getProviders().getValue();
        int activeProvidersCount = 0;
        for ( String providerid : customerProviders.keySet() ){
            if ( customerProviders.get(providerid).isActive() ) activeProvidersCount++;
        }
        if ( activeProvidersCount == 0 ) {
            Parsing.displayTextView(appContext,errorsView,R.string.errors_notsubscribed);
        } else {


            final TableLayout tableLayout = (TableLayout) view.findViewById(R.id.providers_table);
            TableRow row;
            TableRow.LayoutParams layoutParams;

            TextView nameCell = new TextView(appContext);
            TextView detailsCellTitle = new TextView(appContext);
            TextView optionCell = new TextView(appContext);

            row = new TableRow(appContext);
            layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            int rowCount = 0;
            nameCell.setText(getResources().getString(R.string.provider_name).toUpperCase());
            detailsCellTitle.setText(getResources().getString(R.string.form_details).toUpperCase());
            optionCell.setText(getResources().getString(R.string.settings_option).toUpperCase());

            nameCell.setPadding(10, 10, 10, 10);
            detailsCellTitle.setPadding(10, 10, 10, 10);
            optionCell.setPadding(10, 10, 10, 10);

            row.addView(nameCell);
            row.addView(detailsCellTitle);
            row.addView(optionCell);

            tableLayout.addView(row, rowCount++);

            customerProviders = (HashMap<String, Provider>) customer.getProviders().getValue();
            for (final String providerid : customerProviders.keySet()) {
                Provider provider = customerProviders.get(providerid);
                if (provider.isActive()) {
                    final TableRow providerRow = new TableRow(appContext);

                    nameCell = new TextView(appContext);
                    nameCell.setText(Parsing.formatName(provider.getName()));
                    nameCell.setPadding(10, 10, 10, 10);

                    providerRow.addView(nameCell);
                    providerRow.setPadding(10, 10, 10, 10);
                    final int rowid = View.generateViewId();
                    providerRow.setId(rowid);

                    // create a table with the details of the provider and insert it in the "details" cell
                    TableLayout detailsTable = new TableLayout(appContext);
                    int fieldRowCount = 0;
                    for (int i = 0; i < providerFields.length; i++) {
                        String field = providerFields[i];
                        if (!field.equals("name") && !field.equals("id")) {
                            TableRow detailRow = new TableRow(appContext);
                            TableRow.LayoutParams detaileLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                            detailRow.setLayoutParams(detaileLayoutParams);

                        /*
                        Do not show field name in small screen
                         */
                        /*
                        TextView fieldNameCell = new TextView(appContext);
                        fieldNameCell.setText(field);
                        fieldNameCell.setPadding(5,5,5,5);
                        detailRow.addView(fieldNameCell);
                        */
                            TextView valueCell = new TextView(appContext);
                            Object fieldValue = provider.getField(field);
                            String valueStr = (field.equals("address")) ? ((Address) fieldValue).formatAddress() : (String) fieldValue;
                            valueStr = (valueStr.equals("") || valueStr.toLowerCase().equals("n/a")) ? field + " N/A" : valueStr;

                            valueCell.setText(valueStr);
                            valueCell.setPadding(5, 5, 5, 5);
                            detailRow.addView(valueCell);

                            detailsTable.addView(detailRow, fieldRowCount++);
                        }
                    }
                    providerRow.addView(detailsTable);

                    optionCell = new TextView(appContext);
                    optionCell.setText(getResources().getString(R.string.deactivate));
                    optionCell.setPadding(10, 10, 10, 10);
                    optionCell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showConfirmationDialog(getResources().getString(R.string.form_confirm_delete_provider), providerid, sessionid);
                        }
                    });
                    providerRow.addView(optionCell);

                    tableLayout.addView(providerRow, rowCount++);
                }
            }
        }

    }

    public void showConfirmationDialog(String message, String providerid, String sessionid) {
        //String myTAG = TAG + ".showConfDialog";
        DialogFragment dialog = new UnsubscribeConfirmationFragment();

        Bundle dialogBundle = dialog.getArguments();
        if ( dialogBundle == null ) {
            dialogBundle = new Bundle();
        }
        dialogBundle.putString("message",message);
        dialogBundle.putString("providerid",providerid);
        dialogBundle.putString("sessionid",sessionid);
        dialog.setArguments(dialogBundle);

        dialog.show(getActivity().getSupportFragmentManager(), "UnsubscribeConfirmationFragment");

    }
}
