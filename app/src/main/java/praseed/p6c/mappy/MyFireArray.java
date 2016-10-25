package praseed.p6c.mappy;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MyFireArray implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved }
        void onChanged(EventType type, String key ,int index, int oldIndex);
    }

    private Query mQuery;
    private OnChangedListener mListener;
    public ArrayList<DataSnapshot> mSnapshots;

    public MyFireArray(Query ref) {
        mSnapshots = new ArrayList<DataSnapshot>();
        UpdateRef(ref);
    }

    public MyFireArray(){
        mSnapshots = new ArrayList<DataSnapshot>();
    }

    public void spClean(){
        if(mQuery != null){ mQuery.removeEventListener(this);}
        mSnapshots.clear();
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
        mSnapshots.clear();
    }

    public void UpdateRef(Query ref){
        if(mQuery != null){
            cleanup();
        }
        mQuery = ref;
        mQuery.addChildEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();

    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    // Start of ChildEventListener methods
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        String key = snapshot.getKey();
        int index = 0;
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }
        mSnapshots.add(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Added, key, index);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        String key = snapshot.getKey();
        int index = getIndexForKey(key);
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Changed, key, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        String key  = snapshot.getKey();
        int index = getIndexForKey(key);
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.Removed, key, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        String key = snapshot.getKey();
        int oldIndex = getIndexForKey(key);
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Moved, key, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError firebaseError) {

    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, String key ,int index) {
        notifyChangedListeners(type, key , index, -1);
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type,String key, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, key ,index, oldIndex);
        }
    }
}
