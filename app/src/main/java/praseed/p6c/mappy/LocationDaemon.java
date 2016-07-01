package praseed.p6c.mappy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by praseedm on 7/1/2016.
 */
public  abstract class LocationDaemon implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context context;
    private String TAG;
    private String mUser;
    //Location track
    private Location mLastLocation;
    private String mLastTime;
    private LocationObj mLocationObj;
    private LocationRequest mLocationRequest;
    private GoogleApiClient ApiClient;
    private boolean mTrackStatus = false;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 10 sec
    private static int FATEST_INTERVAL = 3000; // 5 sec
    private static int DISPLACEMENT = 1; // 10 meters

    //Date
    public SimpleDateFormat myDateFormat = new SimpleDateFormat("h:mm:ss a");

    public LocationDaemon(Context context, String TAG) {
        Log.d(TAG, "LocationDaemon: ");
        this.context = context;
        this.TAG = TAG;
        buildGoogleApiClient();
        createLocationRequest();

    }

    //GoogleApi Client
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient: ");
        ApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    //Location Request
    public void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    //GoogleApi connect
    public void connect() {
        ApiClient.connect();
    }

    //GoogleApi disconnect
    public void disconnect() {
        ApiClient.disconnect();
    }

    //Return Location
    public LocationObj getLocation() {
        Log.d(TAG, "getLocation: ");

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(ApiClient);
        mLastTime = myDateFormat.format(new Date());
        mLocationObj = new LocationObj(mLastLocation,mLastTime);
        return mLocationObj;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "LocationDeamon onConnectionSuspended: ");
        ApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Start Request
    public void startLocationUpdates() {
        Log.d(TAG, "LocationDeamon startLocationUpdates: ");

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Toast.makeText(context, "LocarionDaemon UpdateStartED", Toast.LENGTH_SHORT).show();
        mTrackStatus = true;
        if(ApiClient.isConnected()){
            Toast.makeText(context, "LocarionDaemon CoNNected", Toast.LENGTH_SHORT).show();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    ApiClient, mLocationRequest, this);
        }
        else {
            Toast.makeText(context, " LocarionDaemon DisconnectED", Toast.LENGTH_SHORT).show();
        }

    }
    //Stop Request
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                ApiClient, this);
        mTrackStatus = false;
        Toast.makeText(context, "UpdateStoppED", Toast.LENGTH_SHORT).show();
    }

}

