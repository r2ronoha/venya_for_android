package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;


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
        String myTAG = TAG + ".onStart";

        View view = getView();
        Context appContext = getContext();

        TextView viewTitle = (TextView)view.findViewById(R.id.tableFragmentTitle);
        Parsing.displayTextView(appContext,viewTitle,R.string.menu_appointments);

        TextView errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        Button createButton = (Button)view.findViewById(R.id.createButtom);
        Parsing.displayTextView(appContext,createButton,new int [] {R.string.form_new,R.string.menu_appointments});
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newListener.newAppointmentClicked(customer);
            }
        });

        Bundle arguments = getArguments();
        try {
            this.customer = (FullCustomerSettings)arguments.getParcelable("customer");
        } catch (Exception e) {
            Log.e(myTAG,"Failed to extract customer details from bundle");
        }

        if ( customer != null ) {
            HashMap<String,Appointment> appointments = (HashMap<String,Appointment>)customer.getAppointments().getValue();

            TableLayout appointmentsTable = (TableLayout)view.findViewById(R.id.my_table);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            int rowCount = 0;

            TableRow row = new TableRow(appContext);
            row.setLayoutParams(layoutParams);

            TextView providerCell = new TextView(appContext);
            providerCell.setText(getResources().getString(R.string.appointment_provider).toUpperCase());
            providerCell.setPadding(15,15,15,15);
            row.addView(providerCell);

            TextView dateCell = new TextView(appContext);
            dateCell.setText(getResources().getString(R.string.menu_date).toUpperCase());
            providerCell.setPadding(15,15,15,15);
            row.addView(dateCell);

            TextView timeCell = new TextView(appContext);
            timeCell.setText(getResources().getString(R.string.menu_time).toUpperCase());
            timeCell.setPadding(15,15,15,15);
            row.addView(timeCell);

            appointmentsTable.addView(row,rowCount++);

            for ( String appointmentid : appointments.keySet() ) {
                Log.d(myTAG,"id " + appointmentid);
                Appointment appointment = appointments.get(appointmentid);

                row = new TableRow(appContext);

                String providerid = appointment.getProviderid();
                Provider provider = ((HashMap<String,Provider>)customer.getProviders().getValue()).get(providerid);
                String name = provider.getName();
                providerCell = new TextView(appContext);
                providerCell.setText(Parsing.formatName(name));
                providerCell.setPadding(15,15,15,15);
                row.addView(providerCell);

                long dateLong = appointment.getDate();
                Date date = new Date(dateLong);
                String dateStr = DateFormat.getDateInstance().format(date);
                String timeStr = DateFormat.getTimeInstance().format(date);

                dateCell = new TextView(appContext);
                dateCell.setText(dateStr);
                dateCell.setPadding(15,15,15,15);
                row.addView(dateCell);

                timeCell = new TextView(appContext);
                timeCell.setText(timeStr);
                timeCell.setPadding(15,15,15,15);
                row.addView(timeCell);

                Button displayButton  =new Button(appContext);
                displayButton.setText(getResources().getString(R.string.appointment_display).toUpperCase());
                displayButton.setPadding(10,10,10,10);

                final String myAppId = appointmentid;
                displayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.appointmentClicked(customer,myAppId);
                    }
                });
                row.addView(displayButton);

                appointmentsTable.addView(row,rowCount++);
            }

        }
    }

}
