package dev.nohasmith.venya_android_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import static android.app.PendingIntent.getActivity;

/**
 * Created by arturo on 07/03/2017.
 */
public class Home extends AppCompatActivity{
    public String SESSION_ID = "closed";
    public FullCustomerSettings customer;
    private static String hostname;
    private static String port;
    ActionBar actionBar;
    private String [] menuOptions;
    private ListView menuList;
    private int currentPosition = 0;
    ActionBarDrawerToggle menuToggle;
    private DrawerLayout menuLayout;
    static Context homeContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        hostname = getResources().getString(R.string.venya_node_server);
        port = getResources().getString(R.string.venya_node_port);

        Toolbar toolbar = (Toolbar)findViewById(R.id.venya_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Create menu based on the array from strings
        menuOptions = getResources().getStringArray(R.array.menuOptions);
        menuList = (ListView)findViewById(R.id.expanded_menu);
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,menuOptions);
        menuList.setAdapter(menuAdapter);

        menuList.setOnItemClickListener(new MenuItemClickListener());
        menuLayout = (DrawerLayout)findViewById(R.id.menuLayout);

        homeContext = getApplicationContext();
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

            selectItem(currentPosition);
        }

    }

    private void setActionBarTitle(int position) {
        String option = menuOptions[position];
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
        String newSessionid = Parsing.setSessionId(getApplicationContext(),(String)customer.getFieldElement("id","value"),homeContext.getResources().getString(R.string.sessionclosed),"customer");
        if ( newSessionid.equals(homeContext.getResources().getString(R.string.sessionclosed)) ) {
            Log.d("HOME","session closed");
            Intent intent = new Intent(intentContext, MainActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( menuToggle.onOptionsItemSelected(item) ) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.options_menu_settings:
                // actions
                Fragment fragment = new SettingsFragment(SESSION_ID,customer);
                goToFragment(fragment,3);
                return true;
            case R.id.options_menu_logout:
                // logout
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
}
