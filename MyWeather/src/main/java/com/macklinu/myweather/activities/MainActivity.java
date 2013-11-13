package com.macklinu.myweather.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.macklinu.myweather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import classes.DownloadWeatherTask;
import classes.VerifiedEditText;
import classes.Weather;
import fragments.CityFragment;

public class MainActivity extends Activity {
    public static ArrayList<Weather> mCitiesList;
    public static ArrayAdapter<Weather> mCitiesAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private LocationManager locationManager;
    private String provider;
    private Location location;

    static final int ADD_ZIP_CODE_REQUEST = 100;  // The request code
    static final String ZIP_CODE = "zip_code";
    public static final String APP_DEBUG = "lolsup";

    static final int ZIP_CODE_MIN = 10000;
    static final int ZIP_CODE_MAX = 99999;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCitiesList = new ArrayList<Weather>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mCitiesAdapter = new ArrayAdapter<Weather>(this, R.layout.drawer_list_item, mCitiesList);
        // Set the adapter for the list view
        mDrawerList.setAdapter(mCitiesAdapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawerLayout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Log.i(APP_DEBUG, "Provider " + provider + " has been selected.");
            // onLocationChanged(location);
        } else {
            // nothing now
        }

    }

    @Override
    protected void onStart() {
        Log.i(APP_DEBUG, "MainActivity: onStart()");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(APP_DEBUG, "MainActivity: onPause()");
        // locationManager.removeUpdates(LocationListener l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(APP_DEBUG, "MainActivity: onResume()");
    }

    @Override
    protected void onRestart() {
        Log.i(APP_DEBUG, "MainActivity: onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.i(APP_DEBUG, "MainActivity: onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(APP_DEBUG, "MainActivity: onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(APP_DEBUG, "MainActivity: onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(APP_DEBUG, "MainActivity: onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            // item.
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_get_location:
                // Get GPS coordinates
                // Make request to Weather API
                // makeToast("Get GPS coordinates");
                // onLocationChanged(location);
                makeFragment(location.getLatitude(), location.getLongitude());
                return true;
            case R.id.action_refresh:
                makeToast("Refresh weather");
                // Update current weather settings for current stored location
                return true;
            case R.id.action_settings:
                makeToast("Settings");
                return true;
            case R.id.action_new:
                // makeToast("Add city");
                makeDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // @Override
    public void onLocationChanged(Location location) {

        // makeToast(lat + ", " + lng);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position, String zipCode) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putString(CityFragment.ARG_ZIP_CODE, zipCode);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        // setTitle(mCityTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // String zip = (String) parent.getItemAtPosition(position);
            // selectItem(position, zip);
        }
    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

        public CustomActionBarDrawerToggle(Activity mActivity,DrawerLayout mDrawerLayout){
            super(
                    mActivity,               /* host Activity */
                    mDrawerLayout,           /* DrawerLayout object */
                    R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open,   /* "open drawer" description */
                    R.string.drawer_close); /* "close drawer" description */
        }


        /** Called when a drawer has settled in a completely closed state. */
        @Override
        public void onDrawerClosed(View view) {
            invalidateOptionsMenu();
            // getActionBar().setTitle(mTitle);
        }

        /** Called when a drawer has settled in a completely open state. */
        @Override
        public void onDrawerOpened(View drawerView) {
            invalidateOptionsMenu();
            // getActionBar().setTitle(getString(R.string.nav_menu_open));
        }
    }

    /** Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_new).setVisible(true);
        menu.findItem(R.id.action_get_location).setVisible(!drawerOpen);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public void makeToast(CharSequence cs) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, cs, duration).show();
    }

    private void makeDialog() {
        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // create dialog text input
        final VerifiedEditText input = new VerifiedEditText(this);
        // set title
        alertDialogBuilder.setTitle("Zip Code");
        // set dialog message
        alertDialogBuilder
                .setMessage("Enter 5-digit zip code")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int zip = input.getText().toString().equalsIgnoreCase("") ? 0 : Integer.parseInt(input.getText().toString().trim());
                        if (zip >= ZIP_CODE_MIN && zip <= ZIP_CODE_MAX) {
                            // mCitiesList.add(Integer.toString(zip));
                            // mCitiesAdapter.notifyDataSetChanged();
                            // start background weather task
                            // new DownloadWeatherTask(zip).execute("");
                            makeFragment(zip);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Need complete zip code", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void makeFragment(int zip) {
        // Create a new fragment and specify the zip code from the dialog
        Fragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putInt(CityFragment.ARG_ZIP_CODE, zip);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void makeFragment(double lat, double lng) {
        // Create a new fragment and specify the zip code from the dialog
        Fragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putDouble(CityFragment.ARG_LAT, lat);
        args.putDouble(CityFragment.ARG_LNG, lng);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    public static void addToNavigationDrawer(Weather weather) {
        mCitiesList.add(weather);
        mCitiesAdapter.notifyDataSetChanged();
    }
}

