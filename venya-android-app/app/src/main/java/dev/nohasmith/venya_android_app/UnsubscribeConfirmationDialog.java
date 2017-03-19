package dev.nohasmith.venya_android_app;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;


public class UnsubscribeConfirmationDialog extends DialogFragment {
    private String message;
    private String sessionid;
    private String providerid;
    private String action;
    private Appointment appointment;
    private FullCustomerSettings customer;
    private final String TAG = "UnsubscribeConfirmationDialog";

    public UnsubscribeConfirmationDialog() {
        // Required empty public constructor
    }
/*
    public ConfirmationFragment(String message) {
        this.message = message;
    }
    */

    public interface ConfirmDialogListener {
        //default listener if something went wrong initialising
        void onDialogPositiveClick();
        // listener for provider unsubscription confirmation dialog
        void onDialogPositiveClick(DialogFragment dialog, Context context, String providerid, String sessionid);
        // listener for cancel appointment confirmtion
        void onDialogPositiveClick(DialogFragment dialog, Context context, FullCustomerSettings customer, Appointment appointment);
        //void onDialogPositiveUnsubscribe(DialogFragment dialog, Context context, String providerid, String customerid);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    ConfirmDialogListener listener;

    @Override
    public void onAttach(Context context){
        Log.d(TAG + ".onAttach","getting attached");
        super.onAttach(context);

        if ( context instanceof ConfirmDialogListener ) {
            listener = (ConfirmDialogListener)context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ConfirmDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.confirmation_fragment, container, false);
        String myTAG = TAG + ".onCreate";
        Log.d(myTAG,"getting created");

        Bundle argsBundle = new Bundle();
        argsBundle = getArguments();

        try {
            message = (String)argsBundle.get("message");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get message from savedInstanceState Bundle. Setting to default confirm");
            message = getResources().getString(R.string.form_confirm);
        }

        try {
            sessionid = argsBundle.getString("sessionid");
            providerid = argsBundle.getString("providerid");
            action = argsBundle.getString("action");
            customer = argsBundle.getParcelable("customer");
            appointment = (Appointment)argsBundle.getSerializable("appointment");
        } catch (Exception e) {
            Log.e(myTAG, "Failed to parse required values from bundle");
            e.printStackTrace();
            return null;
        }

        if ( action == null ||
                ( action.equals("unsubscribe") && ( providerid == null || sessionid == null ) ) ||
                ( action.equals("cancelappointment") && ( ! (customer instanceof FullCustomerSettings) || !(appointment instanceof Appointment) ) )
                ) {
            action = "home";
            message = getResources().getString(R.string.errors_gohomeafterfail);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setPositiveButton(R.string.form_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (action) {
                            case "unsubscribe":
                            listener.onDialogPositiveClick(UnsubscribeConfirmationDialog.this, getActivity(), providerid, sessionid);
                                break;
                            case "cancelappointment":
                                listener.onDialogPositiveClick(UnsubscribeConfirmationDialog.this,getActivity(),customer,appointment);
                                break;
                            default:
                                listener.onDialogPositiveClick();
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(UnsubscribeConfirmationDialog.this);
                    }
                });

        return builder.create();
    }
}
