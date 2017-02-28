package mnf.android.wearnote;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.MenuItem;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

import mnf.android.wearnote.Model.BaseModel;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.callbacks.AdapterItemUpdate;
import mnf.android.wearnote.callbacks.DbBackupCallback;
import mnf.android.wearnote.tools.MobilePreferenceHandler;
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

    private static FirebaseStorage mFirebaseStorage;
    private static StorageReference mStorageReferance;
    private FirebaseAuth mFirebaseAuth;
    static MobilePreferenceHandler pref;
    public static String DB_PATH = "/data/data/mnf.android.wearnote/databases/note.db";
   static  boolean oldContain = false;
    static int ii;
    static DbBackupCallback mListenerDb;
    static AdapterItemUpdate mListenerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        c = this;
/*
        Ollie.with(this)
                .setName(DB_NAME)
                .setVersion(DB_VERSION)
                .setLogLevel(Ollie.LogLevel.FULL)
                .setCacheSize(CACHE_SIZE)
                .init();*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(getApplicationContext(), getInstance().getResources().getString(R.string.banner_ad_unit_id));



        ActiveAndroid.initialize(this);
        pref = new MobilePreferenceHandler(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
           // backupDbToFirebase();
           // restoreBackupDb();

            if(pref.getFirstTimeOpen()){
                //restoreBackupDb();
            }
            Log.e("TAG","Application class user logged in ");
        }else{
            Log.e("TAG","Application class user logged out ");
        }
        syncDatatoWear();
        syncPrefToWear(new WearPreferenceHandler(getInstance()));
    }


    public Context getInstance(){
        this.c = this;
        return c;
    }

    public void setBackupDbListener(DbBackupCallback eventListener) {
        this.mListenerDb=eventListener;
    }
    public void setAdapterItemListener(AdapterItemUpdate eventListener) {
        this.mListenerAdapter=eventListener;
    }


    public boolean isUserLoggedin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            return true;
        }else{
            return false;
        }
    }



    public static void restoreBackupDb(){
        Log.e("TAG","Application class restoreBackupDb");
         final List<Note> oldnNoteItems = Config.getNoteList();

        for (Note itm : oldnNoteItems) {
            Log.e("TAG","Application class oldnNoteItems  old idn = "+itm.getIdn()+" title = "+itm.getTitle());

        }
        if(oldnNoteItems.size()>0){
            oldContain = true;
        }
        Log.e("TAG","Application class restoreBackupDb oldContain = "+oldContain);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            String id = user.getUid();
            mFirebaseStorage = FirebaseStorage.getInstance();
            mStorageReferance = mFirebaseStorage.getReference().child("Database");
            StorageReference idFolder = mStorageReferance.child(id);

            idFolder.child(id).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.e("TAG","Application db download success  ");
                    writeBytesToFile(bytes,DB_PATH,oldnNoteItems);
                }
            }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.e("TAG","Application db download failure  e = "+e);

                 }
             });
        }
    }


    private static void writeBytesToFile(byte[] bFile, String fileDest,List<Note> oldItem) {
        Log.e("TAG","Application db writeBytesToFile  ");

        try (FileOutputStream fileOuputStream = new FileOutputStream(fileDest)) {
            fileOuputStream.write(bFile);
            if(oldContain){
                populateOldNotes(oldItem);
            }else{
                refreshNoteAdapter();
                pref.setFirstTimeOpen(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public static void popOldNote(){
        for (Note itm1 : oldnNoteItems) {
            Log.e("TAG","Application populateOldNotes function start ---  old idn = "+itm1.getIdn()+" title = "+itm1.getTitle());
        }
        List<Note> oldIt = oldnNoteItems;
        for (Note itm3 : oldIt) {
            Log.e("TAG","Application populateOldNotes function start ---  old idn = "+itm3.getIdn()+" title = "+itm3.getTitle());
        }
         newItems = new Config().getNoteList();
        for (Note itm2 : oldnNoteItems) {
            Log.e("TAG","Application populateOldNotes function start ---  old idn = "+itm2.getIdn()+" title = "+itm2.getTitle());
        }
        for (Note itm3 : oldIt) {
            Log.e("TAG","Application populateOldNotes function start ---  old idn = "+itm3.getIdn()+" title = "+itm3.getTitle());
        }
    }

*/


    public static void populateOldNotes(List<Note> oldnNoteItemsAll){
       // Log.e("TAG","Application class populateOldNotes function start old ii  = "+ii);
      //  int i = Config.getExample();
     //   Log.e("TAG","Application class populateOldNotes after old ii  = "+ii+" i ="+i);
       // oldItems = oldnNoteItems;
       for (Note itm : oldnNoteItemsAll) {
            Log.e("TAG","Application class populateOldNotes function start old idn = "+itm.getIdn()+" title = "+itm.getTitle());

        }
       // Cache.removeEntity(yourModel);
        Cache.clear();
        if(oldnNoteItemsAll!=null){
            List<Note> newItems = Config.getNoteList();
            if(newItems!=null) {  // no problem
                int newCount = newItems.size();
                Log.e("TAG","Application populateOldNotes  newCount = "+newCount+" old note size = "+oldnNoteItemsAll.size());
                int key =0;
                for (Note itm : oldnNoteItemsAll) {
                    Log.e("TAG","Application class beforeLoop  old idn = "+itm.getIdn()+" title = "+itm.getTitle());
                }
                for (Note oldNoteItem : oldnNoteItemsAll) {
                    key =0;
                    for (Note newNoteItem: newItems) {
                        Log.e("TAG","Application loop  new idn = "+newNoteItem.getIdn()+" title "+newNoteItem.getTitle()+" old idn = "+oldNoteItem.getIdn()+" title = "+oldNoteItem.getTitle());
                        if(!newNoteItem.getIdn().equals(oldNoteItem.getIdn())){
                            key++;
                            Log.e("TAG","Application loop  k = "+key);
                        }
                    }
                    Log.e("TAG","Application populateOldNotes  inner loop over key  = "+key);
                    if(key == newCount){
                        Note noteAdd = new Note();
                        if(oldNoteItem.getDate()!=null)
                            noteAdd.setDate(oldNoteItem.getDate());
                        if(oldNoteItem.getBody()!=null)
                            noteAdd.setBody(oldNoteItem.getBody());
                        if(oldNoteItem.getTitle()!=null)
                            noteAdd.setTitle(oldNoteItem.getTitle());
                        if(oldNoteItem.getIdn()!=null)
                            noteAdd.setIdn(oldNoteItem.getIdn());
                        noteAdd.save();
                        Log.e("TAG","Application populateOldNotes  new note added idn = "+oldNoteItem.getIdn());
                    }

                }
            }
        }
        refreshNoteAdapter();
        pref.setFirstTimeOpen(false);
        backupDbToFirebase();
    }

    public static void refreshNoteAdapter(){
        if(mListenerAdapter!=null){
            mListenerAdapter.itemUpdated();
        }
           // ListNote.addAdapterItems();
    }


    private static void replaceDb() {
        Log.e("TAG","Application db download success  ");

    }


    public static void backupDbToFirebase(){
        Log.e("TAG","Application backupDbToFirebase  ");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            if(user.getUid()!=null){
                String id = user.getUid();
                mFirebaseStorage = FirebaseStorage.getInstance();
                mStorageReferance = mFirebaseStorage.getReference().child("Database");
                StorageReference idFolder = mStorageReferance.child(id);
                StorageReference refUser = idFolder.child(id);
                Log.e("TAG","Application backupDbToFirebase  url = "+refUser.getPath());

                refUser.putFile(Uri.fromFile(getDbFile())).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e("TAG","Application db upload success  ");
                        if(mListenerDb!=null)
                        mListenerDb.DbBackupDone();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG","Application db upload failure  ");
                        if(mListenerDb!=null)
                            mListenerDb.DbBackupFailed();
                    }
                });
                Log.e("TAG","Application db uri =  "+Uri.fromFile(getDbFile()));

            }else{
                Log.e("TAG","user uid is null  ");
                if(mListenerDb!=null)
                    mListenerDb.DbBackupFailed();
            }

        }else{
            Log.e("TAG","user not logged in  ");

            if(mListenerDb!=null)
                mListenerDb.DbBackupFailed();
        }

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


    public static File getDbFile(){
        return  new File("/data/data/mnf.android.wearnote/databases/note.db");
    }

    public static void syncDbFromFirebase(){

    }

}
