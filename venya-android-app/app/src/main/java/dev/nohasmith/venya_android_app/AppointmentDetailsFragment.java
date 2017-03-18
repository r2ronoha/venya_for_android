package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

    /*
    interface AppointmentUpdateListener {
        void appointmentUpdateClicked(FullCustomerSettings customer, Appointment appointment);
    }
    AppointmentUpdateListener updateListener;
    */
    interface CancelAppointmentListener {
        void cancelAppointmentClicked(FullCustomerSettings customer);
    }
    CancelAppointmentListener cancelListener;

    @Override
    public void onAttach(Context context) {
        String myTAG = TAG + ".onAttach";
        super.onAttach(context);

        /*
        if ( context instanceof AppointmentUpdateListener ) {
            updateListener = (AppointmentUpdateListener)context;
        } else {
            Log.e(myTAG,context.toString() + " must implement AppointmentUpdateListener");
        }
        */

        if ( context instanceof CancelAppointmentListener ) {
            cancelListener = (CancelAppointmentListener)context;
        } else {
            Log.e(myTAG,context.toString() + " must implement CancelAppointmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.apointment_details_fragment, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        final Context appContext = getContext();

        String myTAG = TAG +".onStart";

        final TextView errorsView = (TextView)view.findViewById(R.id.errorsView);
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

            final Button updateButton = (Button) view.findViewById(R.id.updateButtom);
            updateButton.setText(getResources().getString(R.string.form_update));
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //updateListener.appointmentUpdateClicked(customer,appointment);
                    showUpdateTimeDialog(customer,appointment);
                }
            });

            final Button cancelButton = (Button) view.findViewById(R.id.cancelButtom);
            cancelButton.setText(getResources().getString(R.string.form_cancel));
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appointment.setStatus("cancelled");
                    HashMap<String,Object> updateResult = Parsing.updateAppointment(appContext,customer,appointment);
                    String status = (String)updateResult.get("status");

                    if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
                        String errormessage = (String)updateResult.get("errormessage");
                        Parsing.displayTextView(appContext,errorsView,"errors_" + errormessage);
                    } else {
                        cancelListener.cancelAppointmentClicked(customer);
                    }

                }
            });

            TableLayout appTable = (TableLayout) view.findViewById(R.id.my_table);
            int rowCount = 0;

            TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);

            TableRow.LayoutParams titleLayoutParams = new TableRow.LayoutParams();
            titleLayoutParams.width = 0;
            titleLayoutParams.weight = 1;
            titleLayoutParams.setMargins(1,1,1,1);

            TableRow.LayoutParams valueLayoutParams = new TableRow.LayoutParams();
            valueLayoutParams.width = 0;
            valueLayoutParams.weight = 2;
            valueLayoutParams.setMargins(1,1,1,1);

            TableRow row;

            for (int i = 0; i < appointmentFields.length; i++) {
                String field = appointmentFields[i];
                TextView titleCell;
                TextView valueCell;

                if (!field.equals("id") && !field.equals("customerid")) {

                    try {
                        switch (field) {
                            case "providerid":
                                HashMap<String, Provider> customerProviders = (HashMap<String, Provider>) customer.getProviders().getValue();
                                Provider provider = customerProviders.get(appointment.getProviderid());

                                //PROVIDER'S NAME RO
                                row = new TableRow(appContext);
                                row.setLayoutParams(rowLayoutParams);
;
                                String name = provider.getName();
                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,Parsing.formatName(getResources().getString(R.string.appointment_provider).toUpperCase()),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,Parsing.formatName(name),10,R.color.venya_table_value_cell);
                                row.addView(valueCell);

                                appTable.addView(row, 0);

                                // PROVIDER'S ADDRESS ROW
                                row = new TableRow(appContext);
                                row.setLayoutParams(rowLayoutParams);

                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,getResources().getString(R.string.customer_address).toUpperCase(),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,provider.getAddress().formatAddress(),10,R.color.venya_table_value_cell);
                                row.addView(valueCell);

                                appTable.addView(row, 1);
                                break;

                            case "date":
                                // DATE ROW
                                row = new TableRow(appContext);
                                row.setLayoutParams(rowLayoutParams);

                                Log.d(myTAG,"Date : " + appointment.getField("date"));

                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,Parsing.formatName(getResources().getString(R.string.appointment_date).toUpperCase()),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                String date = DateFormat.getDateInstance().format(appointment.getDate());
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,date,10,R.color.venya_table_value_cell);
                                row.addView(valueCell);
                                appTable.addView(row, 2);

                                // TIME ROW
                                row = new TableRow(appContext);
                                row.setLayoutParams(rowLayoutParams);

                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,Parsing.formatName(getResources().getString(R.string.appointment_time)),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                String time = DateFormat.getTimeInstance().format(appointment.getDate());
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,time,10,R.color.venya_table_value_cell);
                                row.addView(valueCell);

                                appTable.addView(row, 3);
                                row.setLayoutParams(rowLayoutParams);
                                break;

                            case "status":
                                row = new TableRow(appContext);
                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,getResources().getString(R.string.appointment_status),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);

                                valueCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,appointment.getStatus(),10,R.color.venya_table_value_cell);
                                row.addView(valueCell);
                                appTable.addView(row, 4);
                                break;

                            case "delay":
                                row = new TableRow(appContext);
                                row.setLayoutParams(rowLayoutParams);

                                titleCell = new TextView(appContext);
                                Parsing.setCellFormat(appContext,titleCell,titleLayoutParams,getResources().getString(R.string.appointment_delay),10,R.color.venya_table_title_cell);
                                row.addView(titleCell);
                                // convert delay (long milliseconds to minutes string)
                                valueCell = new TextView(appContext);
                                long delayMin = (appointment.getDelay() / 1000) / 60;
                                Parsing.setCellFormat(appContext,valueCell,valueLayoutParams,Long.toString(delayMin) + " min",10,R.color.venya_table_value_cell);
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

    private void showUpdateTimeDialog(FullCustomerSettings customer, Appointment appointment) {
        //DialogFragment dialog = new UpdateAppointmentTimeDialog();
        DialogFragment dialog = new UpdateAppointmentDateDialog();

        Bundle args = dialog.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);
        args.putSerializable("appointment",appointment);
        dialog.setArguments(args);

        dialog.show(getActivity().getSupportFragmentManager(),"UpdateAppointmentTimeDialog");
    }

}
