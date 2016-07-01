package praseed.p6c.mappy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener{

    private String TAG = "MainActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean status = false;
    // Google client to interact with Google API
    private GoogleApiClient ApiClient;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    //Check Location Acess
    LocationManager lm;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    //UI
    private TextView lat_display, long_display, acc_display;
    private Button bgetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat_display = (TextView) findViewById(R.id.lat_display);
        long_display = (TextView) findViewById(R.id.long_display);
        acc_display = (TextView) findViewById(R.id.acc_display);
        bgetLocation = (Button) findViewById(R.id.btnShowLocation);

        //check playService
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        } else {
            Log.d(TAG, "onCreate: noPlayServices");
        }

        bgetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");
                if(ApiClient.isConnected() ) {
                    startLocationUpdates();
                }
                else
                {
                    Log.d(TAG, "onClick: else");
                    ApiClient.connect();
                }
                
            }
        });

    }


    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "startLocationUpdates: ");
        if(ApiClient.isConnected()){
            status = true;
            Log.d(TAG, "startLocationUpdates: connected");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                ApiClient, mLocationRequest, (LocationListener) this);
    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: ");
        status = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                ApiClient, (LocationListener) this);
    }

    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "showLocation: Nopermisiion");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(ApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            float accuracy = mLastLocation.getAccuracy();
            Log.d(TAG, "showLocation: Acc :"+accuracy);
            if(accuracy > 1500){
                Log.d(TAG, "showLocation: accuracy > 1500 : ");
                Toast.makeText(MainActivity.this, R.string.poorAccuracy, Toast.LENGTH_SHORT).show();
               // return;
            }
            else if(accuracy > 500){
                Log.d(TAG, "showLocation: LowAccu");
                Toast.makeText(MainActivity.this, R.string.lowAccuracy, Toast.LENGTH_SHORT).show();
            }
            lat_display.setText("Lat : "+latitude);
            long_display.setText("Long : "+longitude);
            acc_display.setText(accuracy+" m");
            status = false;

        } else {
            Toast.makeText(MainActivity.this, R.string.noLocation_Msg, Toast.LENGTH_SHORT).show();
        }
    }

    //GoogleApi Client
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient: ");
        ApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if (ApiClient != null) {
            ApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        checkLocationAcess();
        if(ApiClient.isConnected() && !status) {
            status = true;
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (ApiClient != null) {
            Log.d(TAG, "onStop: API disconnect");
            ApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "API onConnected: ");
        showLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "API onConnectionSuspended: ");
        ApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "API onConnectionFailed: ");
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!ApiClient.isConnecting() &&
                        !ApiClient.isConnected()) {
                    ApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Function to checkLocationAcess Location Acess
    //Toast when GPS disabled , AlertDialog when NetworkProvider disabled .
    protected void checkLocationAcess(){
        Log.d(TAG, "checkLocationAcess: ");
        Context context = this;
        lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_enabled){
            Log.d(TAG, "checkLocationAcess: gps disabled ");
            Toast.makeText(context,"Enable GPS for best result",Toast.LENGTH_LONG).show();
        }
        if(gps_enabled){
            Log.d(TAG, "checkLocationAcess: gps Enabled ");
        }
        if(network_enabled)
        {Log.d(TAG, "checkLocationAcess: network Enabled ");}

        if(!network_enabled) {
            Log.d(TAG, "checkLocationAcess: network disabled ");
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Location Acess Disabled");
            dialog.setMessage("Enable Location Acess");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.create().show();
            return;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        showLocation();
    }
}
