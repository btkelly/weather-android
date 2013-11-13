package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.macklinu.myweather.R;

/**
 * Created by macklinu on 11/11/13.
 */
public class CityFragment extends Fragment {
    // public static final String ARG_CITY_NUMBER = "city_number";
    public static final String ARG_ZIP_CODE = "zip_code";
    public static final String ARG_CITY = "city";
    public static final String ARG_STATE = "state";
    public static final String ARG_TEMPERATURE = "temperature";
    private int mZipCode;
    private String mCity;
    private String mState;
    private String mTemperature;
    private boolean makeNetworkRequest;

    public CityFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mZipCode = getArguments().getInt(ARG_ZIP_CODE);
        mCity = getArguments().getString(ARG_CITY);
        mState = getArguments().getString(ARG_STATE);
        mTemperature = getArguments().getString(ARG_TEMPERATURE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        // int i = getArguments().getInt(ARG_CITY_NUMBER);
        // String city = getResources().getStringArray(R.array.cities_array)[i];
        // progressBar = (ProgressBar) rootView.findViewById(R.id.weatherProgressBar);
        // progressBar.setVisibility(ProgressBar.VISIBLE);
        TextView weatherZipCode = (TextView) rootView.findViewById(R.id.weatherZipCode);
        weatherZipCode.setText(Integer.toString(mZipCode));

        ((TextView) rootView.findViewById(R.id.weatherCity)).setText(mCity + ", " + mState);
        ((TextView) rootView.findViewById(R.id.weatherHigh)).setText(mTemperature);

        return rootView;
    }
}