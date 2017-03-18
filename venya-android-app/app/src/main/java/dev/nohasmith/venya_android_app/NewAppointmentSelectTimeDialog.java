package dev.nohasmith.venya_android_app;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by arturo on 17/03/2017.
 */

public class NewAppointmentSelectTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    FullCustomerSettings customer;
    Appointment appointment;
    Calendar calendar;
    int year = -1;
    int month = -1;
    int day = -1;
    final String TAG = this.getClass().getSimpleName();
    Context appContext;

    interface NewAppointmentListener {
        void newAppointmentClicked(FullCustomerSettings customer, Appointment appointment);
    }
    NewAppointmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof NewAppointmentListener ) {
            listener = (NewAppointmentListener)context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NewAppointmentListener");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);

        long date = calendar.getTimeInMillis();

        appointment.setDate(date);

        listener.newAppointmentClicked(customer,appointment);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        appContext = getContext();
        String myTAG = TAG + ".onCreateDialog";

        try {
            Bundle args = getArguments();
            customer = args.getParcelable("customer");
            appointment = (Appointment)args.getSerializable("appointment");
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get arguments from Bundle");
            e.printStackTrace();
        }

        if ( customer instanceof FullCustomerSettings && appointment instanceof Appointment && year >=0 && month >= 0 && day >= 0 ) {
            calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            TimePickerDialog timeDialog = new TimePickerDialog(appContext, this, hour, min, true);

            TextView title = new TextView(appContext);
            Parsing.displayTextView(appContext, title, R.string.menu_settime);
            title.setPadding(15, 15, 15, 15);
            timeDialog.setCustomTitle(title);

            return timeDialog;
        } else {
            return null;
        }
    }
}
