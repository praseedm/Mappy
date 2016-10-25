package praseed.p6c.mappy;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by praseedm on 10/25/2016.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
