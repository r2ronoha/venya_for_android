package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import static dev.nohasmith.venya_android_app.MainActivity.appointmentActiveStatus;
import static dev.nohasmith.venya_android_app.Parsing.displayTextView;
import static dev.nohasmith.venya_android_app.Parsing.formatName;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {
    final String TAG = "AppointmentsFragment";
    FullCustomerSettings customer;
    String sessionid;
    String [] appointmensList;
    private int countActive;
    private String [] activeProviders;
    private int numberofappointments = 0;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    interface AppointmentListener {
        void appointmentClicked(FullCustomerSettings customer, String [] appointmentsList, long date);
        // String[] and date added for support of limited number of appointments displayed if a specific date is selected om the calendar
    }
    AppointmentListener listener;

    interface NewAppointmentListener {
        void newAppointmentClicked(FullCustomerSettings customer);
    }
    NewAppointmentListener newListener;

    interface GoToCalendarListener {
        void goToCalendarClicked(FullCustomerSettings customer);
    }
    GoToCalendarListener calendarListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.appointments_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if ( context instanceof AppointmentListener ) {
            listener = (AppointmentListener)context;
        } else {
            Log.e(TAG + ".onAttach",context.toString() + " must implement AppointmentListener");
        }

        if ( context instanceof NewAppointmentListener ) {
            newListener = (NewAppointmentListener)context;
        } else {
            Log.e(TAG + ".onAttach",context.toString() + " must implement NewAppointmentListener");
        }

        if ( context instanceof GoToCalendarListener ) {
            calendarListener = (GoToCalendarListener)context;
        } else {
            Log.e(TAG + ".onAttach",context.toString() + " must implement GoToCalendarListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final String myTAG = TAG + ".onStart";

        View view = getView();
        final Context appContext = getContext();

        TextView viewTitle = (TextView)view.findViewById(R.id.tableFragmentTitle);
        displayTextView(appContext,viewTitle,R.string.menu_appointments);

        final TextView errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        Button calendarButton = (Button)view.findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarListener.goToCalendarClicked(customer);
            }
        });

        String errormessage = "";
        try {
            Bundle arguments = getArguments();
            this.customer = arguments.getParcelable("customer");
            errormessage = arguments.getString("errormessage");
            appointmensList = arguments.getStringArray("appointmentsList"); // will contain the list of apppointments to display if not all required. i.e after selecting a specific date oin calendar
            String title = arguments.getString("title"); // specific header to display. i.e to idicate those are the appointments for the date selected in the calendar
            numberofappointments = arguments.getInt("numberofappointments"); // in home page we limit the appointments diplayed to a max
            Parsing.displayTextView(appContext,viewTitle, Parsing.formatMessage(new String[] {title}));
        } catch (Exception e) {
            Log.e(myTAG,"Failed to extract customer details from bundle");
        }

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

        Button createButton = (Button) view.findViewById(R.id.createButtom);
        if ( countActive > 0 ) {
            displayTextView(appContext, createButton, R.string.form_newappointment);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newAppDialog = new NewAppointmentSelectProviderDialog();
                    Bundle args = newAppDialog.getArguments();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putParcelable("customer", customer);
                    args.putStringArray("activeproviders", activeProviders);
                    newAppDialog.setArguments(args);

                    newAppDialog.show(getActivity().getSupportFragmentManager(), "NewAppointmentSelectProvider");
                }
            });
        } else {
            Parsing.displayTextView(appContext,viewTitle,R.string.noappointments);
            createButton.setVisibility(View.INVISIBLE);
        }

        if ( errormessage != null && ! errormessage.equals("") ) {
            Parsing.displayTextView(appContext,errorsView,"errors_" + errormessage);
        } else if ( customer instanceof FullCustomerSettings ) {
            HashMap<String,Appointment> appointments = (HashMap<String,Appointment>)customer.getAppointments().getValue();
            //Log.d(myTAG,"total number of appointments: " + appointments.size());

            if ( appointmensList == null ) {
                //Log.d(myTAG,"No specific list of appointments provided. Getting full list of appointments");
                // if no specific list provided, just set it to all customer appointments
                appointmensList = new String[appointments.size()];
                int a = 0;
                for ( String appid : appointments.keySet() ) {
                    appointmensList[a] = appid;
                    //Log.d(myTAG,"appid " + appid + " added to appointmentsList[" + a + "]");
                    a++;
                }
                //Log.d(myTAG,a + " appointments to process");
            }

            //Log.d(myTAG,"Total number of dates to sort: " + appointmensList.length);
            //long [] dates = new long[appointments.keySet().size()];
            long [] dates = new long[appointmensList.length];
            //for ( String key : appointments.keySet() ) {
            for ( int c=0; c<appointmensList.length; c++ ) {
                dates[c] = appointments.get(appointmensList[c]).getDate();
                //Log.d(myTAG,appointments.get(appointmensList[c]).getDate() + " added to dates[" + c + "]");
            }
            Arrays.sort(dates);

            TableLayout appointmentsTable = (TableLayout)view.findViewById(R.id.my_table);
            //appointmentsTable.setBackgroundColor(appContext.getColor(R.color.black));
            TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
            appointmentsTable.setBackgroundColor(appContext.getColor(R.color.colorPrimaryDark));

            TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

            TableRow.LayoutParams providerLayoutParams = new TableRow.LayoutParams();
            providerLayoutParams.width = 0;
            providerLayoutParams.weight = 3;
            providerLayoutParams.setMargins(1,1,1,1);

            TableRow.LayoutParams dateLayoutParams = new TableRow.LayoutParams();
            dateLayoutParams.width = 0;
            dateLayoutParams.weight = 3;
            dateLayoutParams.setMargins(1,1,1,1);
            dateLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            TableRow.LayoutParams timeLayoutParams = new TableRow.LayoutParams();
            timeLayoutParams.width = 0;
            timeLayoutParams.weight = 3;
            timeLayoutParams.setMargins(1,1,1,1);
            timeLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            TableRow.LayoutParams statusLayoutParams = new TableRow.LayoutParams();
            statusLayoutParams.width = 0;
            statusLayoutParams.weight = 1;
            statusLayoutParams.setMargins(1,1,1,1);
            statusLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            int rowCount = 0;

            TableRow row = new TableRow(appContext);
            row.setLayoutParams(rowLayoutParams);

            TextView providerCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,providerCell,providerLayoutParams,formatName(getResources().getString(R.string.appointment_provider).toUpperCase()),15,R.color.venya_table_title_cell);
            row.addView(providerCell);

            TextView dateCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,dateCell,dateLayoutParams,getResources().getString(R.string.menu_date).toUpperCase(),15,R.color.venya_table_title_cell);
            dateCell.setGravity(Gravity.CENTER);
            row.addView(dateCell);

            TextView timeCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,timeCell,timeLayoutParams,getResources().getString(R.string.menu_time).toUpperCase(),15,R.color.venya_table_title_cell);
            timeCell.setGravity(Gravity.CENTER);
            row.addView(timeCell);

            TextView statusCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,statusCell,statusLayoutParams,getResources().getString(R.string.menu_status_short).toUpperCase(),15,R.color.venya_table_title_cell);
            statusCell.setGravity(Gravity.CENTER);
            row.addView(statusCell);

            appointmentsTable.addView(row,rowCount++);

            //Log.d(myTAG,"Processing appointments following sorted date. Total number of appointments: " + dates.length);

            // Check if there is a limit in the number of appointment we want to display
            int appointmetsToShow = ( numberofappointments == 0 ) ? dates.length : numberofappointments;
            for ( int d=0; d < appointmetsToShow; d++ ) {
                //Log.d(myTAG,"current date = " + dates[d]);
                String appointmentid = null;

                for ( int a=0; a<appointmensList.length; a++) {
                    if ( dates[d] == appointments.get(appointmensList[a]).getDate() ) {
                        //Log.d(myTAG,"Found appoitment. ID: " + appointmensList[a]);
                        appointmentid = appointmensList[a];
                    }
                }

                if ( appointmentid != null ) {
                    //Log.d(myTAG, "id " + appointmentid);
                    Appointment appointment = appointments.get(appointmentid);

                    String providerid = appointment.getProviderid();
                    Provider provider = ((HashMap<String, Provider>) customer.getProviders().getValue()).get(providerid);

                    if (provider.isActive()) { // Only display appointment for providers tht the customer is currently subcribed to

                        row = new TableRow(appContext);
                        row.setLayoutParams(rowLayoutParams);

                        String name = provider.getName();
                        providerCell = new TextView(appContext);
                        Parsing.setCellFormat(appContext, providerCell, providerLayoutParams, formatName(name), 15, R.color.venya_table_title_cell);
                        row.addView(providerCell);

                        long dateLong = appointment.getDate();
                        Date date = new Date(dateLong);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String dateStr = dateFormat.format(date);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String timeStr = timeFormat.format(date);

                        dateCell = new TextView(appContext);
                        Parsing.setCellFormat(appContext, dateCell, dateLayoutParams, dateStr, 15, R.color.venya_table_value_cell);
                        dateCell.setGravity(Gravity.CENTER);
                        row.addView(dateCell);

                        timeCell = new TextView(appContext);
                        Parsing.setCellFormat(appContext, timeCell, timeLayoutParams, timeStr, 15, R.color.venya_table_value_cell);
                        timeCell.setGravity(Gravity.CENTER);
                        row.addView(timeCell);

                        String status = appointment.getStatus().substring(0, 2);
                        statusCell = new TextView(appContext);
                        Parsing.setCellFormat(appContext, statusCell, statusLayoutParams, status, 15, R.color.venya_table_value_cell);
                        statusCell.setGravity(Gravity.CENTER);
                        row.addView(statusCell);

                        if (Parsing.getIndexOf(appointmentActiveStatus, appointment.getStatus()) < 0) {
                            row.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));

                            providerCell.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));
                            providerCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));

                            dateCell.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));
                            dateCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));

                            timeCell.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));
                            timeCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));

                            statusCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));
                            statusCell.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));
                        }

                /*
                Button displayButton = new Button(appContext);
                displayButton.setText(getResources().getString(R.string.appointment_display).toUpperCase());
                displayButton.setPadding(10,10,10,10);
                */

                        final String myAppId = appointmentid;
                        //displayButton.setOnClickListener
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.appointmentClicked(customer, new String[] {myAppId}, 0);
                            }
                        });
                        //row.addView(displayButton);

                        appointmentsTable.addView(row, rowCount++);
                    }
                }
            }

        }
    }
}
