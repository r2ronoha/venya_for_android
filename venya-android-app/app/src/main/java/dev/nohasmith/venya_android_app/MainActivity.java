package dev.nohasmith.venya_android_app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

class MainActivity extends AppCompatActivity implements SigninFragment.SigninListener {
    /*Button submitButton;
    EditText usernameInput;
    EditText passwordInput;
    TextView errorsView;*/
    Context appContext;
    int currentPosition = 0;

    public static String [] customerFields;
    public static String [] privateFields;
    public static String [] booleanFields;
    public static String [] secretFields;
    public static String [] listFields;
    public static String [] nameFields;
    public static String [] addressFields;
    public static String [] statusFields;
    public static String [] customerConstructFields;
    public static String [] upperCaseFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        Parsing.setLocale(appContext,"es");

        customerFields = getResources().getStringArray(R.array.customerFields);
        privateFields = getResources().getStringArray(R.array.privateFields);
        booleanFields = getResources().getStringArray(R.array.booleanFields);
        secretFields = getResources().getStringArray(R.array.secretFields);
        listFields = getResources().getStringArray(R.array.listFields);
        nameFields = getResources().getStringArray(R.array.nameFields);
        addressFields = getResources().getStringArray(R.array.addressFields);
        statusFields = getResources().getStringArray(R.array.statusFields);
        customerConstructFields =  getResources().getStringArray(R.array.customerCunstructFields);
        upperCaseFields =  getResources().getStringArray(R.array.upperCaseFields);

        setFragment(currentPosition);
    }

    private void setFragment(int position) {
        currentPosition = position;
        Fragment fragment = null;
        Toast toast = new Toast(this);

        switch (position) {
            case 1:
                //appointments
                toast.makeText(this,getResources().getString(R.string.action_registration).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 2:
                // notifications
                toast.makeText(this,getResources().getString(R.string.signin_lostusername).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 3:
                // settings
                toast.makeText(this,getResources().getString(R.string.signin_lostpassword).toUpperCase(),Toast.LENGTH_SHORT).show();
                //fragment = new SettingsFragment(SESSION_ID, customer);
                break;
            default:
                // home
                toast.makeText(this,getResources().getString(R.string.signin_button),Toast.LENGTH_LONG).show();
                fragment = new SigninFragment();
        }

        if ( fragment != null ) {
            goToFragment(fragment);
        }
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_layout, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void signinClicked(String sessionid, FullCustomerSettings customer){
        Context intentContext = MainActivity.this;
        Intent intent = new Intent(intentContext, Home.class);
        intent.putExtra("sessionid", sessionid);
        intent.putExtra("customer", customer);
        intentContext.startActivity(intent);
    }
}
