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

import mnf.android.wearnote.Interfaces.DataUpdateCallback;

/**
 * Created by muneef on 31/01/17.
 */

public class AppController extends Application implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    static Context c;
    static GoogleApiClient mApiClient;
    final static String TAG = "AppController";
    DataUpdateCallback mCallback;
    @Override
    public void onCreate() {
        super.onCreate();

        c =  this;
        ActiveAndroid.initialize(this);
      //  MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");


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


    public void setDataUpdateCallback(DataUpdateCallback callback){
        this.mCallback = callback;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG,"GoogleApiClient connected");
        sendMessage();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"GoogleApiClient onConnectionSuspended");
    }
    public static void sendMessage( ) {
        if(mApiClient!=null)
        if(!mApiClient.isConnected()){
            mApiClient.connect();
        }
        final String path ="/test";
        final String text="test message";
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
