package mnf.android.wearnote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import mnf.android.wearnote.Activity.SettingsActivity;
import mnf.android.wearnote.Fragments.ReminderFragments;
import mnf.android.wearnote.Model.BaseModel;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Model.NoteJson;
import mnf.android.wearnote.tools.WearPreferenceHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static GoogleApiClient mGoogleApiClient;
    FragmentManager fManager;
    private static final String COUNT_KEY = "count";
    private int count = 0;

    static Context c;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c =this;

        mFirebaseAuth = FirebaseAuth.getInstance();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ListNote().newInstance("","")).commit();
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //user logged in
                    setAccountVisibility(true);
                    Log.e("TAG","user logged in ");
                    Toast.makeText(c,"Successfully logged in",Toast.LENGTH_LONG).show();
                   // ApplicationClass.backupDbToFirebase();
                    //  attachView(user.getDisplayName());
                     ApplicationClass.restoreBackupDb();

                }else{
                    setAccountVisibility(false);

                    Log.e("TAG","user logged out ");
                    //user logged out
                    //  dettachView();

                   /* startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().createSignInIntentBuilder().build(),
                            RC_SIGN_IN);*/
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }


    public void setAccountVisibility(boolean loggedIn){
        MenuItem menuLogout = navigationView.getMenu().findItem(R.id.nav_logout);
        MenuItem menuLogin = navigationView.getMenu().findItem(R.id.nav_login);
        if(loggedIn){
            menuLogin.setVisible(false);
            menuLogout.setVisible(true);

        }else{
            menuLogin.setVisible(true);
            menuLogout.setVisible(false);


        }

    }




    public void openauthenticationView(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)

                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())  //new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
                                )

                        .build(),
                RC_SIGN_IN);





    }

    public void logoutUser(){
        AuthUI.getInstance()
                .signOut(this);
    }








    @Override
    protected void onResume() {
        super.onResume();
     //   mGoogleApiClient.connect();


    }

    @Override
    protected void onPause() {
        super.onPause();
//        mGoogleApiClient.disconnect();

    }

    public void putWearData(){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putInt(COUNT_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

   /* public static void syncDatatoWear(){
        Log.e("TAG","syncing data to wear");
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
    }*/

    public static void syncPrefToWear(){
        WearPreferenceHandler pref =new WearPreferenceHandler(c);
        Log.e("TAG","syncing Preference to wear f_size = "+pref.getFontSize()+" theme = "+pref.getTheme());

        final PutDataMapRequest putRequestPref = PutDataMapRequest.create("/pref");
        final DataMap mapPref = putRequestPref.getDataMap();
        mapPref.putString("font_size", pref.getFontSize());
        mapPref.putBoolean("theme",pref.getTheme());
        Wearable.DataApi.putDataItem(mGoogleApiClient,  putRequestPref.asPutDataRequest());
    }







    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent set = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(set);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_note) {
            // Handle the camera action
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ListNote().newInstance("","")).commit();

        } else if (id == R.id.nav_settings) {
            Intent set = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(set);
        }else if (id == R.id.nav_reminder){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ReminderFragments().newInstance("","")).commit();

        }else if(id == R.id.nav_login){
            openauthenticationView();
        }else if(id == R.id.nav_logout){
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
