package mnf.android.wearnote.tools;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import mnf.android.wearnote.ApplicationClass;

/**
 * Created by muneef on 26/02/17.
 */

public class WearListenerService extends WearableListenerService {
    final static String TAG ="WearListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //showToast(messageEvent.getPath());
        Log.e(TAG,"onMessageReceived "+messageEvent.getPath());
        ApplicationClass.syncDatatoWear();
        ApplicationClass.syncPrefToWear(new WearPreferenceHandler(this));

    }

    @Override
    public void onCreate() {
        Log.e(TAG,"onCreate ");

        super.onCreate();
    }

    private void showToast(String message) {
       Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}