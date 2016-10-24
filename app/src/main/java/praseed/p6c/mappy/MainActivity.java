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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private boolean trackStatus = false;
    private LocationDaemon mLocationDaemon;
    private LocationObj mLocationObj;


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

        mLocationDaemon = new LocationDaemon(this,TAG) {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MainActivity.this, "CHANGED", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onLocationChanged: ");
                showLocation();
                trackStatus = false;
                mLocationDaemon.stopLocationUpdates();
            }
        };
        mLocationDaemon.connect();

        bgetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                track();
            }
        });
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void track() {
        if(!trackStatus){
            trackStatus = true;
            Log.d(TAG, "track: Starting");
            mLocationDaemon.startLocationUpdates();

        }
        else {
            Log.d(TAG, "track: Stopping");
            trackStatus = false;
            mLocationDaemon.stopLocationUpdates();
        }
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
        mLocationObj = mLocationDaemon.getLocation();
        if (mLocationObj != null) {
            double latitude = mLocationObj.getmLatitude();
            double longitude = mLocationObj.getmLongitude();
            float accuracy = mLocationObj.getmAccuracy();
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
            trackStatus = false;

        } else {
            Toast.makeText(MainActivity.this, R.string.noLocation_Msg, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        checkLocationAcess();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
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


}
