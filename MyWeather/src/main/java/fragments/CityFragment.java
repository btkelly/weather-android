package fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.macklinu.myweather.R;
import com.macklinu.myweather.activities.MainActivity;

import classes.DownloadWeatherTask;

/**
 * Created by macklinu on 11/11/13.
 */
public class CityFragment extends Fragment {
    // public static final String ARG_CITY_NUMBER = "city_number";
    public static final String ARG_ZIP_CODE = "zip_code";
    public static final String ARG_LAT = "lat";
    public static final String ARG_LNG = "lng";
    public static final String ARG_CITY = "city";
    public static final String ARG_STATE = "state";
    public static final String ARG_TEMPERATURE = "temperature";
    private int mZipCode;
    private double mLat, mLng;
    private String mCity;
    private String mState;
    private String mTemperature;
    private boolean makeNetworkRequest;
    private Activity main;

    public CityFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static CityFragment createCityFragment() {
        return new CityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onCreate()");
        mZipCode = getArguments().containsKey(ARG_ZIP_CODE) ? getArguments().getInt(ARG_ZIP_CODE) : 0;

        if (getArguments().containsKey(ARG_LAT)) {
            mLat = getArguments().getDouble(ARG_LAT);
            mLng = getArguments().getDouble(ARG_LNG);
        } else {
            mLat = 0;
            mLng = 0;
        }

        /*
        mCity = getArguments().getString(ARG_CITY);
        mState = getArguments().getString(ARG_STATE);
        mTemperature = getArguments().getString(ARG_TEMPERATURE);
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        TextView weatherZipCode = (TextView) rootView.findViewById(R.id.weatherZipCode);
        weatherZipCode.setText(Integer.toString(mZipCode));

        // ((TextView) rootView.findViewById(R.id.weatherCity)).setText(mCity + ", " + mState);
        // ((TextView) rootView.findViewById(R.id.weatherHigh)).setText(mTemperature);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(MainActivity.APP_DEBUG, "CityFragment: onViewCreated()");
        if (mZipCode == 0) {
            new DownloadWeatherTask(view, mLat, mLng).execute("");
        } else {
            new DownloadWeatherTask(view, mZipCode).execute("");
        }
    }
}