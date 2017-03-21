package dev.nohasmith.venya_android_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.PendingIntent.getActivity;
import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;
import static dev.nohasmith.venya_android_app.MainActivity.appLanguage;
import static dev.nohasmith.venya_android_app.MainActivity.locale_from_language;
import static dev.nohasmith.venya_android_app.MainActivity.menuOptions;
import static dev.nohasmith.venya_android_app.MainActivity.menuOptionsTags;
import static dev.nohasmith.venya_android_app.MainActivity.supportedLanguages;
import static dev.nohasmith.venya_android_app.MainActivity.venyaUrl;



/**
 * Created by arturo on 07/03/2017.
 */
public class Home extends AppCompatActivity implements
        SettingsFragment.ChangeAddressListener,
        SettingsFragment.ChangeEmailListener,
        SettingsFragment.ChangeUsernameListener,
        SettingsFragment.ChangePasswordListener,
        SettingsFragment.ChangeLanguageListener,
        SettingsFragment.UpdateBooleanListener,
        SettingsFragment.ChangePhoneListener,

        ChangeAddressFragment.UpdateAddressListener,
        ChangeAddressFragment.CancelListener,

        ChangeEmailFragment.UpdateEmailListener,
        ChangeEmailFragment.CancelListener,

        ChangeUsernameFragment.UpdateUsernameListener,
        ChangeUsernameFragment.CancelListener,

        ChangePasswordFragment.UpdatePasswordListener,
        ChangePasswordFragment.CancelListener,

        ChangeLanguageFragment.UpdateLanguageListener,
        ChangeLanguageFragment.CancelListener,
        ChangeLanguageDialog.UpdateLanguageListener,

        ChangePhoneFragment.UpdatePhoneListener,
        ChangePhoneFragment.CancelListener,

        UnsubscribeConfirmationDialog.ConfirmDialogListener,

        CalendarFragment.ViewAppointmentListener,
        AppointmentsFragment.AppointmentListener,
        AppointmentsFragment.NewAppointmentListener,
        AppointmentsFragment.GoToCalendarListener,

        AppointmentDetailsFragment.BackListener,
        AppointmentDetailsFragment.CancelAppointmentListener,

        UpdateAppointmentTimeDialog.ConfirmUpdateListener,

        NewAppointmentSelectTimeDialog.NewAppointmentListener{
    String TAG = this.getClass().getSimpleName();

    public String SESSION_ID = "closed";
    public FullCustomerSettings customer;
    private static String hostname;
    private static String port;
    ActionBar actionBar;
    //private String [] menuOptions;
    private ListView menuList;
    private int currentPosition = 0;
    ActionBarDrawerToggle menuToggle;
    private DrawerLayout menuLayout;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String myTAG = TAG + ".onCreate";
        super.onCreate(savedInstanceState);
        //Parsing.setLocale(this,"es");
        setContentView(R.layout.home);
        appContext = getApplicationContext();

        hostname = getResources().getString(R.string.venya_node_server);
        port = getResources().getString(R.string.venya_node_port);

        // Check if the screen has been rotated (there was an option already selected) and we set it back
        if ( savedInstanceState != null ) {
            currentPosition = savedInstanceState.getInt("position");
            customer = (FullCustomerSettings)savedInstanceState.getParcelable("customer");
            SESSION_ID = savedInstanceState.getString("sessionid");
            setActionBarTitle(currentPosition);
        } else {

            Log.d(myTAG, "Getting Extras");
            SESSION_ID = (String) getIntent().getExtras().get("sessionid");
            customer = (FullCustomerSettings) getIntent().getParcelableExtra("customer");
        }

        appLanguage = (String) customer.getLanguage().getValue();
        Parsing.setLocale(this,locale_from_language.get(appLanguage));
        menuOptions = getResources().getStringArray(R.array.menuOptions);

        Toolbar toolbar = (Toolbar)findViewById(R.id.venya_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Create menu based on the array from strings
        //menuOptions super.attachBaseContext(MyContextWrapper.wrapContext(newBase,appLanguage));= getResources().getStringArray(R.array.menuOptions);
        menuList = (ListView)findViewById(R.id.expanded_menu);
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,menuOptions);
        menuList.setAdapter(menuAdapter);

        menuList.setOnItemClickListener(new MenuItemClickListener());
        menuLayout = (DrawerLayout)findViewById(R.id.menuLayout);

        //homeContext = appContext;
        //Activity myActivity = ((AppCompatActivity)getP;
        menuToggle = new ActionBarDrawerToggle((Activity)this, menuLayout, R.string.menu_open, R.string.menu_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };
        menuLayout.addDrawerListener(menuToggle);

        if ( SESSION_ID == null || customer == null) {
            Toast toast = new Toast(this);
            String failed = ( customer == null ) ? "customer" : "sessionid";
            Log.d(myTAG,"Failed To Parse " + failed);
            toast.makeText(this,"Failed To Parse " + failed,Toast.LENGTH_LONG).show();
        } else {
            //TextView errorsView = (TextView)findViewById(R.id.homeErrorsView);
            //errorsView.setText(SESSION_ID);
            //Log.d(myTAG,"Home activity started with sessionid = " + SESSION_ID);
            //Log.d(myTAG,"customer: " + (String)customer.getFieldElement("firstname","value") + " " + (String)customer.getFieldElement("surname","value"));

            //set locale to customers profile language
            String lang = (String) customer.getLanguage().getValue();
            Parsing.setLocale(this,locale_from_language.get(lang));

            selectItem(currentPosition);
        }

    }

    private void setActionBarTitle(int position) {
        String option = menuOptionsTags[position];
        String title;
        title = "menu_" + option;
        int titleID = Parsing.getResId(getApplication(),title);
        getSupportActionBar().setTitle(titleID);
    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment = null;
        Toast toast = new Toast(this);

        switch (position) {
            case 1:
                //appointments
                toast.makeText(this,getResources().getString(R.string.menu_appointments).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new AppointmentsFragment();
                Bundle appArgs = fragment.getArguments();
                if ( appArgs == null ) { appArgs = new Bundle(); }
                appArgs.putParcelable("customer",customer);
                fragment.setArguments(appArgs);
                break;
            case 2:
                // notifications
                toast.makeText(this,getResources().getString(R.string.errors_pagenotavailable).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 3:
                // show providers
                toast.makeText(this,getResources().getString(R.string.zone_provider).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new CustomerProvidersFragment(SESSION_ID,customer);
                break;
            case 4:
                // settings
                toast.makeText(this,getResources().getString(R.string.menu_settings).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new SettingsFragment(SESSION_ID, customer);
                break;
            case 5:
                // logout
                logout(Home.this,customer);
                break;
            case 6:
                // change address
                toast.makeText(this,getResources().getString(R.string.menu_changeaddress).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeAddressFragment(customer);
                break;
            case 7:
                // change email
                toast.makeText(this,getResources().getString(R.string.menu_changeemail).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeEmailFragment(customer);
                break;
            case 8:
                // change usernamge
                toast.makeText(this,getResources().getString(R.string.menu_changeusername).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeUsernameFragment(customer);
                break;
            case 9:
                // change password
                toast.makeText(this,getResources().getString(R.string.menu_changepassword).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangePasswordFragment(customer);
                break;
            case 10:
                // change language
                toast.makeText(this,getResources().getString(R.string.menu_changelanguage).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeLanguageFragment();
                Bundle langArgs = fragment.getArguments();
                if ( langArgs == null ) { langArgs = new Bundle(); }
                langArgs.putParcelable("customer", customer);
                langArgs.putInt("currentPosition",currentPosition);
                fragment.setArguments(langArgs);
                break;
            case 11:
                // change language
                toast.makeText(this,getResources().getString(R.string.menu_changephone).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangePhoneFragment(customer);
                break;
            case 12:
                // see appointment details
                toast.makeText(this,getResources().getString(R.string.menu_changephone).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new AppointmentDetailsFragment();
                /*Bundle args = fragment.getArguments();
                if ( args == null ) { args = new Bundle(); }
                args.putParcelable("customer",customer);
                //args.putSerializable("appointment,");
                fragment.setArguments(args);
                */
                break;
            case 13:
                // update appointment fragment
                toast.makeText(this,getResources().getString(R.string.menu_updateappointment).toUpperCase(),Toast.LENGTH_SHORT);
                fragment = new UpdateAppointmentTimeDialog();
                break;
            case 14:
                // go to calendar view
                toast.makeText(this,menuOptionsTags[14].toUpperCase(),Toast.LENGTH_SHORT);
                Fragment calendarFragment = new CalendarFragment();
                Bundle args = calendarFragment.getArguments();
                args.putParcelable("customer",customer);
                calendarFragment.setArguments(args);
                break;
            default:
                // home
                toast.makeText(this,getResources().getString(R.string.home_welcome),Toast.LENGTH_SHORT).show();
                fragment = new HomeFragment(SESSION_ID, customer);
        }

        if ( fragment != null ) {
            goToFragment(fragment,position);
        }
    }

    public void logout(Context intentContext, FullCustomerSettings customer) {
        String myTAG = TAG + ".logout";
        Toast toast = new Toast(this);
        toast.makeText(this,getResources().getString(R.string.goodbye).toUpperCase(),Toast.LENGTH_SHORT).show();
        String newSessionid = Parsing.setSessionId(appContext,(String)customer.getFieldElement("id","value"),getResources().getString(R.string.sessionclosed),"customer");
        if ( newSessionid.equals(getResources().getString(R.string.sessionclosed)) ) {
            Log.d(myTAG,"session closed");
            Intent intent = new Intent(intentContext, MainActivity.class);
            intentContext.startActivity(intent);
        } else {
            Log.e(myTAG,"Failed to close session. Sending to logout with error message");
            Intent intent = new Intent(intentContext, MainActivity.class);
            intent.putExtra("error",getResources().getString(R.string.errors_invalidsessionid));
            intentContext.startActivity(intent);
        }
    }

    public void goToFragment(Fragment fragment, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setActionBarTitle(position);

        menuLayout = (DrawerLayout) findViewById(R.id.menuLayout);
        menuLayout.closeDrawer(menuList);
    }

    public void goToFragment(Fragment fragment, Bundle bundle, int position) {
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setActionBarTitle(position);

        menuLayout = (DrawerLayout) findViewById(R.id.menuLayout);
        menuLayout.closeDrawer(menuList);
    }
/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }
*/
    /*
    @Override
    protected void onResume() {
        invalidateOptionsMenu();

        /*Bundle instanceState = new Bundle();
        instanceState.putString("sessionid",SESSION_ID);
        instanceState.putInt("position",currentPosition);
        instanceState.putParcelable("customer",customer);

        onCreate(instanceState);
        //onCreate(null);
        super.onResume();
        super.recreate();
        //
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        //setContentView(R.layout.home);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( menuToggle.onOptionsItemSelected(item) ) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.options_menu_settings:
                Fragment fragment = new SettingsFragment(SESSION_ID,customer);
                goToFragment(fragment,Parsing.getIndexOf(menuOptionsTags,"settings"));
                return true;
            case R.id.options_menu_lang:
                DialogFragment langDialog = new ChangeLanguageDialog();
                Bundle args = langDialog.getArguments();
                if ( args == null ) { args = new Bundle(); }
                args.putParcelable("customer",customer);
                args.putInt("currentPosition",currentPosition);
                langDialog.setArguments(args);
                langDialog.show(getSupportFragmentManager(),"change language options menu");
                return true;
            case R.id.options_menu_logout:
                logout(Home.this,customer);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstance) {
        super.onPostCreate(savedInstance);
        menuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        menuToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("position", currentPosition);
        state.putParcelable("customer", customer);
        state.putString("sessionid",SESSION_ID);
    }

    // Listeners from Settings page to go to each settting update page
    public void changeAddressClicked(FullCustomerSettings customer) {
        goToFragment(new ChangeAddressFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changeaddress"));
    }

    public void changeEmailClicked(FullCustomerSettings customer) {
        goToFragment(new ChangeEmailFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changeemail"));
    }

    public void changeUsernameClicked(FullCustomerSettings customer) {
        goToFragment(new ChangeUsernameFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changeusername"));
    }

    public void changePasswordClicked(FullCustomerSettings customer) {
        goToFragment(new ChangePasswordFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changepassword"));
    }

    public void changeLanguageClicked(FullCustomerSettings customer) {
        Fragment fragment = new ChangeLanguageFragment();
        Bundle args = fragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putInt("currentPosition",currentPosition);
        args.putParcelable("customer",customer);

        goToFragment(fragment,args,Parsing.getIndexOf(menuOptionsTags,"changelanguage"));
    }

    public void changePhoneClicked(FullCustomerSettings customer) {
        goToFragment(new ChangePhoneFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changephone"));
    }

    // Listeners from the settings update pages to go back to settings after update
    public void updateAddressClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    public void updateEmailClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    public void updateUsernameClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    public void updatePasswordClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    public void updatePhoneClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    // update language via standard fragment
    public void updateLanguageClicked(FullCustomerSettings customer, int currentPosition) {
        appLanguage = (String)customer.getLanguage().getValue();

        Parsing.setLocale(this,locale_from_language.get((String)customer.getLanguage().getValue()));

        invalidateOptionsMenu();
        menuOptions = getResources().getStringArray(R.array.menuOptions);
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,menuOptions);
        menuList.setAdapter(menuAdapter);

        this.customer = customer;
        selectItem(currentPosition);

        //goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }
    // update language via options menu alert dialog
    @Override
    public void updateLanguageClicked(FullCustomerSettings customer, int choice, int currentPosition) {
        String response = null;
        Toast toast = new Toast(appContext);

        String lang = supportedLanguages[choice].toLowerCase();
        //if (!lang.equals((String) customer.getLanguage().getValue())) {
        String reqUrl = venyaUrl + "/updateSetting?" +
                "action=update&type=customer" +
                "&id=" + (String) customer.getId().getValue() +
                "&field=language" +
                "&newvalue=" + lang;
        MyHttpHandler httpHandler = new MyHttpHandler(appContext);
        try {
            response = httpHandler.execute(reqUrl).get();
        } catch (Exception e) {
            Log.e("ChangeLanguage.OptionSelected", "Failed to update customer with language: " + lang);
            toast.makeText(appContext,R.string.errors_failedupdate,Toast.LENGTH_LONG);
        }

        if (response == null) {
            Log.e("ChangeLanguage.OptionSelected", "NULL response from server to lang: " + lang);
            toast.makeText(appContext,R.string.errors_failedupdate,Toast.LENGTH_LONG);
        } else {
            HashMap<String, Object> parsedResponse = Parsing.parseGetCustomerResponseJson(response, appContext);
            String status = (String) parsedResponse.get("status");
            Log.d("CHangeLAnguage.OptinSelected", "Request status: " + status);
            if (!status.equals(getResources().getString(R.string.success_status))) {
                String errormessage = (String) parsedResponse.get("errormessage");
                Log.d("ChangeLanguage.Optionselecte", "Error to update request = " + errormessage);
                try {
                    toast.makeText(appContext,Parsing.getResId(appContext,"errors_" + errormessage),Toast.LENGTH_LONG);
                } catch (Exception e) {
                    toast.makeText(appContext,errormessage,Toast.LENGTH_LONG);
                }
            } else {
                CustomerSettings updatedCustomer = (CustomerSettings) parsedResponse.get("customer");
                String updatedLang = updatedCustomer.getLanguage();
                customer.setField("language", updatedLang);

                appLanguage = (String)customer.getLanguage().getValue();

                Parsing.setLocale(this,locale_from_language.get((String)customer.getLanguage().getValue()));

                invalidateOptionsMenu();
                menuOptions = getResources().getStringArray(R.array.menuOptions);
                ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,menuOptions);
                menuList.setAdapter(menuAdapter);

                this.customer = customer;
                selectItem(currentPosition);
            }
        }
    }

    public void updateBooleanClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    /*@Override
    public void appointmentClicked(FullCustomerSettings customer, String appointmentId) {
        // display all appointment details
        Bundle args = new Bundle();
        args.putParcelable("customer",customer);
        args.putString("appointmentid",appointmentId);
        goToFragment(new AppointmentDetailsFragment(),args,Parsing.getIndexOf(menuOptionsTags,"appointmentdetails"));
    }*/

    @Override
    public void appointmentClicked(FullCustomerSettings customer, String [] appointmentsList, long date) {
        /*
         if only 1 appointment passed, display its details (AppointmentDetails Fragment).
         if multiple appointments, swho list of appointments in appointments fragmet
          */
        Bundle args = new Bundle();
        args.putParcelable("customer", customer);

        if ( appointmentsList.length == 1 ) {
            String appointmentId = appointmentsList[0];
            Log.d(TAG + ".appointmentClicked","Calling AppointmentDetails with appointmentid " + appointmentId);
            args.putString("appointmentid", appointmentId);
            goToFragment(new AppointmentDetailsFragment(), args, Parsing.getIndexOf(menuOptionsTags, "appointmentdetails"));
        } else {
            args.putStringArray("appointmentsList",appointmentsList);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dateStr = df.format(new Date(date));
            String title = Parsing.formatMessage(new String[] { getResources().getString(R.string.title_dayAppointments), dateStr });
            args.putString("title",title);
            goToFragment(new AppointmentsFragment(),args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
        }
    }

    public void newAppointmentClicked(FullCustomerSettings customer) {
        // got to create appointment fragment
    }

    // Listener for the cancel button in update settings pages
    public void cancelClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    // Listener for unsubscribe provider confirmation dialog


    @Override
    public void onDialogPositiveClick() {
        goToFragment(new HomeFragment(),0);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Context context, String providerid, String sessionid) {
        if ( Parsing.unsubscribeProvider(context,providerid,sessionid) ) {
            //update custoemr
            customer.removeProvider(providerid);
        } else {
            Toast toast = new Toast(appContext);
            toast.makeText(appContext,getResources().getString(R.string.errors_failedupdate),Toast.LENGTH_SHORT);
        }
        // call the customer's providers fragment with updated customer
        goToFragment(new CustomerProvidersFragment(sessionid,customer),Parsing.getIndexOf(menuOptionsTags,"providers"));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Context context, FullCustomerSettings customer, Appointment appointment) {
        appointment.setStatus("cancelled");
        HashMap<String, Object> updateResult = Parsing.updateAppointment(appContext, customer, appointment);
        String status = (String) updateResult.get("status");

        if (!status.equals(getResources().getString(R.string.success_status))) {
            // if the update failed, show a message (toast) and go back to the appointment details
            Toast toast = new Toast(appContext);
            toast.makeText(appContext,getResources().getString(R.string.errors_failedupdate).toUpperCase(),Toast.LENGTH_LONG);

            Fragment fragment = new AppointmentDetailsFragment();

            Bundle args = fragment.getArguments();
            if ( args == null ) { args = new Bundle(); }
            args.putParcelable("customer",customer);
            args.putString("appointmentid",appointment.getId());

            goToFragment(fragment,args,Parsing.getIndexOf(menuOptionsTags,"appointmentdetails"));
        } else {
            // if update successfull, got to the appointmets fragment
            //cancelAppointmentClicked(customer);
            Toast toast = new Toast(appContext);
            toast.makeText(appContext,getResources().getString(R.string.appointment_cancelled).toUpperCase(),Toast.LENGTH_SHORT);

            Bundle args = new Bundle();
            args.putParcelable("customer",customer);

            goToFragment(new AppointmentsFragment(),args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast toast = new Toast(appContext);
        toast.makeText(appContext,getResources().getString(R.string.form_cancel),Toast.LENGTH_SHORT).show();
        goToFragment(new CustomerProvidersFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"providers"));
    }

    // Listener to insert the new appointment after provider, date and time have been confirmed
    @Override
    public void newAppointmentClicked(FullCustomerSettings customer, Appointment appointment) {
        String myTAG = TAG + ".newAppointmentClicked";
        Log.d(myTAG,"number of appointments for this customer BEFORE insertion: " + ((HashMap<String,Appointment>)customer.getAppointments().getValue()).size());
        HashMap<String,Object> response = Parsing.insertAppointment(appContext,appointment);
        Fragment fragment = new AppointmentsFragment();

        Bundle args = fragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);
        args.putSerializable("appointment",appointment);

        String status = (String)response.get("status");
        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
            String errormessage = (String)response.get("errormessage");
            args.putString("errormessage",errormessage);
        } else {
            appointment.setId((String)response.get("appointmentid"));
            Log.d(myTAG,"appointment " + appointment.getId() + " added to the customer");
            customer.addAppointment(appointment);
            args.putParcelable("customer",customer);
        }
        Log.d(myTAG,"number of appointments for this customer AFTER insertion: " + ((HashMap<String,Appointment>)customer.getAppointments().getValue()).size());

        goToFragment(fragment,args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
    }

    // Listener to go to update appoitment fragment from fragment details fragment
    /*
    @Override
    public void appointmentUpdateClicked(FullCustomerSettings customer, Appointment appointment) {
        Fragment newFragment = new UpdateAppointmentTimeDialog();
        Bundle args = newFragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);
        args.putSerializable("appointment",appointment);
        goToFragment(newFragment,args,Parsing.getIndexOf(menuOptionsTags,"updateappointment"));
    }
    */

    // back button in appointment details fragment -> go back to appointments view
    @Override
    public void backClicked(FullCustomerSettings customer) {
        Fragment fragment = new AppointmentsFragment();

        Bundle args = fragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);

        goToFragment(fragment,args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
    }

    @Override
    public void cancelAppointmentClicked(FullCustomerSettings customer) {
        Toast toast = new Toast(appContext);
        toast.makeText(appContext,getResources().getString(R.string.appointment_cancelled).toUpperCase(),Toast.LENGTH_SHORT);

        Bundle args = new Bundle();
        args.putParcelable("customer",customer);

        goToFragment(new AppointmentsFragment(),args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
    }

    @Override
    public void goToCalendarClicked(FullCustomerSettings customer) {
        Fragment fragment = new CalendarFragment();

        Bundle args = fragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);

        goToFragment(fragment,args,Parsing.getIndexOf(menuOptionsTags,"calendar"));
    }

    // Listener for appointment change of date
    @Override
    public void confirmNewDatePositiveClick(DialogFragment fragment, FullCustomerSettings customer, Appointment appointment) {
        String myTAG = TAG + ".confirmNewDatePositiveClick";

        Fragment newFragment = new AppointmentsFragment();
        Bundle args = newFragment.getArguments();
        if ( args == null ) { args = new Bundle(); }

        HashMap<String,Object> updateResult = Parsing.updateAppointment(appContext,customer,appointment);
        String status = (String)updateResult.get("status");
        if ( ! status.equals(getResources().getString(R.string.success_status)) ) {
            String errormessage = (String)updateResult.get("errormessage");
            args.putString("errormessage",errormessage);
        }
        FullCustomerSettings newCustomer = (FullCustomerSettings)updateResult.get("customer");
        args.putParcelable("customer",newCustomer);

        goToFragment(newFragment,args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
    }

    public void confirmNewDateNegativeClick(DialogFragment fragment, FullCustomerSettings customer) {
        Toast toast = new Toast(appContext);
        toast.makeText(appContext,getResources().getString(R.string.form_cancel),Toast.LENGTH_SHORT).show();

        Fragment newFragment = new AppointmentsFragment();
        Bundle args = newFragment.getArguments();
        if ( args == null ) { args = new Bundle(); }
        args.putParcelable("customer",customer);

        goToFragment(newFragment,args,Parsing.getIndexOf(menuOptionsTags,"appointments"));
    }


/*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrapContext(newBase,appLanguage));
    }
    */
}
