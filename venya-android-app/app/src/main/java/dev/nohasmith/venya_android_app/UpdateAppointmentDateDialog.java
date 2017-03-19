package dev.nohasmith.venya_android_app;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateAppointmentDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    FullCustomerSettings customer;
    Appointment appointment;
    Calendar calendar;
    DatePicker datePicker;

    final String TAG = this.getClass().getSimpleName();

    public UpdateAppointmentDateDialog() {
        // Required empty public constructor
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DialogFragment dialog = new UpdateAppointmentTimeDialog();

        Bundle args = dialog.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer", customer);
        args.putSerializable("appointment",appointment);
        args.putInt("year",year);
        args.putInt("month",month);
        args.putInt("day",dayOfMonth);
        dialog.setArguments(args);

        dialog.show(getActivity().getSupportFragmentManager(),"UpdateAppointmentTimeDialog");
    }

    /*
    interface ConfirmUpdateDateListener {
        void confirmNewDateClicked();
    }
    ConfirmUpdateDateListener updateDateListener;

*/
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( ! (context instanceof UpdateAppointmentTimeDialog.ConfirmUpdateListener) ) {
            throw new RuntimeException(context.toString() + " must implement ConfirmUpdateListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String myTAG = TAG + ".onCreateDialog";

        View view = getView();
        Context appContext = getContext();

        try {
            Bundle args = getArguments();
            customer = args.getParcelable("customer");
            appointment = (Appointment)args.getSerializable("appointment");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get arguments from Bundle");
            e.printStackTrace();
        }

        if ( customer != null && appointment != null ) {
            /*
            Set DatePicker date to appointment date
            1. set the calendar date to the date in the appointent
            2. get the foramted date from the updated calendar
            3. set datePicker date
             */
            long date = appointment.getDate();

            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(appContext,this,year,month,day);
            //Disaable past dates
            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

            TextView title = new TextView(appContext);
            Parsing.displayTextView(appContext,title,R.string.setnewdate);
            title.setPadding(15,15,15,15);
            datePickerDialog.setCustomTitle(title);

            return datePickerDialog;
        } else {
            Log.e(myTAG,"NULL parameters from Bundle");
            return null;
        }
    }

}
