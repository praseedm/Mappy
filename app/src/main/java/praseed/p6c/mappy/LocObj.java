package praseed.p6c.mappy;

import java.text.DecimalFormat;

/**
 * Created by praseedm on 10/25/2016.
 */

public class LocObj {
    public String name;
    public String id;
    public double mLatitude , mLongitude;
    public int mAccuracy;

    public LocObj() {
    }

    public LocObj(double mLatitude, double mLongitude, int mAccuracy) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAccuracy = mAccuracy;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getmAccuracy() {
        return mAccuracy;
    }

    public void setmAccuracy(int mAccuracy) {
        this.mAccuracy = mAccuracy;
    }
}
