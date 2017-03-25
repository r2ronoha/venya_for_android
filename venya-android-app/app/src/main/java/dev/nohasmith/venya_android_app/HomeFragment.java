package dev.nohasmith.venya_android_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import static android.R.attr.left;
import static dev.nohasmith.venya_android_app.MainActivity.appLanguage;
import static dev.nohasmith.venya_android_app.MainActivity.booleanFields;
import static dev.nohasmith.venya_android_app.MainActivity.customerFields;
import static dev.nohasmith.venya_android_app.MainActivity.dateFields;
import static dev.nohasmith.venya_android_app.MainActivity.homeFields;
import static dev.nohasmith.venya_android_app.MainActivity.privateFields;
import static dev.nohasmith.venya_android_app.MainActivity.providerFields;
import static dev.nohasmith.venya_android_app.MainActivity.secretFields;
import static dev.nohasmith.venya_android_app.MainActivity.venyaUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FullCustomerSettings customer;
    String sessionid;
    private final String TAG = this.getClass().getSimpleName();

    public HomeFragment() {}

    /*
    public HomeFragment(String sessionid, FullCustomerSettings customer) {
        this.customer = customer;
        this.sessionid = sessionid;
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        TableLayout tableLayout = (TableLayout)view.findViewById(R.id.customer_table);
        TableRow row;
        TableRow.LayoutParams layoutParams;
        TextView fieldCell;
        View valueCell;
        TextView textCell;
        ImageView imageCell;
        int rowCount = 0;

        try {
            sessionid = getArguments().getString("sessionid");
            customer = (FullCustomerSettings)getArguments().getParcelable("customer");
        } catch (Exception e) {
            Log.e(TAG + ".onStart","Failed to get arguments");
            e.printStackTrace();
        }

        if ( customer instanceof FullCustomerSettings ) {
            for ( int i=0; i<homeFields.length; i++ ) {
                String field = homeFields[i];
                if ( customer.getField(field) != null && ! Arrays.asList(privateFields).contains(field) ) {
                    row = new TableRow(getContext());
                    layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    row.setLayoutParams(layoutParams);

                    fieldCell = new TextView(getContext());
                    TableRow.LayoutParams fieldLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    fieldLP.width = 0;
                    fieldLP.weight = 3;
                    fieldCell.setLayoutParams(fieldLP);

                    int langFieldId = Parsing.getResId(getContext(),"customer_" + field);
                    fieldCell.setText(Parsing.formatMessage(new String [] {getResources().getString(langFieldId)}));
                    fieldCell.setPadding(10,10,10,10);
                    fieldCell.setGravity(Gravity.END);
                    row.addView(fieldCell);

                    CustomerField fullField = customer.getField(field);
                    TableRow.LayoutParams valueLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    valueLP.width = 0;
                    valueLP.weight = 2;
                    valueCell = new View(getContext());
                    textCell = new TextView(getContext());
                    imageCell = new ImageView(getContext());
                    if ( field.equals("providers") ) {
                        // display the name of the active providers
                        HashMap<String, Provider> providers = (HashMap<String,Provider>)fullField.getValue();

                        TableLayout providersTable = new TableLayout(getContext());
                        TableRow.LayoutParams providersParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        int provCount = 0;

                        for ( String providerid : providers.keySet() ) {
                            if ( providers.get(providerid).isActive() ){
                                Provider providerDetails = Parsing.getProviderDetails(getContext(),providerid);

                                TableRow.LayoutParams provLP = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                                provLP.width = 0;
                                provLP.weight = 1;
                                provLP.setMargins(0,0,0,0);

                                TableRow provRow = new TableRow(getContext());
                                TextView provView = new TextView(getContext());
                                String providerName = providerDetails.getName();
                                provView.setText(Parsing.formatName(providerName));
                                provView.setLayoutParams(provLP);
                                provView.setPadding(5,5,5,5);
                                provView.setGravity(Gravity.END);
                                provRow.addView(provView);
                                providersTable.addView(provRow,provCount++);

                            }
                        }
                        row.addView(providersTable);
                    }else {
                        if (field.equals("address")) {
                            // iterate through address fields
                            Address address = (Address) fullField.getValue();
                            String addrStr = address.formatAddress();
                            textCell.setText(addrStr);
                        } else if (Arrays.asList(booleanFields).contains(field)) {
                            boolean value = (boolean) fullField.getValue();
                            textCell.setText(Parsing.getBooleanValue(value));
                        } else if (Arrays.asList(dateFields).contains(field)) {
                            String date = Parsing.formatDate((String) fullField.getValue());
                            textCell.setText(date);
                        } else if (field.equals("language")) {
                            String lang = (String) fullField.getValue();
                            imageCell.setImageResource(Parsing.getResId(getContext(), lang, "drawable"));
                            imageCell.setForegroundGravity(Gravity.END);
                        } else {
                            String value = Parsing.formatName((String) fullField.getValue());
                            if (Arrays.asList(secretFields).contains(field)) {
                                value = Parsing.hideValue(value);
                            }
                            textCell.setText(value);
                        }
                        textCell.setGravity(Gravity.END);
                        valueCell.setLayoutParams(valueLP);
                        valueCell = (field.equals("language")) ? imageCell : textCell;
                        valueCell.setPadding(10, 10, 10, 10);
                        //valueCell.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        row.addView(valueCell);
                    }
                    tableLayout.addView(row, rowCount++);
                }
            }


            Fragment fragment = new AppointmentsFragment();
            Bundle args = fragment.getArguments();
            if ( args == null ) { args = new Bundle(); }
            args.putParcelable("customer",customer);
            args.putString("title",getResources().getString(R.string.title_nextappointments));
            args.putInt("numberofappointments",4); // Limit the number of appointments displayed in home page to next 4
            fragment.setArguments(args);

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.appointments_table,fragment,"homeAppointmentsFragment");
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();


            /*
            CaldroidFragment caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar calendar = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.appointments_table, caldroidFragment);
            ft.commit();
            */

        }
    }

}
