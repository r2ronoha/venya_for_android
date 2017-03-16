package dev.nohasmith.venya_android_app;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ConfirmationFragment extends DialogFragment {
    private String message;

    public ConfirmationFragment() {
        // Required empty public constructor
    }

    public ConfirmationFragment(String message) {
        this.message = message;
    }

    public interface ConfirmDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    ConfirmDialogListener listener;

    @Override
    public void onAttach(Context context){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("confirm")
                .setPositiveButton(R.string.form_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(ConfirmationFragment.this);
                    }
                })
                .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(ConfirmationFragment.this);
                    }
                });

        return builder.create();
    }
}
