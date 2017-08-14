package mnf.android.wearnote;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Cache;

import java.util.List;

import mnf.android.wearnote.Adapter.RecycleAdapterMenu;
import mnf.android.wearnote.Adapter.RecycleAdapterNotes;
import mnf.android.wearnote.Fragment.FragmentNote;
import mnf.android.wearnote.Interfaces.DataUpdateCallback;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Tools.Config;
import mnf.android.wearnote.Tools.DataLayerListenerService;
import mnf.android.wearnote.Tools.RecyclerTouchListener;
import mnf.android.wearnote.Tools.SimpleDividerItemDecoration;
import mnf.android.wearnote.Tools.WearPreferenceHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    WearableRecyclerView mRecyclerView;
    List<Note> noteData;
    RelativeLayout headerLayout;
    WearPreferenceHandler pref;
    final static String TAG = "ListFragment";
    RecyclerTouchListener recyclerTouchListener;
    TextView tvNoItem;

    private OnFragmentInteractionListener mListener;
    Context c;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        pref = new WearPreferenceHandler(getActivity());
        c = getActivity();
        mRecyclerView = (WearableRecyclerView) v.findViewById(R.id.notes_recycler_view);
        tvNoItem = (TextView) v.findViewById(R.id.empty_placeholder);
        Typeface faceCabin=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cabin-Regular.ttf");
        tvNoItem.setTypeface(faceCabin);
        //  mRecyclerView.setCenterEdgeItems(true);
        headerLayout = (RelativeLayout) v.findViewById(R.id.rel_head);
        if(pref.getTheme()){
            headerLayout.setBackgroundColor(getResources().getColor(R.color.black));
        }
        MainActivity.MyOffsettingHelper myOffsettingHelper = new MainActivity.MyOffsettingHelper();
        //mRecyclerView.setOffsettingHelper(myOffsettingHelper);
        //mRecyclerView.setCircularScrollingGestureEnabled(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mRecyclerView.setBezelWidth(0.5f);
        mRecyclerView.setBezelWidth(0.5f);
        //mRecyclerView.setScrollDegreesPerScreen(90);
         noteData = Config.getDBItems();
        if(noteData.size()==0){
            tvNoItem.setVisibility(View.VISIBLE);
        }else{
            tvNoItem.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(new RecycleAdapterNotes(getContext(), noteData));

        recyclerTouchListener = new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position,RecyclerView rv) {
                // Movie movie = movieList.get(position);
                //  Toast.makeText(c, position + " is selected!", Toast.LENGTH_SHORT).show();
                Log.e("TAG", " addOnItemTouchListener position = " + position);
                if(noteData!=null) {
                    Note noteItem = noteData.get(position);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.containerView,new FragmentNote().newInstance(""+noteItem.getIdn(),""+noteItem.getBody())).addToBackStack("note").commit();
                }
                //   getActivity().getFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(list.get(position).getIdn().toString(),"")).addToBackStack("note").commit();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        mRecyclerView.addOnItemTouchListener(recyclerTouchListener);


        new DataLayerListenerService().setDataUpdateCallback(new DataUpdateCallback() {
            @Override
            public void dataUpdated() {
                Log.e(TAG,"dataUpdated callback");
                Cache.clear();
               final List<Note> noteDataNew = Config.getDBItems();
                if(mRecyclerView!=null) {
                    if(getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG,"running on ui thread");
                            tvNoItem.setVisibility(View.GONE);

                            mRecyclerView.setAdapter(new RecycleAdapterNotes(getContext(), noteDataNew));

                            if(recyclerTouchListener!=null)
                                mRecyclerView.removeOnItemTouchListener(recyclerTouchListener);


                            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position, RecyclerView rv) {
                                    // Movie movie = movieList.get(position);
                                    //  Toast.makeText(c, position + " is selected!", Toast.LENGTH_SHORT).show();
                                    Log.e("TAG", " addOnItemTouchListener position = " + position);

                                    if(noteDataNew!=null) {
                                        Note noteItem = noteDataNew.get(position);
                                        getActivity().getFragmentManager().beginTransaction().replace(R.id.containerView,new FragmentNote().newInstance(""+noteItem.getIdn(),""+noteItem.getBody())).addToBackStack("note_detail").commit();
                                    }
                                    //   getActivity().getFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(list.get(position).getIdn().toString(),"")).addToBackStack("note").commit();

                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }
                            }));
                        }
                    });


                }
                }
        });


            return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.sendMessage();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
