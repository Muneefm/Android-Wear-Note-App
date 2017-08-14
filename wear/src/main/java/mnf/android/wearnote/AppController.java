package mnf.android.wearnote;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.ConfirmationOverlay;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.wearable.intent.RemoteIntent;
import com.google.android.wearable.playstore.PlayStoreAvailability;

import java.util.Set;

import mnf.android.wearnote.Interfaces.DataUpdateCallback;

/**
 * Created by muneef on 31/01/17.
 */

public class AppController extends Application implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener{
    static Context c;
    static GoogleApiClient mApiClient;
    final static String TAG = "AppController";
    DataUpdateCallback mCallback;
    private static final String PLAY_STORE_APP_URI =
            "market://details?id=mnf.android.wearnote";
    private static final String CAPABILITY_PHONE_APP = "verify_remote_wearnote_phone_app";
    private Node mAndroidPhoneNodeWithApp;


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
       // new AppController().checkIfPhoneHasApp();


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
        Wearable.CapabilityApi.addCapabilityListener(
                mApiClient,
                this,
                CAPABILITY_PHONE_APP);
       // checkIfPhoneHasApp();


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

    public void checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()");

        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(
                        mApiClient,
                        CAPABILITY_PHONE_APP,
                        CapabilityApi.FILTER_ALL);

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {

            @Override
            public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Log.d(TAG, "onResult(): " + getCapabilityResult);

                if (getCapabilityResult.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
                    verifyNodeAndUpdateUI();

                } else {
                    Log.d(TAG, "Failed CapabilityApi: " + getCapabilityResult.getStatus());
                }
            }
        });
    }

    public void openAppInStoreOnPhone() {
        Log.d(TAG, "openAppInStoreOnPhone()");

        int playStoreAvailabilityOnPhone =
                PlayStoreAvailability.getPlayStoreAvailabilityOnPhone(getInstance());

        switch (playStoreAvailabilityOnPhone) {

            // Android phone with the Play Store.
            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_AVAILABLE:
                Log.d(TAG, "\tPLAY_STORE_ON_PHONE_AVAILABLE");

                // Create Remote Intent to open Play Store listing of app on remote device.
                Intent intentAndroid =
                        new Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(Uri.parse(PLAY_STORE_APP_URI));

                RemoteIntent.startRemoteActivity(
                        getInstance(),
                        intentAndroid,
                        mResultReceiver);
                break;

            // Assume iPhone (iOS device) or Android without Play Store (not supported right now).
            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_UNAVAILABLE:
                Log.e(TAG, "\tPLAY_STORE_ON_PHONE_UNAVAILABLE");

                // Create Remote Intent to open App Store listing of app on iPhone.
                /*Intent intentIOS =
                        new Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(Uri.parse(APP_STORE_APP_URI));*/

              /*  RemoteIntent.startRemoteActivity(
                        getApplicationContext(),
                        intentIOS,
                        mResultReceiver);*/
                break;

            case PlayStoreAvailability.PLAY_STORE_ON_PHONE_ERROR_UNKNOWN:
                Log.d(TAG, "\tPLAY_STORE_ON_PHONE_ERROR_UNKNOWN");
                break;
        }
    }


    // Result from sending RemoteIntent to phone to open app in play/app store.
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == RemoteIntent.RESULT_OK) {
              //  new ConfirmationOverlay().showOn(AppController.this);
                Log.e(TAG, "open in phone result ok");

            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                Log.e(TAG, "open in phone result failed");
               /* new ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(MainWearActivity.this);*/

            } else {
                throw new IllegalStateException("Unexpected result " + resultCode);
            }
        }
    };


    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): " + capabilityInfo);

        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
        verifyNodeAndUpdateUI();
    }
    private Node pickBestNodeId(Set<Node> nodes) {
        Log.d(TAG, "pickBestNodeId(): " + nodes);

        Node bestNodeId = null;
        // Find a nearby node/phone or pick one arbitrarily. Realistically, there is only one phone.
        for (Node node : nodes) {
            bestNodeId = node;
        }
        return bestNodeId;
    }
    private void verifyNodeAndUpdateUI() {

        if (mAndroidPhoneNodeWithApp != null) {

            // TODO: Add your code to communicate with the phone app via
            // Wear APIs (MessageApi, DataApi, etc.)

           /* String installMessage =
                    String.format(INSTALLED_MESSAGE, mAndroidPhoneNodeWithApp.getDisplayName());
            Log.d(TAG, installMessage);
            mInformationTextView.setText(installMessage);
            mRemoteOpenButton.setVisibility(View.INVISIBLE);*/
            Log.e(TAG, " App found in phone " );


        } else {
            Log.e(TAG, " App not found in phone " );
            Intent installWindow = new Intent(getInstance(), InstallNoteActivity.class);
            getInstance().startActivity(installWindow);

        }
    }

}
