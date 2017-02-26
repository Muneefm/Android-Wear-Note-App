package mnf.android.wearnote;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by muneef on 31/01/17.
 */

public class AppController extends Application implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    static Context c;
    GoogleApiClient mApiClient;
    final static String TAG = "AppController";
    @Override
    public void onCreate() {
        super.onCreate();

        c =  this;
        ActiveAndroid.initialize(this);

        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
        Log.e(TAG,"GoogleApiClient call");


    }

    public static Context getInstance(){
        return c;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG,"GoogleApiClient connected");
        sendMessage("/test","test message");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"GoogleApiClient onConnectionSuspended");
    }
    public void sendMessage( final String path, final String text ) {
        Log.e(TAG,"GoogleApiClient sendMessage");
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }


            }
        }).start();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"GoogleApiClient onConnectionFailed");

    }
}
