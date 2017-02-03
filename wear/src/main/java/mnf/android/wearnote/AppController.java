package mnf.android.wearnote;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;

/**
 * Created by muneef on 31/01/17.
 */

public class AppController extends Application {
    static Context c;
    @Override
    public void onCreate() {
        super.onCreate();

        c =  this;
        ActiveAndroid.initialize(this);

    }

    public static Context getInstance(){
        return c;
    }
}
