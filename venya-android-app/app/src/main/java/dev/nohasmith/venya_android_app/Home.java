package dev.nohasmith.venya_android_app;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;
import static dev.nohasmith.venya_android_app.MainActivity.appLanguage;
import static dev.nohasmith.venya_android_app.MainActivity.locale_from_language;
import static dev.nohasmith.venya_android_app.MainActivity.menuOptions;
import static dev.nohasmith.venya_android_app.MainActivity.menuOptionsTags;

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

        ChangeAddressFragment.UpdateAddressListener,
        ChangeAddressFragment.CancelListener,

        ChangeEmailFragment.UpdateEmailListener,
        ChangeEmailFragment.CancelListener,

        ChangeUsernameFragment.UpdateUsernameListener,
        ChangeUsernameFragment.CancelListener,

        ChangePasswordFragment.UpdatePasswordListener,
        ChangePasswordFragment.CancelListener,

        ChangeLanguageFragment.UpdateLanguageListener,
        ChangeLanguageFragment.CancelListener{

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
    //static Context homeContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Parsing.setLocale(this,"es");
        setContentView(R.layout.home);

        hostname = getResources().getString(R.string.venya_node_server);
        port = getResources().getString(R.string.venya_node_port);

        // Check if the screen has been rotated (there was an option already selected) and we set it back
        if ( savedInstanceState != null ) {
            currentPosition = savedInstanceState.getInt("position");
            customer = (FullCustomerSettings)savedInstanceState.getParcelable("customer");
            SESSION_ID = savedInstanceState.getString("sessionid");
            setActionBarTitle(currentPosition);
        } else {

            Log.d("HOME", "Getting Extras");
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

        //homeContext = getApplicationContext();
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
            Log.d("HOME","Failed To Parse " + failed);
            toast.makeText(this,"Failed To Parse " + failed,Toast.LENGTH_LONG).show();
        } else {
            //TextView errorsView = (TextView)findViewById(R.id.homeErrorsView);
            //errorsView.setText(SESSION_ID);
            //Log.d("HOME","Home activity started with sessionid = " + SESSION_ID);
            //Log.d("HOME","customer: " + (String)customer.getFieldElement("firstname","value") + " " + (String)customer.getFieldElement("surname","value"));
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
                toast.makeText(this,getResources().getString(R.string.errors_pagenotavailable).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 2:
                // notifications
                toast.makeText(this,getResources().getString(R.string.errors_pagenotavailable).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 3:
                // settings
                toast.makeText(this,getResources().getString(R.string.menu_settings).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new SettingsFragment(SESSION_ID, customer);
                break;
            case 4:
                // logout
                logout(Home.this,customer);
                break;
            case 5:
                // change address
                toast.makeText(this,getResources().getString(R.string.menu_changeaddress).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeAddressFragment(customer);
                break;
            case 6:
                // change email
                toast.makeText(this,getResources().getString(R.string.menu_changeemail).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeEmailFragment(customer);
                break;
            case 7:
                // change usernamge
                toast.makeText(this,getResources().getString(R.string.menu_changeusername).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeUsernameFragment(customer);
                break;
            case 8:
                // change password
                toast.makeText(this,getResources().getString(R.string.menu_changepassword).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangePasswordFragment(customer);
                break;
            case 9:
                // change language
                toast.makeText(this,getResources().getString(R.string.menu_changelanguage).toUpperCase(),Toast.LENGTH_SHORT).show();
                fragment = new ChangeLanguageFragment(customer);
                break;
            default:
                // home
                toast.makeText(this,getResources().getString(R.string.home_welcome),Toast.LENGTH_LONG).show();
                fragment = new HomeFragment(SESSION_ID, customer);
        }

        if ( fragment != null ) {
            goToFragment(fragment,position);
        }
    }

    public void logout(Context intentContext, FullCustomerSettings customer) {
        Toast toast = new Toast(this);
        toast.makeText(this,getResources().getString(R.string.goodbye).toUpperCase(),Toast.LENGTH_SHORT).show();
        String newSessionid = Parsing.setSessionId(getApplicationContext(),(String)customer.getFieldElement("id","value"),getResources().getString(R.string.sessionclosed),"customer");
        if ( newSessionid.equals(getResources().getString(R.string.sessionclosed)) ) {
            Log.d("HOME","session closed");
            Intent intent = new Intent(intentContext, MainActivity.class);
            intentContext.startActivity(intent);
        } else {
            Log.e("HOME.logout","Failed to close session. Sending to logout with error message");
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
                changeLanguageClicked(customer);
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
        goToFragment(new ChangeLanguageFragment(customer),Parsing.getIndexOf(menuOptionsTags,"changelanguage"));
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

    public void updateLanguageClicked(FullCustomerSettings customer) {
        appLanguage = (String)customer.getLanguage().getValue();

        Parsing.setLocale(this,locale_from_language.get((String)customer.getLanguage().getValue()));

        invalidateOptionsMenu();
        menuOptions = getResources().getStringArray(R.array.menuOptions);
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,menuOptions);
        menuList.setAdapter(menuAdapter);

        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    public void updateBooleanClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }

    // Listener for the cancel button in update settings pages
    public void cancelClicked(FullCustomerSettings customer) {
        goToFragment(new SettingsFragment(SESSION_ID,customer),Parsing.getIndexOf(menuOptionsTags,"settings"));
    }


/*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrapContext(newBase,appLanguage));
    }
    */
}
