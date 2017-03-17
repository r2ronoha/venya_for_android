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
import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.appointmentFields;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentDetailsFragment extends Fragment {
    final String TAG = "AppointmentDetailsFragment";
    FullCustomerSettings customer;
    String appointmentid;
    Appointment appointment;

    public AppointmentDetailsFragment() {
        // Required empty public constructor
    }

    interface AppointmentUpdateListener {
        void appointmentUpdateClicked(FullCustomerSettings customer, Appointment appointment);
    }
    AppointmentUpdateListener updateListener;

    @Override
    public void onAttach(Context context) {
        String myTAG = TAG + ".onAttach";
        super.onAttach(context);

        if ( context instanceof AppointmentUpdateListener ) {
            updateListener = (AppointmentUpdateListener)context;
        } else {
            Log.e(myTAG,context.toString() + " must implement AppointmentUpdateListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.table_errors_button_fragment, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        Context appContext = getContext();

        String myTAG = TAG +".onStart";

        TextView errorsView = (TextView)view.findViewById(R.id.errorsView);
        errorsView.setText("");

        TextView title = (TextView)view.findViewById(R.id.tableFragmentTitle);
        Parsing.displayTextView(appContext,title,R.string.header_appointmentdetails);

        try {
            Bundle args = getArguments();
            customer = (FullCustomerSettings)args.getParcelable("customer");
            appointmentid = args.getString("appointmentid");
            //appointment = (Appointment)args.getSerializable("appointment");
        } catch (Exception e) {
            Log.e(myTAG,"failed to get information from Bundle");
            e.printStackTrace();
        }

        if ( customer != null && appointmentid != null ) {
            HashMap<String,Appointment> appointments = (HashMap<String,Appointment>)customer.getAppointments().getValue();
            appointment = appointments.get(appointmentid);

            Button updateButton = (Button) view.findViewById(R.id.createButtom);
            updateButton.setText(getResources().getString(R.string.form_submit));

            TableLayout appTable = (TableLayout) view.findViewById(R.id.my_table);
            int rowCount = 0;

            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            TableRow row;

            for (int i = 0; i < appointmentFields.length; i++) {
                String field = appointmentFields[i];
                TextView titleCell;
                TextView valueCell;

                if (!field.equals("id") && !field.equals("customerid")) {
                    //titleCell.setPadding(10,10,10,10);
                    //valueCell.setPadding(10,10,10,10);

                    try {
                        switch (field) {
                            case "providerid":
                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);
                                titleCell = new TextView(appContext);
                                valueCell = new TextView(appContext);
                                titleCell.setPadding(10, 10, 10, 10);
                                valueCell.setPadding(10, 10, 10, 10);

                                HashMap<String, Provider> customerProviders = (HashMap<String, Provider>) customer.getProviders().getValue();
                                Provider provider = customerProviders.get(appointment.getProviderid());
                                // create a row for the provider's name
                                String name = provider.getName();
                                titleCell.setText(Parsing.formatName(getResources().getString(R.string.appointment_provider).toUpperCase()));
                                row.addView(titleCell);

                                valueCell.setText(Parsing.formatName(name));
                                row.addView(valueCell);

                                appTable.addView(row, 0);

                                // create a row for the provider's address
                                String address = provider.getAddress().formatAddress();
                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);

                                titleCell = new TextView(appContext);
                                titleCell.setText(Parsing.formatName(getResources().getString(R.string.customer_address).toUpperCase()));
                                titleCell.setPadding(10, 10, 10, 10);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                valueCell.setText(address);
                                valueCell.setPadding(10, 10, 10, 10);
                                row.addView(valueCell);

                                appTable.addView(row, 1);
                                break;

                            case "date":
                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);
                                titleCell = new TextView(appContext);
                                valueCell = new TextView(appContext);
                                titleCell.setPadding(10, 10, 10, 10);
                                valueCell.setPadding(10, 10, 10, 10);

                                Log.d(myTAG,"Date : " + appointment.getField("date"));
                                String date = DateFormat.getDateInstance().format(appointment.getDate());
                                titleCell.setText(Parsing.formatName(getResources().getString(R.string.appointment_date).toUpperCase()));
                                row.addView(titleCell);
                                valueCell.setText(date);
                                row.addView(valueCell);
                                appTable.addView(row, 2);

                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);
                                String time = DateFormat.getTimeInstance().format(appointment.getDate());
                                titleCell = new TextView(appContext);
                                titleCell.setText(Parsing.formatName(getResources().getString(R.string.appointment_time)));
                                titleCell.setPadding(10, 10, 10, 10);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                valueCell.setText(time);
                                valueCell.setPadding(10, 10, 10, 10);
                                row.addView(valueCell);

                                appTable.addView(row, 3);
                                break;

                            case "status":
                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);
                                titleCell = new TextView(appContext);
                                valueCell = new TextView(appContext);
                                titleCell.setPadding(10, 10, 10, 10);
                                valueCell.setPadding(10, 10, 10, 10);

                                titleCell.setText(getResources().getString(R.string.appointment_status));
                                row.addView(titleCell);
                                valueCell.setText(appointment.getStatus());
                                row.addView(valueCell);
                                appTable.addView(row, 4);
                                break;

                            case "delay":
                                row = new TableRow(appContext);
                                row.setLayoutParams(layoutParams);
                                titleCell = new TextView(appContext);
                                valueCell = new TextView(appContext);
                                titleCell.setPadding(10, 10, 10, 10);
                                valueCell.setPadding(10, 10, 10, 10);

                                titleCell.setText(getResources().getString(R.string.appointment_delay));
                                row.addView(titleCell);
                                // convert delay (long milliseconds to minutes string)
                                long delayMin = (appointment.getDelay() / 1000) / 60;
                                valueCell.setText(Long.toString(delayMin) + " min");
                                row.addView(valueCell);

                                appTable.addView(row, 5);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e(myTAG,"Exception while creating the table");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Parsing.displayTextView(appContext,errorsView,R.string.errors_httpexception);
        }
    }

}
