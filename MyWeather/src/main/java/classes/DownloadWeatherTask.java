package classes;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * Created by Macklin Underdown on 11/13/13.
 * GitHub: @macklinu
 */
public class DownloadWeatherTask extends AsyncTask<String, String, Weather> {

    private Integer mZipCode;
    private Double mLat, mLng;
    private StringBuffer result;
    private HttpURLConnection imageConnection;
    private Weather weather;
    private boolean isSuccessfulRequest;

    private WeatherDownloadListener listener;

    // Weather info
    String city, state, zipCode, tempString, imgUrl;
    Bitmap image;

    public DownloadWeatherTask(WeatherDownloadListener listener, Integer zip, Double lat, Double lng) {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: constructor");
        this.listener = listener;
        mZipCode = zip;
        mLat = lat;
        mLng = lng;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: onPreExecute()");
        listener.showProgressBar();
    }

    @Override
    protected Weather doInBackground(String... params) {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: doInBackground()");
        weather = new Weather();
        try {
            String apiUrl = listener.getWeatherURL();
            StringBuffer sb = makeWeatherAPICall(apiUrl);
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

        return weather;
    }

    /**
     * Make a call to the Weather API and return a StringBuffer on success
     */
    private StringBuffer makeWeatherAPICall(String apiUrl) throws IOException {
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: makeWeatherAPICall()");
        Log.i(MainActivity.APP_DEBUG, "URL: " + apiUrl);
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
    protected void onPostExecute(Weather weather) {
        super.onPostExecute(weather);
        Log.i(MainActivity.APP_DEBUG, "DownloadWeatherTask: onPostExecute()");
        if (isSuccessfulRequest) listener.weatherReady(weather);
        else listener.weatherFailed();
    }
}
