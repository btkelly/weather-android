package classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.macklinu.myweather.R;
import com.macklinu.myweather.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fragments.CityFragment;

/**
 * Created by macklinu on 11/13/13.
 */
public class DownloadWeatherTask extends AsyncTask<String, String, String> {

    private RelativeLayout mainFragmentView;
    private ProgressBar progressBar;
    private TextView unsuccessfulText;
    private int zip;
    private double mLat, mLng;
    private StringBuffer result;
    private HttpURLConnection imageConnection;
    private Weather weather;
    private boolean isSuccessfulRequest;

    // Weather infos
    String city, state, zipCode, tempString, imgUrl;
    Bitmap image;

    // API stuff
    private String BASE_URL = "http://api.wunderground.com/api/";
    private String API_KEY = "0a80de5d54554b74";
    private String MID_URL = "/conditions/q/";
    private String END_URL = ".json";

    private void init(View view) {
        mainFragmentView = (RelativeLayout) view.findViewById(R.id.weatherViewFragment);
        progressBar = (ProgressBar) view.findViewById(R.id.weatherProgressBar);
        unsuccessfulText = (TextView) view.findViewById(R.id.unsuccessfulWeatherText);
    }

    public DownloadWeatherTask(View view, int zip) {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: constructor");
        this.zip = zip;
        init(view);
    }

    public DownloadWeatherTask(View view, double lat, double lng) {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: constructor");
        mLat = lat;
        mLng = lng;
        init(view);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: onPreExecute()");
        mainFragmentView.setVisibility(RelativeLayout.GONE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        unsuccessfulText.setVisibility(TextView.GONE);
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: doInBackground()");
        weather = new Weather();
        ConnectivityManager connMgr = (ConnectivityManager) mainFragmentView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                StringBuffer sb = zip == 0 ? makeWeatherAPICall(mLat, mLng) : makeWeatherAPICall(zip);
                // Log.i(MainActivity.APP_DEBUG, sb.toString());
                try {
                    Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: getting JSON stuff");
                    isSuccessfulRequest = true;
                    JSONObject root = new JSONObject(sb.toString());
                    JSONObject data = root.getJSONObject("current_observation");
                    city = data.getJSONObject("display_location").getString("city");
                    state = data.getJSONObject("display_location").getString("state");
                    zipCode = data.getJSONObject("display_location").getString("zip");
                    tempString = Integer.toString(data.getInt("temp_f"));
                    imgUrl = data.getString("icon_url");
                    weather.setCity(city);
                    weather.setState(state);
                    weather.setZipCode(zipCode);
                    weather.setHigh(tempString);
                    // get image from imgUrl
                    try {
                        // String url1 = "http://<my IP>/test/abc.jpg";
                        URL url = new URL(imgUrl);
                        imageConnection = (HttpURLConnection) url.openConnection();
                        InputStream is = imageConnection.getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        if (bmp != null) {
                            image = bmp;
                            weather.setImage(image);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (imageConnection != null) {
                            imageConnection.disconnect();
                        }
                    }
                    // Log.i(MainActivity.APP_DEBUG, "Got city from JSON: " + city);
                } catch (JSONException e) {
                    Log.i(MainActivity.APP_DEBUG, "City doesn't exist");
                    isSuccessfulRequest = false;
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isSuccessfulRequest = false;
            // no connectivity
        }
        return "Complete";
    }

    /**
     * Make a call to the Weather API and return a StringBuffer on success
     */
    private StringBuffer makeWeatherAPICall(int zip) throws IOException {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: makeWeatherAPICall()");
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

    private StringBuffer makeWeatherAPICall(double lat, double lng) throws IOException {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: makeWeatherAPICall()");
        String apiUrl = BASE_URL + API_KEY + MID_URL + Double.toString(lat) + "," + Double.toString(lng) + END_URL;
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
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: onPostExecute()");
        progressBar.setVisibility(ProgressBar.GONE);
        if (isSuccessfulRequest) {
            MainActivity.addToNavigationDrawer(weather);
            // isFinishing()
            ((TextView) mainFragmentView.findViewById(R.id.weatherCity)).setText(city + ", " + state);
            ((TextView) mainFragmentView.findViewById(R.id.weatherZipCode)).setText(zipCode);
            ((TextView) mainFragmentView.findViewById(R.id.weatherHigh)).setText(tempString);
            ((ImageView) mainFragmentView.findViewById(R.id.weatherImage)).setImageBitmap(image);
            mainFragmentView.setVisibility(RelativeLayout.VISIBLE);
        } else {
            unsuccessfulText.setVisibility(TextView.VISIBLE);
        }
    }
}
