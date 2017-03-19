package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    interface AppointmentListener {
        void appointmentClicked(FullCustomerSettings customer, String appointmentId);
    }
    AppointmentListener listener;

    interface NewAppointmentListener {
        void newAppointmentClicked(FullCustomerSettings customer);
    }
    NewAppointmentListener newListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.table_errors_button_fragment, container, false);
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

        Button createButton = (Button)view.findViewById(R.id.createButtom);
        displayTextView(appContext,createButton,new int [] {R.string.form_new,R.string.menu_appointments});
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countActive = 0;
                HashMap<String,Provider> providersList = (HashMap<String, Provider>) customer.getProviders().getValue();
                Set<String> providerids = providersList.keySet();
                for ( String id : providerids ) {
                    if ( providersList.get(id).isActive() ) countActive++;
                }
                if ( countActive == 0 ) {
                    Parsing.displayTextView(appContext,errorsView,R.string.errors_notsubscribed);
                } else {
                    String [] activeProviders = new String[countActive];
                    int i = 0;
                    for ( String providerid : providerids ) {
                        Log.d(myTAG,"providerid = " + providerid);
                        if ( providersList.get(providerid).isActive() ) {
                            Log.d(myTAG,providerid + " is active. i = " + i);
                            activeProviders[i] = providersList.get(providerid).getId();
                            i++;
                        }
                    }

                    DialogFragment newAppDialog = new NewAppointmentSelectProviderDialog();
                    Bundle args = newAppDialog.getArguments();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putParcelable("customer", customer);
                    args.putStringArray("activeproviders",activeProviders);
                    newAppDialog.setArguments(args);

                    newAppDialog.show(getActivity().getSupportFragmentManager(),"NewAppointmentSelectProvider");
                }
                //newListener.newAppointmentClicked(customer);
            }
        });

        String errormessage = "";
        try {
            Bundle arguments = getArguments();
            this.customer = arguments.getParcelable("customer");
            errormessage = arguments.getString("errormessage");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to extract customer details from bundle");
        }

        if ( errormessage != null && ! errormessage.equals("") ) {
            Parsing.displayTextView(appContext,errorsView,"errors_" + errormessage);
        } else if ( customer instanceof FullCustomerSettings ) {
            HashMap<String,Appointment> appointments = (HashMap<String,Appointment>)customer.getAppointments().getValue();

            TableLayout appointmentsTable = (TableLayout)view.findViewById(R.id.my_table);
            TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
            //appointmentsTable.setBackgroundColor(appContext.getColor(R.color.colorPrimaryDark));

            TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

            TableRow.LayoutParams providerLayoutParams = new TableRow.LayoutParams();
            providerLayoutParams.width = 0;
            providerLayoutParams.weight = 3;
            providerLayoutParams.setMargins(1,1,1,1);

            TableRow.LayoutParams dateLayoutParams = new TableRow.LayoutParams();
            dateLayoutParams.width = 0;
            dateLayoutParams.weight = 3;
            dateLayoutParams.setMargins(1,1,1,1);
            //dateLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            TableRow.LayoutParams timeLayoutParams = new TableRow.LayoutParams();
            timeLayoutParams.width = 0;
            timeLayoutParams.weight = 3;
            timeLayoutParams.setMargins(1,1,1,1);
            //timeLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            TableRow.LayoutParams statusLayoutParams = new TableRow.LayoutParams();
            statusLayoutParams.width = 0;
            statusLayoutParams.weight = 1;
            statusLayoutParams.setMargins(1,1,1,1);
            //timeLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            int rowCount = 0;

            TableRow row = new TableRow(appContext);
            row.setLayoutParams(rowLayoutParams);

            TextView providerCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,providerCell,providerLayoutParams,formatName(getResources().getString(R.string.appointment_provider).toUpperCase()),15,R.color.venya_table_title_cell);
            row.addView(providerCell);

            TextView dateCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,dateCell,dateLayoutParams,getResources().getString(R.string.menu_date).toUpperCase(),15,R.color.venya_table_title_cell);
            row.addView(dateCell);

            TextView timeCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,timeCell,timeLayoutParams,getResources().getString(R.string.menu_time).toUpperCase(),15,R.color.venya_table_title_cell);
            row.addView(timeCell);

            TextView statusCell = new TextView(appContext);
            Parsing.setCellFormat(appContext,statusCell,statusLayoutParams,getResources().getString(R.string.menu_status_short).toUpperCase(),15,R.color.venya_table_title_cell);
            row.addView(statusCell);

            appointmentsTable.addView(row,rowCount++);

            for ( String appointmentid : appointments.keySet() ) {
                Log.d(myTAG,"id " + appointmentid);
                Appointment appointment = appointments.get(appointmentid);

                String providerid = appointment.getProviderid();
                Provider provider = ((HashMap<String,Provider>)customer.getProviders().getValue()).get(providerid);

                if ( provider.isActive() ) { // Only display appointment for providers tht the customer is currently subcribed to

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
                    row.addView(dateCell);

                    timeCell = new TextView(appContext);
                    Parsing.setCellFormat(appContext, timeCell, timeLayoutParams, timeStr, 15, R.color.venya_table_value_cell);
                    row.addView(timeCell);

                    String status = appointment.getStatus().substring(0,2);
                    statusCell = new TextView(appContext);
                    Parsing.setCellFormat(appContext,statusCell,statusLayoutParams,status,15,R.color.venya_table_value_cell);
                    row.addView(statusCell);

                    if ( Parsing.getIndexOf(appointmentActiveStatus,appointment.getStatus()) < 0 ) {
                        row.setBackgroundColor(appContext.getColor(R.color.venya_cancelled_background));
                        providerCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));
                        dateCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));
                        timeCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));
                        statusCell.setTextColor(appContext.getColor(R.color.venya_cancelled_text));
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
                            listener.appointmentClicked(customer, myAppId);
                        }
                    });
                    //row.addView(displayButton);

                    appointmentsTable.addView(row, rowCount++);
                }
            }

        }
    }
}
