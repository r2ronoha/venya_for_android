package dev.nohasmith.venya_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

class MainActivity extends AppCompatActivity implements LostIdFragment.ToSigninListener, LostIdFragment.LostIdListener, SigninFragment.SigninListener, SigninFragment.ToRegisterListener, RegisterFragment.ToSigninListener, RegisterFragment.GetIdListener, RegisterFragment.RegisterListener{
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
    public static String [] signinOptions;
    public static String [] dateFields;
    public static String [] securityCheckFields;
    public static HashMap<String,String> languages_from_locale;

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
        signinOptions = getResources().getStringArray(R.array.signinOptions);
        dateFields = getResources().getStringArray(R.array.dateFields);
        securityCheckFields = getResources().getStringArray(R.array.securityCheckFields);

        languages_from_locale = new HashMap<String,String>();
        languages_from_locale.put("es","esp");
        languages_from_locale.put("en","esp");

        setFragment(currentPosition);
    }

    private void setFragment(int position) {
        currentPosition = position;
        Fragment fragment = null;
        Toast toast = new Toast(this);

        switch (position) {
            case 1:
                //registration
                toast.makeText(this,getResources().getString(R.string.action_registration).toUpperCase(),Toast.LENGTH_LONG).show();
                fragment = new RegisterFragment();
                break;
            case 2:
                // lost username
                toast.makeText(this,getResources().getString(R.string.signin_lostusername).toUpperCase(),Toast.LENGTH_LONG).show();
                break;
            case 3:
                // lost passwird
                toast.makeText(this,getResources().getString(R.string.signin_lostpassword).toUpperCase(),Toast.LENGTH_SHORT).show();
                //fragment = new SettingsFragment(SESSION_ID, customer);
                break;
            default:
                // default ==> got to signin
                toast.makeText(this,getResources().getString(R.string.signin_button),Toast.LENGTH_LONG).show();
                //fragment = new SigninFragment(getApplicationContext());
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

    public void registerClicked(String sessionid, FullCustomerSettings customer){
        Context intentContext = MainActivity.this;
        Intent intent = new Intent(intentContext, Home.class);
        intent.putExtra("sessionid", sessionid);
        intent.putExtra("customer", customer);
        intentContext.startActivity(intent);
    }

    public void toRegisterClicked(int position) {
        currentPosition = position;
        goToFragment(new RegisterFragment());
    }

    public void toSigninClicked(int position) {
        currentPosition = position;
        goToFragment(new SigninFragment());
    }

    /*
    public void getIdClicked(String url){
        Context intentContext = MainActivity.this;
        Intent browserInternet = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intentContext.startActivity(browserInternet);
    }
    */

    public void getIdClicked(){
        goToFragment(new LostIdFragment());
    }

    public void lostIdClicked(FullCustomerSettings customer) {
        goToFragment(new RegisterFragment(customer));
    }
}
