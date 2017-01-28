package mnf.android.wearnote;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.activeandroid.ActiveAndroid;


/**
 * Created by muneef on 26/01/17.
 */

public class ApplicationClass extends MultiDexApplication {

    private static final int CACHE_SIZE = 1;
    private static final String DB_NAME = "note";
    private static final int DB_VERSION = 1;
    Context c;
    @Override
    public void onCreate() {
        super.onCreate();
/*
        Ollie.with(this)
                .setName(DB_NAME)
                .setVersion(DB_VERSION)
                .setLogLevel(Ollie.LogLevel.FULL)
                .setCacheSize(CACHE_SIZE)
                .init();*/

        ActiveAndroid.initialize(this);
    }
}
