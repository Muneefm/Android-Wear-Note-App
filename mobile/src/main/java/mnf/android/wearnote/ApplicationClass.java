package mnf.android.wearnote;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.MenuItem;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.List;

import mnf.android.wearnote.Model.BaseModel;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.tools.WearPreferenceHandler;


/**
 * Created by muneef on 26/01/17.
 */

public class ApplicationClass extends MultiDexApplication implements NavigationView.OnNavigationItemSelectedListener,DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int CACHE_SIZE = 1;
    private static final String DB_NAME = "note";
    private static final int DB_VERSION = 1;
    static Context c;
    private static GoogleApiClient mGoogleApiClient;

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("TAG","ApplicationClass onConnected");
        syncDatatoWear();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("TAG","ApplicationClass onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG","ApplicationClass onConnectionFailed");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.e("TAG","ApplicationClass onDataChanged");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("TAG","ApplicationClass onTerminate");
        mGoogleApiClient.disconnect();

    }

    public static void syncDatatoWear(){
        Log.e("TAG","ApplicationClass syncing data to wear");
        List<Note> notes =new Select()
                .all()
                .from(Note.class)
                .execute();
        BaseModel model = new BaseModel();
        model.setNote(notes);

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();
        String json = gson.toJson(model);
        Log.e("TAG","jsonString = "+json);
        final PutDataMapRequest putRequest = PutDataMapRequest.create("/notes");
        final DataMap map = putRequest.getDataMap();
        //  map.putInt("color", Color.RED);
        map.putString("database", json);
        Wearable.DataApi.putDataItem(mGoogleApiClient,  putRequest.asPutDataRequest());
    }

    public static void syncPrefToWear(WearPreferenceHandler prefVar){
        WearPreferenceHandler pref =prefVar;
        Log.e("TAG"," ApplicationClass syncing Preference to wear f_size = "+pref.getFontSize()+" theme = "+pref.getTheme());

        final PutDataMapRequest putRequestPref = PutDataMapRequest.create("/pref");
        final DataMap mapPref = putRequestPref.getDataMap();
        mapPref.putString("font_size", pref.getFontSize());
        mapPref.putBoolean("theme",pref.getTheme());
        mapPref.putInt("font_color",pref.getFontColor());
        mapPref.putString("font_style",pref.getFontStyle());
        Wearable.DataApi.putDataItem(mGoogleApiClient,  putRequestPref.asPutDataRequest());
    }

}
