package mnf.android.wearnote.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mnf.android.wearnote.R;
import mnf.android.wearnote.Tools.WearPreferenceHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNote.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNote extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
        TextView tvNote;
    RelativeLayout rootContainer;
    WearPreferenceHandler pref;
    public FragmentNote() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNote.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNote newInstance(String param1, String param2) {
        FragmentNote fragment = new FragmentNote();
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
        View v = inflater.inflate(R.layout.fragment_fragment_note, container, false);
        tvNote = (TextView) v.findViewById(R.id.note);
        rootContainer = (RelativeLayout) v.findViewById(R.id.note_frag_container);
        pref =new WearPreferenceHandler(getActivity());
        if(pref.getTheme())
        rootContainer.setBackgroundColor(getResources().getColor(R.color.black));
        else
            rootContainer.setBackgroundColor(getResources().getColor(R.color.white));


        Typeface faceYellowtail=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Yellowtail-Regular.ttf");
        Typeface faceCabin=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface faceRoboto=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface faceNunitoSans=Typeface.createFromAsset(getActivity().getAssets(), "fonts/NunitoSans-Regular.ttf");

        switch (pref.getFontSize()){
            case "0":
                tvNote.setTextSize(10f);
                break;
            case "1":
                tvNote.setTextSize(15f);
                break;
            case "2":
                tvNote.setTextSize(20f);
                break;
            case "3":
                tvNote.setTextSize(30f);
                break;
            default:
                tvNote.setTextSize(25f);
                break;
        }
        switch (pref.getFontStyle()){
            case "0":
                tvNote.setTypeface(faceYellowtail);
                break;
            case "1":
                tvNote.setTypeface(faceRoboto);
                break;
            case "2":
                tvNote.setTypeface(faceNunitoSans);
                break;
            case "3":
                tvNote.setTypeface(faceCabin);
                break;
            default:
                tvNote.setTypeface(faceRoboto);
                break;

        }

        if(!mParam2.equals("")&&mParam2!=null) {
            tvNote.setText(mParam2);
            if(pref.getFontColor() == 0){
                Log.e("NoteFragment", "color pref value is zero ");

                tvNote.setTextColor(getResources().getColor(R.color.grey800));

            }else {
                Log.e("NoteFragment", "color pref value is not zero ");
                tvNote.setTextColor(pref.getFontColor());
                Log.e("NoteFragment", "raw colot  int = " + R.color.grey800);

                Log.e("NoteFragment", "pref int = " + pref.getFontColor() + " grey int = " + getResources().getColor(R.color.grey800));
            }
            tvNote.setVisibility(View.VISIBLE);
        }



        return v;
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
