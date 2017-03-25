package dev.nohasmith.venya_android_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.HashMap;

import static dev.nohasmith.venya_android_app.MainActivity.mService;

/**
 * Created by arturo on 24/03/2017.
 */

public class ManageGoogleEvents extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    private String action;
    private String eventid;
    private Event event;
    private String sessionid;
    private FullCustomerSettings customer;
    private Appointment appointment;
    private HashMap<String,Object> eventDataMap;
    private int currentPosition = 0;

    @Override
    //public void onStart() {
    public void onCreate(Bundle savedInstanceState) {
        //super.onStart();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_calendar_async_activity);

        TextView progressView = (TextView)findViewById(R.id.progressText);
        progressView.setText(R.string.gettingData);

        try {
            action = getIntent().getExtras().getString("action");
            Log.i(TAG,"action: " + action);
            sessionid = getIntent().getExtras().getString("sessionid");
            Log.i(TAG,"sessionid: " + sessionid);
            customer = (FullCustomerSettings) getIntent().getParcelableExtra("customer");
            appointment = (Appointment)getIntent().getSerializableExtra("appointment");
            eventid = getIntent().getExtras().getString("eventid");
            Log.i(TAG,"event ID: " + eventid);
            eventDataMap = (HashMap<String, Object>)getIntent().getSerializableExtra("eventDataMap");
            Log.i(TAG,"event DAta MAP: " + eventDataMap.toString());
            currentPosition = getIntent().getExtras().getInt("currentPosition");
            Log.i(TAG,"current position: " + currentPosition);
            //newEventMap = (HashMap<String, Object>)getIntent().getSerializableExtra("newEventMap");
        } catch (Exception e) {
            Log.e(TAG + ".onStart","Failed to get arguments from Bundle");
            e.printStackTrace();
        }

        if ( (action == null || !(customer instanceof FullCustomerSettings) || !(appointment instanceof Appointment) ) ||
                (action.equals("update") && (eventid == null || eventDataMap == null)) ||
                (action.equals("insert") && eventDataMap == null) ) {
            Log.e(TAG + ".onStart","Wrong arguments received");
            backHome("badrequest");

        } else {
            new Manager().execute();
        }
    }

    private class Manager extends AsyncTask<Void, Void, Event> {

        @Override
        protected Event doInBackground(Void... params) {
            //Event event = params[0];
            switch(action) {
                case "update":
                    try {
                        Log.d(TAG + ".onCreate","getting event " + eventid + " from Google Calendar");
                        event = mService.events().get("primary", eventid).execute();
                    } catch (Exception e) {
                        Log.e(TAG + ".onStart", "Failed to get event " + eventid + " from Google Calendar");
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.d(TAG + ".onStart","Creating Event instance for " + action);
                    event = new Event();
                    break;
            }
            Log.d(TAG + ".onCreate","number of fields to process: " + eventDataMap.keySet().size());
            for ( String field : eventDataMap.keySet() ) {
                Object value = eventDataMap.get(field);
                Log.d(TAG + ".onCreate","Processing field: " + field);
                long startLong;
                long endLong;
                EventDateTime start = new EventDateTime();
                EventDateTime end = new EventDateTime();
                switch (field) {
                    case "date":
                        startLong = (long) value;
                        endLong = startLong + appointment.getDuration();

                        start.setDateTime(new DateTime(startLong));
                        end.setDateTime(new DateTime(endLong));

                        Log.d(TAG + ".onCreate","Setting start (" + startLong + ") and end (" + endLong + ")");
                        event.setStart(start);
                        event.setEnd(end);
                        break;
                    case "start":
                        startLong = (long) value;
                        start.setDateTime(new DateTime(startLong));
                        Log.d(TAG + ".onCreate","Setting start (" + startLong + ")");
                        event.setStart(start);
                        break;
                    case "end":
                        endLong = (long)value;
                        end.setDateTime(new DateTime(endLong));
                        Log.d(TAG + ".onCreate","Setting end (" + endLong + ")");
                        event.setEnd(end);
                        break;
                    default:
                        Log.d(TAG + ".onCreate","Setting " + field + ": " + (String)value);
                        event.set(field, (String) value);
                        break;
                }
            }
            try {
                Log.d(TAG + ".doInBackground","Performing action " + action);
                switch (action) {
                    case "insert":
                        Log.d(TAG + ".doInBackground", "Inserting event " + event.getSummary());
                        Event newEvent = mService.events().insert("primary", event).execute();
                        return newEvent;
                    case "delete":
                        Log.d(TAG + ".doInBackground", "Deleting evetn " + event.getId());
                        try {
                            mService.events().delete("primary", event.getId()).execute();
                            event.setStatus("deleted");
                            return event;
                        } catch (Exception e) {
                            Log.e(TAG + ".doInBackground", "Failed to " + action + " event");
                            return null;
                        }
                    case "update":
                        Log.d(TAG + ".doInBackground","Updating event " + event.getId());
                        Event updatedEvent = mService.events().update("primary",event.getId(),event).execute();
                        return updatedEvent;
                    default:
                        return null;
                }
            } catch (Exception e) {
                Log.e(TAG + ".doInBackground", "Failed to " + action + " event");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                switch (action) {
                    case "insert":
                        appointment.setGoogleId(event.getId());
                        HashMap<String, Object> updateResult = Parsing.updateAppointment(getApplicationContext(), customer, appointment);
                        String status = (String) updateResult.get("status");

                        if (!status.equals(getResources().getString(R.string.success_status))) {
                            Log.e(TAG + ".onPostCreate","Failed to update appointment with Google Event ID");
                            try {
                                String errormessage = (String)updateResult.get("errormessage");
                                backHome(errormessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                                backHome("google_update_fail");
                            }
                        } else {
                            backHome();
                        }
                        break;
                }

                backHome();
                /*
                Context intentContext = ManageGoogleEvents.this;
                Intent intent = new Intent(intentContext,Home.class);
                intent.putExtra("customer",customer);
                intent.putExtra("currentPosition",currentPosition);
                intentContext.startActivity(intent);
                */
            }else {
                Log.e(TAG + ".onPostExecute", "NULL result to " + action);
                //failedListener.newGoogleAppointmentFailed();
                backHome("nullfromserver");
            }
        }
    }

    private void backHome() {
        Context intentContext = ManageGoogleEvents.this;
        Intent intent = new Intent(intentContext,Home.class);
        intent.putExtra("sessionid",(String)customer.getSessionid().getValue());
        intent.putExtra("customer",customer);
        intent.putExtra("currentPosition",currentPosition);
        intentContext.startActivity(intent);
    }

    private void backHome(String errormessage) {
        Context intentContext = ManageGoogleEvents.this;
        Intent intent = new Intent(intentContext,Home.class);
        intent.putExtra("sessionid",(String)customer.getSessionid().getValue());
        intent.putExtra("customer",customer);
        intent.putExtra("currentPosition",currentPosition);
        intent.putExtra("errormessage",errormessage);
        intentContext.startActivity(intent);
    }
}
