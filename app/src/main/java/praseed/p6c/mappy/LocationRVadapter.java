package praseed.p6c.mappy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by praseedm on 10/25/2016.
 */

public abstract class LocationRVadapter extends RecyclerView.Adapter<LocationViewHolder> {
    private MyFireArray mDataSet;
    protected int mModelLayout = R.layout.locationitem;

    public LocationRVadapter( MyFireArray mDataSet) {
        this.mDataSet = mDataSet;
    }


    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        LocObj mObj = getItem(position);
        populateViewHolder(holder, mObj, position);
    }

    public String getItemKey(int position) {
        return mDataSet.getItem(position).getKey();
    }

    @Override
    public int getItemCount() {
        return mDataSet.getCount();
    }

    public LocObj getItem(int position) {
        return parseSnapshot(mDataSet.getItem(position));
    }

    protected LocObj parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(LocObj.class);
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }


    protected abstract void populateViewHolder(LocationViewHolder viewHolder, LocObj model, int position);
}
