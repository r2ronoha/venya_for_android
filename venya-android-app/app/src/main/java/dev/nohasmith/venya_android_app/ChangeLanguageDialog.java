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

import static dev.nohasmith.venya_android_app.MainActivity.menuOptionsTags;
import static dev.nohasmith.venya_android_app.MainActivity.supportedLanguages;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLanguageDialog extends DialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    Context appContext;
    FullCustomerSettings customer;
    private int currentPosition;
    private int newLang;

    public ChangeLanguageDialog() {
        // Required empty public constructor
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }
    */
    interface UpdateLanguageListener {
        void updateLanguageClicked(FullCustomerSettings customer, int choice, int currentPosition);
    }
    UpdateLanguageListener updateListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof UpdateLanguageListener) {
            updateListener = (UpdateLanguageListener)context;
        } else {
            throw new RuntimeException(context.toString() + " (UpdateLanguageListener@ChangeLanguage) must implement OnFragmentInteractionListenen");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        appContext = getContext();
        String myTAG = TAG + ".onCreateDialog";
        final View view = getView();

        Bundle args = getArguments();
        try {
            currentPosition = args.getInt("currentPosition");
        } catch (Exception e) {
            Log.w(TAG,"Could not get current position from arguments. Setting to 'settings'");
            currentPosition = Parsing.getIndexOf(menuOptionsTags,"settings");
        }

        try {
            customer = args.getParcelable("customer");
        } catch (Exception e) {
            Log.e(TAG,"Could not get customer from arguments");
            e.printStackTrace();
        }

        if ( customer instanceof FullCustomerSettings ) {
            TextView title = new TextView(appContext);
            Parsing.displayTextView(appContext,title,R.string.menu_select_language);

            String myLang = (String)customer.getLanguage().getValue();
            final int option = ( Parsing.getIndexOf(supportedLanguages,myLang) >= 0 ) ? Parsing.getIndexOf(supportedLanguages,myLang) : -1;
            //final String newLang;

            return new AlertDialog.Builder(appContext)
                    .setCustomTitle(title)
                    .setView(view)
                    .setSingleChoiceItems(supportedLanguages, option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newLang = which;
                        }
                    })
                    .setPositiveButton(R.string.form_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateListener.updateLanguageClicked(customer,newLang,currentPosition);
                        }
                    })
                    .setNegativeButton(R.string.form_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        } else {
            return null;
        }
    }

}
