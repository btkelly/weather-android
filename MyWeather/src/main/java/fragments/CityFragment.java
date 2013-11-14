package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.macklinu.myweather.R;
import com.macklinu.myweather.activities.MainActivity;

import classes.DownloadWeatherTask;
import classes.Weather;
import classes.WeatherDownloadListener;

/**
 * Created by Macklin Underdown on 11/11/13.
 * GitHub: @macklinu
 */
public class CityFragment extends Fragment implements WeatherDownloadListener {
    // public static final String ARG_CITY_NUMBER = "city_number";
    public static final String ARG_ZIP_CODE = "zip_code";
    public static final String ARG_LAT = "lat";
    public static final String ARG_LNG = "lng";
    public static final String ARG_CITY = "city";
    public static final String ARG_STATE = "state";
    public static final String ARG_TEMPERATURE = "temperature";
    private Integer mZipCode;
    private Double mLat, mLng;
    private String mCity, mState, mHigh;

    private Weather mWeather = new Weather();
    private Weather tempWeather;

    // Views, IDs
    private ProgressBar mProgressBarView;
    private TextView mCityTextView;
    private TextView mZipCodeTextView;
    private TextView mHighTextView;
    private TextView mErrorTextView;
    private RelativeLayout mContentFragmentView;
    private ImageView mWeatherImageView;

    // API stuff
    private String BASE_URL = "http://api.wunderground.com/api/";
    private String API_KEY = "0a80de5d54554b74";
    private String MID_URL = "/conditions/q/";
    private String END_URL = ".json";

    public CityFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onCreate()");
        // Log.i(MainActivity.APP_DEBUG, "What is mWeather???: " + mWeather.toString());
        // set zipCode and lat/lng variables
        mZipCode = getArguments().containsKey(ARG_ZIP_CODE) ? getArguments().getInt(ARG_ZIP_CODE) : null;
        mLat = getArguments().containsKey(ARG_LAT) ? getArguments().getDouble(ARG_LAT) : null;
        mLng = getArguments().containsKey(ARG_LNG) ? getArguments().getDouble(ARG_LNG) : null;
        mCity = getArguments().containsKey(ARG_CITY) ? getArguments().getString(ARG_CITY) : null;
        mState = getArguments().containsKey(ARG_STATE) ? getArguments().getString(ARG_STATE) : null;
        mHigh = getArguments().containsKey(ARG_TEMPERATURE) ? getArguments().getString(ARG_TEMPERATURE) : null;
        if (mZipCode != null &&
                mCity != null &&
                mState != null &&
                mHigh != null) {
            Log.i(MainActivity.APP_DEBUG, "** tempWeather is being created. **");
            tempWeather = new Weather();
            tempWeather.setCity(mCity);
            tempWeather.setState(mState);
            tempWeather.setZipCode(Integer.toString(mZipCode));
            tempWeather.setHigh(mHigh);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(MainActivity.APP_DEBUG, "CityFragment: create the options menu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.city, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mWeather.isNull()) {
                    new DownloadWeatherTask(this, mZipCode, mLat, mLng).execute("");
                } else {
                    Log.i(MainActivity.APP_DEBUG, "Using local weather object");
                    setWeatherViews(mWeather);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        // define the views
        mProgressBarView = (ProgressBar) rootView.findViewById(R.id.weatherProgressBar);
        mCityTextView = (TextView) rootView.findViewById(R.id.weatherCity);
        mContentFragmentView = (RelativeLayout) rootView.findViewById(R.id.weatherViewFragment);
        mWeatherImageView = (ImageView) rootView.findViewById(R.id.weatherImage);
        mZipCodeTextView = (TextView) rootView.findViewById(R.id.weatherZipCode);
        mHighTextView = (TextView) rootView.findViewById(R.id.weatherHigh);
        mErrorTextView = (TextView) rootView.findViewById(R.id.unsuccessfulWeatherText);

        if (tempWeather != null) {
            Log.i(MainActivity.APP_DEBUG, "** tempWeather is not null, setWeatherViews(tempWeather) **");
            setWeatherViews(tempWeather);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onViewCreated()");
        // make network call
        if (mWeather.isNull()) {
            Log.i(MainActivity.APP_DEBUG, "** mWeather is null, make network call **");
            new DownloadWeatherTask(this, mZipCode, mLat, mLng).execute("");
        } else {
            Log.i(MainActivity.APP_DEBUG, "** mWeather exists, setWeatherViews() locally **");
            setWeatherViews(mWeather);
        }
    }

    @Override
    public void showProgressBar() {
        mContentFragmentView.setVisibility(RelativeLayout.GONE);
        mErrorTextView.setVisibility(TextView.GONE);
        mProgressBarView.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void weatherReady(Weather weather) {
        // create a global instance of weather that can be used again in the fragment
        mWeather = weather;
        Log.i(MainActivity.APP_DEBUG, "SET mWeather: " + mWeather.toString());
        MainActivity.addToNavigationDrawer(weather);
        mProgressBarView.setVisibility(ProgressBar.GONE);
        setWeatherViews(weather);
        mContentFragmentView.setVisibility(RelativeLayout.VISIBLE);
    }

    public void setWeatherViews(Weather weather) {
        mCityTextView.setText(weather.getCity() + ", " + weather.getState());
        mZipCodeTextView.setText(weather.getZipCode());
        mHighTextView.setText(weather.getHigh());
        mWeatherImageView.setImageBitmap(weather.getImage());
    }

    @Override
    public void weatherFailed() {
        mProgressBarView.setVisibility(ProgressBar.GONE);
        mErrorTextView.setVisibility(TextView.VISIBLE);
    }

    @Override
    public String getWeatherURL() {
        String MAIN_URL = BASE_URL + API_KEY + MID_URL;
        if (mZipCode != null) {
            return MAIN_URL + Integer.toString(mZipCode) + END_URL;
        } else {
            return MAIN_URL + Double.toString(mLat) + "," + Double.toString(mLng) + END_URL;
        }
    }
}