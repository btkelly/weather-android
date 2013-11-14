package classes;

/**
 * Created by Macklin Underdown on 11/13/13.
 * GitHub: @macklinu
 */
public interface WeatherDownloadListener {

    public void showProgressBar();
    public void weatherReady(Weather weather);
    public void weatherFailed();
    public String getWeatherURL();

}
