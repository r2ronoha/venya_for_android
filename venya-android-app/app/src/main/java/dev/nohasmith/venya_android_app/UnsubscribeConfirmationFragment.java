package dev.nohasmith.venya_android_app;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;


public class UnsubscribeConfirmationFragment extends DialogFragment {
    private String message;
    private String sessionid;
    private String providerid;
    private final String TAG = "UnsubscribeConfirmationFragment";

    public UnsubscribeConfirmationFragment() {
        // Required empty public constructor
    }
/*
    public ConfirmationFragment(String message) {
        this.message = message;
    }
    */

    public interface ConfirmDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Context context, String providerid, String sessionid);
        //public void onDialogPositiveUnsubscribe(DialogFragment dialog, Context context, String providerid, String customerid);
        public void onDialogNegativeClick(DialogFragment dialog);
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
            sessionid = (String)argsBundle.get("sessionid");
            providerid = (String)argsBundle.get("providerid");
        } catch (Exception e) {
            Log.e(myTAG, "Failed to parse required values from bundle");
            e.printStackTrace();
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setPositiveButton(R.string.form_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(UnsubscribeConfirmationFragment.this,getActivity(),providerid,sessionid);
                    }
                })
                .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(UnsubscribeConfirmationFragment.this);
                    }
                });

        return builder.create();
    }
}
