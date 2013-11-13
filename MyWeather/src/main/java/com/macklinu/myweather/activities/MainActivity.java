package com.macklinu.myweather.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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

import classes.VerifiedEditText;
import fragments.CityFragment;

public class MainActivity extends Activity {
    private ArrayList<String> mCitiesList;
    private ArrayAdapter<String> mCitiesAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    static final int ADD_ZIP_CODE_REQUEST = 100;  // The request code
    static final String ZIP_CODE = "zip_code";
    static final String APP_DEBUG = "lolsup";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCitiesList = new ArrayList<String>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mCitiesAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mCitiesList);
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
                makeToast("Get GPS coordinates");
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

    /** Swaps fragments in the main content view */
    private void selectItem(int position, String zipCode) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new CityFragment();
        Bundle args = new Bundle();
        // args.putInt(CityFragment.ARG_CITY_NUMBER, position);
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
            String zip = (String) parent.getItemAtPosition(position);
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
                        int value = Integer.parseInt(input.getText().toString().trim());
                        if (value >= 10000 && value <= 99999) {
                            mCitiesList.add(Integer.toString(value));
                            mCitiesAdapter.notifyDataSetChanged();
                            // Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
                            // makeFragment(value);
                            new DownloadWeatherTask(value).execute("");
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

    public void makeFragment(int zip, String city, String state, String temp) {
        // Create a new fragment and specify the zip code from the dialog
        Fragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putInt(CityFragment.ARG_ZIP_CODE, zip);
        args.putString(CityFragment.ARG_CITY, city);
        args.putString(CityFragment.ARG_STATE, state);
        args.putString(CityFragment.ARG_TEMPERATURE, temp);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private class DownloadWeatherTask extends AsyncTask<String, String, String> {

        int count = 0;

        private ProgressBar progressBar;
        private Fragment fragment;
        private int zip;
        private StringBuffer result;

        // Weather infos
        String city, state, tempString;

        // API stuff
        private String BASE_URL = "http://api.wunderground.com/api/";
        private String API_KEY = "0a80de5d54554b74";
        private String MID_URL = "/conditions/q/";
        private String END_URL = ".json";

        DownloadWeatherTask(int i) {
            // this.fragment = fragment;
            zip = i;
            progressBar = (ProgressBar) findViewById(R.id.weatherProgressBar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    StringBuffer sb = makeWeatherAPICall(zip);
                    Log.i(MainActivity.APP_DEBUG, sb.toString());
                    try {
                        JSONObject root = new JSONObject(sb.toString());
                        JSONObject data = root.getJSONObject("current_observation");
                        city = data.getJSONObject("display_location").getString("city");
                        state = data.getJSONObject("display_location").getString("state");
                        tempString = data.getString("temperature_string");
                        Log.i(MainActivity.APP_DEBUG, city);
                    } catch (JSONException e) {
                        Log.i(MainActivity.APP_DEBUG, "City doesn't exist");
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // no connectivity
            }
            return "Complete";
        }

        /** Make a call to the Wunderground API and return a StringBuffer on success*/
        private StringBuffer makeWeatherAPICall(int zip) throws IOException {
            String apiUrl = BASE_URL + API_KEY + MID_URL + Integer.toString(zip) + END_URL;
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                result = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            progressBar.setVisibility(ProgressBar.GONE);
            MainActivity.this.makeFragment(zip, city, state, tempString);
        }
    }
}

