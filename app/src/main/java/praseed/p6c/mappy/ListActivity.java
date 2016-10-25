package praseed.p6c.mappy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {
    private RecyclerView locRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser mFbUser;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(), locRef = mRootRef.child(Constants.dataRef);
    private MyFireArray locArray;
    private LocationRVadapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("List");
        }
        mAuth = FirebaseAuth.getInstance();
        mFbUser = mAuth.getCurrentUser();
        if (mFbUser == null) {
            finish();
        }
        locRecyclerView = (RecyclerView) findViewById(R.id.locRecyclerView);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setUpLoactionRv();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locArray.cleanup();
    }

    private void setUpLoactionRv() {
        locArray = new MyFireArray(locRef.child(mFbUser.getUid()));
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listAdapter = new LocationRVadapter(locArray) {
            @Override
            protected void populateViewHolder(LocationViewHolder viewHolder, LocObj model, int position) {
                viewHolder.nameView.setText(model.getName());
                viewHolder.latView.setText("Lat : "+model.getmLatitude());
                viewHolder.longView.setText("Long : "+model.getmLongitude());
                viewHolder.accView.setText("Acc : "+ model.getmAccuracy()+" m");
            }
        };
        locRecyclerView.setLayoutManager(mLinearLayoutManager);
        locRecyclerView.setAdapter(listAdapter);
        setFireListener();
    }

    private void setFireListener() {
        locArray.setOnChangedListener(new MyFireArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, String key, int index, int oldIndex) {
                switch (type) {
                    case Added:
                        listAdapter.notifyItemInserted(index);
                        break;
                    case Changed:
                        listAdapter.notifyItemChanged(index);
                        break;
                    case Removed:
                        listAdapter.notifyItemRemoved(index);
                        break;
                    case Moved:
                        listAdapter.notifyItemMoved(oldIndex, index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }
        });
    }

}
