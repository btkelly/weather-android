package classes;

import android.graphics.Bitmap;

/**
 * Created by macklinu on 11/12/13.
 */
public class Weather {
    public String mCity;
    public String mState;
    public Bitmap mImage;
    public String mZipCode;
    public String mHigh;

    public Weather() {}

    @Override
    public String toString() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setState(String state) {
        mState = state;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }

    public void setHigh(String high) {
        mHigh = high;
    }
    
    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public String getHigh() {
        return mHigh;
    }

    public Bitmap getImage() {
        return mImage;
    }
}
