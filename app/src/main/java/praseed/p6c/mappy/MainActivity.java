package praseed.p6c.mappy;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{



    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient ApiClient;
    //UI
    private TextView lat_display,long_display,acc_display;
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
        
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
