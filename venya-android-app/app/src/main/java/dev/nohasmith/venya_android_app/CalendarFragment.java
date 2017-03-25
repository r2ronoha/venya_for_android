package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    Context appContext;
    FullCustomerSettings customer;
    private final String TAG = this.getClass().getSimpleName();
    SimpleDateFormat dfNoTime = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
    ColorDrawable hasAppointmentColor;
    ColorDrawable selectedDateColor;
    private long newAppointmentDate;
    private Date selectedDate;
    private int countActive;
    private String [] activeProviders;

    public CalendarFragment() {
        // Required empty public constructor
    }

    interface ViewAppointmentListener {
        void appointmentClicked(FullCustomerSettings customer, String [] appointmentsList, long date);
    }
    ViewAppointmentListener viewListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_monthview_fragment,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String myTAG = TAG + ".onAttach";

        if ( context instanceof ViewAppointmentListener ) {
            viewListener = (ViewAppointmentListener)context;
        } else {
            throw new RuntimeException(myTAG + " " + context.toString() + " must implement ViewAppointmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        appContext = getContext();
        final String myTAG = TAG + ".onStart";
        View view = getView();

        hasAppointmentColor =  new ColorDrawable(appContext.getColor(R.color.venya_appointment_date));
        selectedDateColor = new ColorDrawable(appContext.getColor(R.color.colorPrimary));

        try {
            Bundle args = getArguments();
            customer = args.getParcelable("customer");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to get customer from arguments");
        }

        if ( customer instanceof FullCustomerSettings ) {
            // get the active providers. This will be used to evaluate whether to allow new dates and dispaly existing dates
            HashMap<String,Provider> providers = (HashMap<String,Provider>)customer.getProviders().getValue();
            countActive = 0;
            Set<String> providerids = providers.keySet();
            for ( String providerid : providerids ) {
                if ( providers.get(providerid).isActive() ) countActive++;
            }

            activeProviders = new String[countActive];
            int i = 0;
            for (String providerid : providerids) {
                //Log.d(myTAG, "providerid = " + providerid);
                if (providers.get(providerid).isActive()) {
                    //Log.d(myTAG, providerid + " is active. i = " + i);
                    activeProviders[i] = providers.get(providerid).getId();
                    i++;
                }
            }

            final Button newAppButton = (Button)view.findViewById(R.id.newappointment);
            newAppButton.setVisibility(View.INVISIBLE);
            newAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newAppDialog = new NewAppointmentSelectProviderDialog();
                    Bundle args = newAppDialog.getArguments();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putParcelable("customer", customer);
                    args.putStringArray("activeproviders", activeProviders);
                    args.putLong("newdate", newAppointmentDate);
                    newAppDialog.setArguments(args);

                    newAppDialog.show(getActivity().getSupportFragmentManager(), "NewAppointmentSelectProvider");

                }
            });

            final CaldroidFragment caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar calendar = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            final HashMap<String, HashMap<String, Appointment>> dateToAppointment = new HashMap<String, HashMap<String, Appointment>>();

            if ( countActive > 0 ) {
                HashMap<String, Appointment> appointments = (HashMap<String, Appointment>) customer.getAppointments().getValue();
                HashMap<String, Appointment> appointmentsRef;

                for (String appointmentid : appointments.keySet()) {
                    long dateLong = appointments.get(appointmentid).getDate();
                    Date date = new Date(dateLong);
                    String dateStr = df.format(date);
                    // highlight days with appointment
                    caldroidFragment.setBackgroundDrawableForDate(hasAppointmentColor, date);
                    caldroidFragment.refreshView();

                    // create referenc to date without time, as CAldroid successListener does not keep time of appointment
                    String dateNoTimeStr = dfNoTime.format(date);

                    if (dateToAppointment.containsKey(dateNoTimeStr)) {
                        // if here is already an appointment registered for the given day get the map for that day
                        //Log.d(myTAG, "Appointment found for " + dateNoTimeStr);
                        appointmentsRef = dateToAppointment.get(dateNoTimeStr);
                    } else {
                        // if no appointments for that day, initialise the map
                        //Log.d(myTAG, "NO Appointment on " + dateNoTimeStr);
                        appointmentsRef = new HashMap<String, Appointment>();
                    }
                    // add the new appointment
                    //Log.d(myTAG, "Appointment added to " + dateNoTimeStr);
                    appointmentsRef.put(dateStr, appointments.get(appointmentid));
                    // insert/update the map
                    dateToAppointment.put(dateNoTimeStr, appointmentsRef);

                    //Log.d(myTAG,"date " + date + " (" + dateStr + ") added to the appointments dates");
                }
            }

            CaldroidListener caldroidListener = new CaldroidListener() {
                @Override
                public void onSelectDate(Date date, View view) {
                    String selectedDateStr = dfNoTime.format(date);
                    //Log.d(myTAG,"onSetelctDate called. Date = " + selectedDateStr);

                    if ( dateToAppointment.containsKey(selectedDateStr) ) {
                        HashMap<String,Appointment> dayAppointments = dateToAppointment.get(selectedDateStr);
                        String [] appointmentsList = new String[dayAppointments.size()];
                        int i = 0;
                        for ( String dayAppDate : dayAppointments.keySet() ) {
                            appointmentsList[i] = dayAppointments.get(dayAppDate).getId();
                            i++;
                        }
                        viewListener.appointmentClicked(customer,appointmentsList,date.getTime()); // call appt details fragment for this appointment
                    } else {
                        try {
                            caldroidFragment.clearBackgroundDrawableForDate(selectedDate);
                        } catch (Exception e) {
                            Log.w(myTAG,"Could not reset background of previous selected date");
                            e.printStackTrace();
                        }
                        caldroidFragment.setBackgroundDrawableForDate(selectedDateColor,date);
                        caldroidFragment.refreshView();
                        selectedDate = date;

                        // display button to create appointment
                        if ( countActive > 0 ) {
                            newAppButton.setVisibility(View.VISIBLE);
                            newAppointmentDate = date.getTime();
                        }
                    }
                }
            };
            caldroidFragment.setCaldroidListener(caldroidListener);

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.calendar_view, caldroidFragment);
            ft.commit();
        }
    }

}
