package dev.nohasmith.venya_android_app;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateAppointmentTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    String TAG = this.getClass().getSimpleName();
    FullCustomerSettings customer;
    Appointment appointment;
    String appointmentId;
    int year = -1;
    int month = -1;
    int day = -1;

    private TimePicker timePicker;
    private DatePicker datePicker;
    private Calendar calendar;
    private Bundle savedInstanceState;

    public UpdateAppointmentTimeDialog() {
        // Required empty public constructor
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        long newDate = calendar.getTimeInMillis();
        appointment.setDate(newDate);

        if ( calendar.before(Calendar.getInstance()) ) {
            savedInstanceState.putString("errormessage",getResources().getString(R.string.errors_invalidtime));
            this.onCreateDialog(savedInstanceState).show();
        } else {
            updateListener.confirmNewDatePositiveClick(UpdateAppointmentTimeDialog.this, customer, appointment);
        }
    }

    interface ConfirmUpdateListener {
        void confirmNewDatePositiveClick(DialogFragment fragment, FullCustomerSettings customer, Appointment appointment);
        void confirmNewDateNegativeClick(DialogFragment fragment, FullCustomerSettings customer);
    }
    ConfirmUpdateListener updateListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof ConfirmUpdateListener ) {
            updateListener = (ConfirmUpdateListener)context;
        } else {
            throw new RuntimeException(TAG + ".onAttach - " + context.toString() + " must immplement ConfirmUpdateListener");
        }
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_appointment_fragment, container, false);
    }
    */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        String myTAG = TAG + ".onCreateDialog";

        View view = getView();
        final Context appContext = getContext();

        // get appointment info form Bundle
        try {
            Bundle args = getArguments();
            customer = args.getParcelable("customer");
            appointment = (Appointment)args.getSerializable("appointment");
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get appointment details from Bundle");
            e.printStackTrace();
        }

        if ( appointment != null && customer != null && year >=0 || month >= 0 || day >= 0 ) {
            Log.d(myTAG,"getting date from appointment");
            long appDate = appointment.getDate();
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(appDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            Log.d(myTAG,"initializing picker");
            TimePickerDialog dialog = new TimePickerDialog(appContext,this,hour,min,true);

            TextView dialogTitle = new TextView(appContext);
            Parsing.displayTextView(appContext,dialogTitle,R.string.menu_settime);

            try {
                String errormessage = savedInstanceState.getString("errormessage");
                Parsing.displayTextView(appContext,dialogTitle,"errors_" + errormessage);
            } catch (Exception e) {}

            dialogTitle.setGravity(Gravity.CENTER_HORIZONTAL);

            dialog.setCustomTitle(dialogTitle);

            return dialog;
        } else {
            Log.e(myTAG,"Empty Bundle arguments");
            return null;
        }
    }

}
