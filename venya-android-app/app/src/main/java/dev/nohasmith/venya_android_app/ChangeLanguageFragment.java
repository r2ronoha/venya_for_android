package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.supportedLanguages;
import static dev.nohasmith.venya_android_app.MainActivity.venyaUrl;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLanguageFragment extends Fragment {
    FullCustomerSettings customer;
    Context appContext;
    TextView [] languageOptions;

    public ChangeLanguageFragment() {
        // Required empty public constructor
    }

    public ChangeLanguageFragment(FullCustomerSettings customer) {
        this.customer = customer;
    }

    interface UpdateLanguageListener {
        void updateLanguageClicked(FullCustomerSettings customer);
    }
    UpdateLanguageListener updateListener;

    interface CancelListener {
        void cancelClicked(FullCustomerSettings customer);
    }
    CancelListener cancelListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof UpdateLanguageListener ) {
            updateListener = (UpdateLanguageListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateLanguageListener@ChangeLanguage) must implement OnFragmentInteractionListenen");
        }

        if ( context instanceof CancelListener ) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (cancelListener@ChangeLanguage) must implement OnFragmentInteractionListenen");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        appContext = view.getContext();

        final TextView errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        TextView cancelView = (TextView)view.findViewById(R.id.cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelListener.cancelClicked(customer);
            }
        });

        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.options_table);
        TableRow row;
        TableRow.LayoutParams layoutParams;
        RadioButton tableButton;
        ImageView langImage;
        int rowCount = 0;

        for (int i=0; i<supportedLanguages.length; i++) {
            // create select menu
            final String language = supportedLanguages[i];
            row = new TableRow(appContext);
            layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            tableButton = new RadioButton(appContext);
            langImage = new ImageView(appContext);
            
            int imageid = Parsing.getResId(appContext,language,"drawable");
            langImage.setImageResource(imageid);

            //tableButton.setOnClickListener(new View.OnClickListener() {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String response = null;

                    String lang = language.toLowerCase();
                    //if (!lang.equals((String) customer.getLanguage().getValue())) {
                        String reqUrl = venyaUrl + "/updateSetting?" +
                                "action=update&type=customer" +
                                "&id=" + (String) customer.getId().getValue() +
                                "&field=language" +
                                "&newvalue=" + lang;
                        MyHttpHandler httpHandler = new MyHttpHandler(appContext);
                        try {
                            response = httpHandler.execute(reqUrl).get();
                        } catch (Exception e) {
                            Log.e("ChangeLanguage.OptionSelected","Failed to update customer with language: " + lang);
                        }

                        if ( response == null ) {
                            Log.e("ChangeLanguage.OptionSelected","NULL response from server to lang: " + lang);
                            Parsing.displayTextView(appContext,errorsView,R.string.errors_nullfromserver);
                        } else {
                            HashMap<String,Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response,appContext);
                            String status = (String)parsedResponse.get("status");
                            Log.d("CHangeLAnguage.OptinSelected","Request status: " + status);
                            if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                                String errormessage = (String)parsedResponse.get("errormessage");
                                Log.d("ChangeLanguage.Optionselecte","Error to update request = " + errormessage);
                                try {
                                    Parsing.displayTextView(appContext,errorsView,"errors_" + errormessage);
                                } catch (Exception e) {
                                    errorsView.setText(errormessage);
                                }
                            } else {
                                CustomerSettings updatedCustomer = (CustomerSettings)parsedResponse.get("customer");
                                String updatedLang = updatedCustomer.getLanguage();
                                customer.setField("language",updatedLang);
                                updateListener.updateLanguageClicked(customer);
                            }
                        }
                    //}
                }
            });
            
            //row.addView(tableButton);
            langImage.setPadding(10,10,10,10);
            row.addView(langImage);
            
            tableLayout.addView(row,rowCount);
            rowCount++;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.change_language_fragment, container, false);
    }

}
