package mnf.android.wearnote;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by muneef on 31/01/17.
 */

public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);

    }
}
