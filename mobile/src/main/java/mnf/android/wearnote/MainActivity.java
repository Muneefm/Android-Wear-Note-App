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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
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
import mnf.android.wearnote.callbacks.DbBackupCallback;
import mnf.android.wearnote.callbacks.PurchaseCallback;
import mnf.android.wearnote.tools.MobilePreferenceHandler;
import mnf.android.wearnote.tools.WearPreferenceHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BillingProcessor.IBillingHandler {
    private static GoogleApiClient mGoogleApiClient;
    FragmentManager fManager;
    private static final String COUNT_KEY = "count";
    private int count = 0;

    static Context c;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    NavigationView navigationView;
    static MaterialDialog loadingDialog;
    TextView tag;
    static MobilePreferenceHandler pref;
    public static String TAG = "MainActivity";
    BillingProcessor bp;
    static PurchaseCallback mPurchaseCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c =this;
        pref = new MobilePreferenceHandler(c);
        // pref.setUserPaidOrNot(false);


        loadingDialog =  new MaterialDialog.Builder(this)
                .progress(true,0)
                .build();


        mFirebaseAuth = FirebaseAuth.getInstance();


        bp = new BillingProcessor(this, Config.base64, this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();
        tag = (TextView) findViewById(R.id.tag);
         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setProVisibility(pref);
        final TextView userNameTv  = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        final TextView userEmailTv  = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);

        final ImageView userImageView  = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_image);



        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Log.e("TAG","bundle not null");

            if(bundle.containsKey("uid")) {
                Log.e("TAG","bundle has key uid");

                if (bundle.getString("uid") != null) {
                    String uid = bundle.getString("uid");
                    Log.e("TAG","bundle uid =  "+uid);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new NoteFragment().newInstance(uid+"", "")).commit();
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ListNote().newInstance("","")).commit();
                }
            }else{
                Log.e("TAG","bundle has  no key ");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ListNote().newInstance("","")).commit();

            }
        }else{
            Log.e("TAG","bundle null");

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new ListNote().newInstance("","")).commit();

        }








        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //user logged in
                    setAccountVisibility(true);
                    if(tag!=null)
                    tag.setVisibility(View.INVISIBLE);

                    Log.e("MainActivity","user logged in main listener ");
                    //Toast.makeText(c,"Successfully logged in",Toast.LENGTH_LONG).show();
                   // ApplicationClass.backupDbToFirebase();
                    //  attachView(user.getDisplayName());
                    if(user.getDisplayName()!=null)
                    userNameTv.setText(user.getDisplayName());

                    if(user.getEmail()!=null)
                    userEmailTv.setText(user.getEmail());

                    if(user.getPhotoUrl()!=null){
                        userImageView.setVisibility(View.VISIBLE);
                        Config.loadImage(user.getPhotoUrl().toString(),userImageView);
                    }



                     pref.setFirstTimeSignInRestore(true);
                     ApplicationClass.restoreBackupDb();

                }else{
                    setAccountVisibility(false);
                    userNameTv.setText("Guest User");
                    userEmailTv.setText("");
                    userImageView.setVisibility(View.INVISIBLE);
                    if(tag!=null)
                        tag.setVisibility(View.VISIBLE);

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


        Log.e(TAG,"purchase call ");

        //onProductPurchased("",null);


    }


    public void setPurchaseCallback(PurchaseCallback mCallback){
        Log.e(TAG,"setPurchaseCallback  ");
        this.mPurchaseCallback = mCallback;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
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


    public static  void showProgressLoading(String body,String header){
    if(loadingDialog!=null){
        loadingDialog.setTitle(header);
        loadingDialog.setContent(body);
        loadingDialog.show();
    }
    }

    public static void hideProgressLoading(){
        if(loadingDialog!=null)
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }



    public void openauthenticationView(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)

                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())  //new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
                                )

                        .build(),
                RC_SIGN_IN);

    }

    public void logoutUser(){
        AuthUI.getInstance()
                .signOut(this);
    }



    public void setProVisibility(MobilePreferenceHandler pref){
        Log.e(TAG,"setProVisibility call ");
        if(navigationView!=null) {
            MenuItem menuLogout = navigationView.getMenu().findItem(R.id.pro);
            if (pref != null && menuLogout != null) {
                Log.e(TAG, "setProVisibility call ");
                if (pref.getUserPaidOrNot()) {
                    menuLogout.setVisible(false);
                }
            }
        }else{
            Log.e(TAG, "navigationView false ");

        }
    }





    @Override
    protected void onResume() {
        super.onResume();
     //   mGoogleApiClient.connect();
        Log.e(TAG,"onResume visiblity call ");
        if(pref!=null){
            Log.e(TAG,"onResume pref not null ");
            setProVisibility(pref);
        }else{
            Log.e(TAG,"onResume pref is null ");

        }


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
        }else if(id == R.id.action_backup_db) {
            if (new ApplicationClass().isUserLoggedin()) {
                showProgressLoading("Please wait..", "Backing Up !");
                new ApplicationClass().setBackupDbListener(new DbBackupCallback() {
                    @Override
                    public void DbBackupDone() {
                        Log.e("TAG", "callback backup done option");
                        Toast.makeText(c, "Backup completed", Toast.LENGTH_LONG).show();

                        hideProgressLoading();
                    }

                    @Override
                    public void DbBackupFailed() {
                        Log.e("TAG", "callback backup failed option");
                        Toast.makeText(c, "Failed to backup", Toast.LENGTH_LONG).show();
                        hideProgressLoading();
                    }
                });
                ApplicationClass.backupDbToFirebase();
            }else{

                openauthenticationView();
            }
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
        else if(id == R.id.pro){
           // onProductPurchased("",null);
           bp.purchase(this, Config.productIdAds);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e(TAG,"onProductPurchased product id= "+productId);
        Log.e(TAG,"onProductPurchased transation details = "+details.purchaseToken);
        if(pref!=null){
            pref.setUserPaidOrNot(true);
            setProVisibility(pref);
        }
        if(mPurchaseCallback!=null){
            Log.e(TAG,"mPurchaseCallback is not null ");
            mPurchaseCallback.onPurchaseMade(productId);
        }else{
            Log.e(TAG,"mPurchaseCallback is null ");
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.e(TAG,"onPurchaseHistoryRestored "+bp.getPurchaseListingDetails(Config.productIdAds));
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e(TAG,"onBillingError errorCode = "+errorCode+"  ");
    }

    @Override
    public void onBillingInitialized() {
        Log.e(TAG,"onBillingInitialized ");
        //bp.purchase(this, Config.productIdAds);
       // Log.e(TAG,"onPurchaseHistoryRestored "+bp.getPurchaseTransactionDetails(Config.productIdAds));

    }
}

