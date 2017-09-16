package mnf.android.wearnote;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DefaultOffsettingHelper;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import mnf.android.wearnote.Adapter.RecycleAdapterMenu;
import mnf.android.wearnote.Fragment.FragmentNote;
import mnf.android.wearnote.Fragment.NewNoteFragment;
import mnf.android.wearnote.Interfaces.PhoneAppCheckCallback;
import mnf.android.wearnote.Model.MenuModel;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Tools.RecyclerTouchListener;

public class MainActivity extends WearableActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;
    private static final String COUNT_KEY = "count";
    RelativeLayout appInstallContainer;
    WearableRecyclerView mRecyclerView;
    TextView tvInstallNote;
    Button installBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_launcher_view);
        mRecyclerView.setCenterEdgeItems(true);
        appInstallContainer = (RelativeLayout) findViewById(R.id.not_installed_container);
        tvInstallNote = (TextView) findViewById(R.id.install_note);
        installBtn = (Button) findViewById(R.id.inst_btn);
        Typeface faceCabin=Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Cabin-Regular.ttf");
        tvInstallNote.setTypeface(faceCabin);
        /* final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        CircularOffsettingHelper circularHelper = new CircularOffsettingHelper();
        MyOffsettingHelper myOffsettingHelper = new MyOffsettingHelper();
       mRecyclerView.setOffsettingHelper(myOffsettingHelper);
        mRecyclerView.setCircularScrollingGestureEnabled(false);
        mRecyclerView.setBezelWidth(1f);
        mRecyclerView.setScrollDegreesPerScreen(250);

        getFragmentManager().beginTransaction().replace(R.id.containerView,new ListFragment().newInstance("","")).addToBackStack("note").commit();


     /*   final List<MenuModel> listItem = new ArrayList<>();
        listItem.add(new MenuModel("1","New Note",R.mipmap.red_add));
        listItem.add(new MenuModel("2","Notes",R.mipmap.red_note));
        listItem.add(new MenuModel("3","Settings",R.mipmap.red_settr));

        mRecyclerView.setAdapter(new RecycleAdapterMenu(this,listItem));

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position,RecyclerView rv) {
                // Movie movie = movieList.get(position);
                //  Toast.makeText(c, position + " is selected!", Toast.LENGTH_SHORT).show();
                Log.e("TAG", " addOnItemTouchListener position = " + position);
                if(listItem!=null) {
                    MenuModel menuItem = listItem.get(position);
                    switch (menuItem.getId()){
                        case "1":
                            getFragmentManager().beginTransaction().replace(R.id.containerView,new NewNoteFragment().newInstance("","")).addToBackStack("new_note").commit();
                            break;
                        case "2":
                          FragmentTransaction transNotes = getFragmentManager().beginTransaction();
                            transNotes.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                            transNotes.replace(R.id.containerView,new ListFragment().newInstance("","")).addToBackStack("note_list").commit();
                            break;
                        case "3":
                            getFragmentManager().beginTransaction().replace(R.id.containerView,new ListFragment().newInstance("","")).addToBackStack("note_list").commit();
                            break;


                    }
                   //getFragmentManager().beginTransaction().replace(R.id.containerView,new FragmentNote().newInstance(""+noteItem.getIdn(),""+noteItem.getBody())).addToBackStack("note_detail").commit();
                }
                //   getActivity().getFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(list.get(position).getIdn().toString(),"")).addToBackStack("note").commit();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/



    }



    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        new AppController().sendMessage();
        Log.e("callback","onResume ");



    }


    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("TAG","mGoogleApiClient onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.e("TAG","mGoogleApiClient onDataChanged");

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }

    }
    private void updateCount(int c) {  }

    public class CircularOffsettingHelper extends WearableRecyclerView.OffsettingHelper {

        @Override
        public void updateChild(View child, WearableRecyclerView parent) {
            int progress = child.getTop() / parent.getHeight();
            child.setTranslationX(-child.getHeight() * progress);
        }
    }

    public static  class MyOffsettingHelper extends DefaultOffsettingHelper {

        /** How much should we scale the icon at most. */
        private static final float MAX_ICON_PROGRESS = 2f;

        private float mProgressToCenter;

        public void OffsettingHelper() {}

        @Override

        public void updateChild(View child,  WearableRecyclerView parent) {
            super.updateChild(child, parent);


            // Figure out % progress from top to bottom
            float centerOffset = ((float) child.getHeight() / 2.0f) /  (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            // Normalize for center
            mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            // Adjust to the maximum scale
            mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - mProgressToCenter);
            child.setScaleY(1 - mProgressToCenter);
        }
    }



}
