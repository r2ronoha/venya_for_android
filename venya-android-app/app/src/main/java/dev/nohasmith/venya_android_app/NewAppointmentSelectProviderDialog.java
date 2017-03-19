package dev.nohasmith.venya_android_app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewAppointmentSelectProviderDialog extends DialogFragment{
    FullCustomerSettings customer;
    Appointment appointment;
    String providerid;
    String [] activeProviders;
    String TAG = this.getClass().getSimpleName();
    Context appContext;

    public NewAppointmentSelectProviderDialog() {
        // Required empty public constructor
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_appointment_fragment, container,false);
    }
    */
    /*
    interface CancelListener {
        void onCancelNewAppointmentClicked(FullCustomerSettings customer);
    }
    CancelListener cancelListener;
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if ( context instanceof CancelListener ) {
            cancelListener = (CancelListener)context;
        } else {
            throw new RuntimeException(TAG + " " + context.toString() + " must implement CancelListener");
        }
        */
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String myTAG = TAG + ".onCreateDialog";

        final View view = getView();
        appContext = getContext();
        Bundle args = getArguments();
        try {
            customer = args.getParcelable("customer");
            activeProviders = args.getStringArray("activeproviders");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get arguments from Bundle");
            e.printStackTrace();
        }

        if ( customer instanceof FullCustomerSettings && activeProviders.length > 0 ) {
            HashMap<String, Provider> provList = (HashMap<String, Provider>) customer.getProviders().getValue();
            final HashMap<String, String> activeProvPairs = new HashMap<String, String>();
            final String[] activeProvNames = new String[activeProviders.length];
            //Log.d(myTAG, "number of active providers received = " + activeProviders.length);
            for (int i = 0; i < activeProviders.length; i++) {
                Log.d(myTAG, "i = " + i);
                String myproviderid = activeProviders[i];
                Log.d(myTAG, "providerid = " + myproviderid);
                Provider provider = provList.get(myproviderid);
                Log.d(myTAG, "provider name = " + provider.getName());
                activeProvPairs.put(provider.getName(), myproviderid);
                activeProvNames[i] = provider.getName();
            }

            TextView title = new TextView(appContext);
            Parsing.displayTextView(appContext, title, R.string.menu_selectprovider);
            final int option = (activeProviders.length == 1) ? 0 : -1;
            //int option = -1;
            //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appContext)
            return new AlertDialog.Builder(appContext)
                    .setCustomTitle(title)
                    .setView(view)
                    .setSingleChoiceItems(activeProvNames, option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            providerid = activeProvPairs.get(activeProvNames[which]);
                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.form_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ( option == 0 && providerid == null ) {
                                providerid = activeProvPairs.get(activeProvNames[0]);
                            }
                            goToDatePicker(customer, providerid);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.form_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //cancelListener.onCancelNewAppointmentClicked(customer);
                        }
                    })
                    .show();
            //return dialogBuilder.create();
            //}

        } else {
            Log.e(myTAG,"Bad customer argument format");
            return null;
        }
    }

    public void goToDatePicker(FullCustomerSettings customer, String providerid) {
        long date = new Date().getTime();
        appointment = new Appointment((String)customer.getId().getValue(),providerid,date);

        Bundle args = new Bundle();
        args.putParcelable("customer",customer);
        args.putSerializable("appointment",appointment);

        DialogFragment dateDialog = new NewAppointmentSelectDateDialog();
        dateDialog.setArguments(args);

        dateDialog.show(getActivity().getSupportFragmentManager(),"NewAppointmentSelectDialog");
    }
}
