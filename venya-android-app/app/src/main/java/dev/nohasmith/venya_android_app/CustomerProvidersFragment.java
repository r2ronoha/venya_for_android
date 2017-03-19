package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

        TextView title = (TextView)view.findViewById(R.id.providersTitle);
        Parsing.displayTextView(appContext,title,R.string.menu_providers);

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
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

            TableRow.LayoutParams providerLayoutParams = new TableRow.LayoutParams();
            providerLayoutParams.width = 0;
            providerLayoutParams.weight = 2;
            providerLayoutParams.setMargins(1,1,1,1);

            TableRow.LayoutParams detailsLayoutParams = new TableRow.LayoutParams();
            detailsLayoutParams.width = 0;
            detailsLayoutParams.weight = 2;
            detailsLayoutParams.setMargins(1,1,1,1);

            TableRow.LayoutParams optionLayoutParams = new TableRow.LayoutParams();
            optionLayoutParams.width = 0;
            optionLayoutParams.weight = 1;
            optionLayoutParams.setMargins(1,1,1,1);

            TextView nameCell = new TextView(appContext);
            TextView detailsCellTitle = new TextView(appContext);
            TextView optionCell = new TextView(appContext);

            row = new TableRow(appContext);
            row.setLayoutParams(layoutParams);

            int rowCount = 0;
            Parsing.setCellFormat(appContext,nameCell,providerLayoutParams,getResources().getString(R.string.provider_name).toUpperCase(),15,R.color.venya_table_title_cell);
            Parsing.setCellFormat(appContext,detailsCellTitle,detailsLayoutParams,getResources().getString(R.string.form_details).toUpperCase(),15,R.color.venya_table_title_cell);
            Parsing.setCellFormat(appContext,optionCell,optionLayoutParams,getResources().getString(R.string.settings_option).toUpperCase(),15,R.color.venya_table_title_cell);

            row.addView(nameCell);
            row.addView(detailsCellTitle);
            row.addView(optionCell);

            tableLayout.addView(row, rowCount++);

            customerProviders = (HashMap<String, Provider>) customer.getProviders().getValue();
            for (final String providerid : customerProviders.keySet()) {
                Provider provider = customerProviders.get(providerid);
                if (provider.isActive()) {
                    final TableRow providerRow = new TableRow(appContext);
                    providerRow.setLayoutParams(layoutParams);
                    providerRow.setBackgroundColor(appContext.getColor(R.color.venya_table_value_cell));

                    nameCell = new TextView(appContext);
                    TableRow.LayoutParams nameValueLP = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
                    nameValueLP.width = 0;
                    nameValueLP.weight = 5;
                    Parsing.setCellFormat(appContext,nameCell,nameValueLP,Parsing.formatName(provider.getName()),15,R.color.venya_table_value_cell);

                    providerRow.addView(nameCell);
                    providerRow.setPadding(10, 10, 10, 10);
                    final int rowid = View.generateViewId();
                    providerRow.setId(rowid);

                    // create a table with the details of the providernd insert it in the "details" cell
                    TableLayout detailsTable = new TableLayout(appContext);
                    TableLayout.LayoutParams detailsTableLP = new TableLayout.LayoutParams();
                    detailsTableLP.width = 0;
                    detailsTableLP.weight = 5;
                    detailsTable.setBackgroundColor(appContext.getColor(R.color.venya_table_value_cell));
                    int fieldRowCount = 0;
                    for (int i = 0; i < providerFields.length; i++) {
                        String field = providerFields[i];
                        if (!field.equals("name") && !field.equals("id")) {
                            TableRow detailRow = new TableRow(appContext);
                            TableRow.LayoutParams detaileLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                            detaileLayoutParams.setMargins(0,0,0,0);
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

                            TableRow.LayoutParams valueLP = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
                            Parsing.setCellFormat(appContext,valueCell,valueLP,valueStr,5,R.color.venya_table_value_cell);
                            //valueCell.setText(valueStr);
                            //valueCell.setBackgroundColor(appContext.getColor(R.color.venya_table_value_cell));

                            detailRow.addView(valueCell);

                            detailsTable.addView(detailRow, fieldRowCount++);
                        }
                    }

                    providerRow.addView(detailsTable);

                    optionCell = new TextView(appContext);
                    TableRow.LayoutParams optionLP = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT);
                    optionLP.width = 0;
                    optionLP.weight = 2;
                    optionCell.setText(getResources().getString(R.string.deactivate));
                    Parsing.setCellFormat(appContext,optionCell,optionLP,getResources().getString(R.string.deactivate).toUpperCase(),5,R.color.venya_table_value_cell);
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
        DialogFragment dialog = new UnsubscribeConfirmationDialog();

        Bundle dialogBundle = dialog.getArguments();
        if ( dialogBundle == null ) {
            dialogBundle = new Bundle();
        }
        dialogBundle.putString("action","unsubscribe");
        dialogBundle.putString("message",message);
        dialogBundle.putString("providerid",providerid);
        dialogBundle.putString("sessionid",sessionid);
        dialog.setArguments(dialogBundle);

        dialog.show(getActivity().getSupportFragmentManager(), "UnsubscribeConfirmationDialog");

    }
}
