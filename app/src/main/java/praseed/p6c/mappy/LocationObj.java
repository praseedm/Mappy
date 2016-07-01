package praseed.p6c.mappy;

import android.location.Location;

/**
 * Created by praseedm on 7/1/2016.
 */
public class LocationObj {
    public String mLastTime;
    public double mLatitude , mLongitude;
    public float mAccuracy;

    public LocationObj() {
    }

    public LocationObj(Location mLastLocation, String mLastTime) {
        this.mLastTime = mLastTime;
        this.mLatitude = mLastLocation.getLatitude();
        this.mLongitude = mLastLocation.getLongitude();
        this.mAccuracy = mLastLocation.getAccuracy();
    }

    public float getmAccuracy() {
        return mAccuracy;
    }

    public void setmAccuracy(float mAccuracy) {
        this.mAccuracy = mAccuracy;
    }

    public String getmLastTime() {
        return mLastTime;
    }

    public void setmLastTime(String mLastTime) {
        this.mLastTime = mLastTime;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
