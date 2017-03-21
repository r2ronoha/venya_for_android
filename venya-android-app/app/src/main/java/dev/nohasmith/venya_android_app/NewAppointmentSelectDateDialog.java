package dev.nohasmith.venya_android_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by arturo on 17/03/2017.
 */

public class NewAppointmentSelectDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    FullCustomerSettings customer;
    Appointment appointment;
    String providerid;
    Calendar calendar;
    final String TAG = this.getClass().getSimpleName();
    Context appContext;
    Bundle savedInstanceState;
    private long newAppointmentDate = 0;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Bundle args = new Bundle();
        args.putParcelable("customer", customer);
        args.putSerializable("appointment", appointment);
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", dayOfMonth);

        DialogFragment timeDialog = new NewAppointmentSelectTimeDialog();
        timeDialog.setArguments(args);
        timeDialog.show(getActivity().getSupportFragmentManager(), "NewAppointmentSelectTimeDialog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        appContext = getContext();
        String myTAG = TAG + ".onCreateDialog";

        try {
            Bundle args = getArguments();
            customer = args.getParcelable("customer");
            appointment = (Appointment)args.getSerializable("appointment");
            newAppointmentDate = args.getLong("newdate");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get arguments from Bundle");
            e.printStackTrace();
        }

        if ( customer instanceof FullCustomerSettings && appointment instanceof Appointment ) {

            calendar = Calendar.getInstance();
            if ( newAppointmentDate != 0 ) {
                calendar.setTime(new Date(newAppointmentDate));
            } else {
                calendar.setTime(new Date());
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dateDialog = new DatePickerDialog(appContext, this, year, month, day);
            // disable past dates
            dateDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

            TextView title = new TextView(appContext);
            Parsing.displayTextView(appContext,title,R.string.setnewdate);
            title.setPadding(15,15,15,15);
            dateDialog.setCustomTitle(title);

            return dateDialog;
        } else {
            return null;
        }

    }
}
