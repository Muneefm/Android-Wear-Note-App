package mnf.android.wearnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.activeandroid.DatabaseHelper;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import mnf.android.wearnote.Activity.SettingsActivity;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.tools.MobilePreferenceHandler;
import mnf.android.wearnote.tools.Reciever;
import mnf.android.wearnote.tools.SendNotification;

import static android.content.Context.ALARM_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Note note;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;
    private OnFragmentInteractionListener mListener;
    MobilePreferenceHandler pref;
    FloatingActionsMenu fabMenu;
    FloatingActionButton fabSendWear,fabReminder;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @BindView(R.id.edt_note)
    EditText edtNote;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this,v);
        pref = new MobilePreferenceHandler(getActivity());
        context = getActivity();

        AdView mAdView = (AdView) v.findViewById(R.id.adViewNote);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.e("TAG","id recieved is "+mParam1);

      //  Typeface face=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cabin-Regular.ttf");
       // edtNote.setTypeface(face);

        Typeface faceYellowtail=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Yellowtail-Regular.ttf");
        Typeface faceCabin=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface faceRoboto=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface faceNunitoSans=Typeface.createFromAsset(getActivity().getAssets(), "fonts/NunitoSans-Regular.ttf");

        Log.e("TAG","pref values "+pref.getFontStyle()+" -- "+pref.getFontSize());
        switch (pref.getFontStyle()){
            case "0":
                edtNote.setTypeface(faceYellowtail);
                break;
            case "1":
                edtNote.setTypeface(faceRoboto);
                break;
            case "2":
                edtNote.setTypeface(faceNunitoSans);
                break;
            case "3":
                edtNote.setTypeface(faceCabin);
                break;
            default:
                edtNote.setTypeface(faceRoboto);
                break;

        }


        switch (pref.getFontSize()){
            case "0":
                edtNote.setTextSize(10f);
                break;
            case "1":
                edtNote.setTextSize(15f);
                break;
            case "2":
                edtNote.setTextSize(20f);
                break;
            case "3":
                edtNote.setTextSize(30f);
                break;
            default:
                edtNote.setTextSize(25f);
                break;
        }



        if(mParam1!=null&&!mParam1.equals("")) {
       note =  new Select()
                .from(Note.class)
                .where("idn = ?", mParam1)
                .executeSingle();

        if(note.getTitle()!=null)
            getActivity().setTitle(note.getTitle());

        edtNote.setText(note.getBody());
    }

        edtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("Tag","beforeTextChanged s="+s);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Tag","onTextChanged s="+s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                    Log.e("Tag","afterTextChanged s="+s);
               // String str= s.toString().trim();
                String multiLines = s.toString();
                String[] lines;
                String delimiter = "\n";
                Log.e("TAG","string before escape = "+s.toString());

                String str = DatabaseUtils.sqlEscapeString(s.toString());
               // s.toString().replaceAll("'","''");
                Log.e("TAG","string after escape = "+str);

                lines = multiLines.split(delimiter);
                new Update(Note.class)
                        .set("body = "+str)
                        .where("idn = ?", mParam1)
                        .execute();
                new Update(Note.class)
                        .set("date = "+"'"+ DateFormat.getDateTimeInstance().format(new Date())+"'")
                        .where("idn = ?", mParam1)
                        .execute();
                if(lines[0]!=null)
                new Update(Note.class)
                        .set("title = "+""+DatabaseUtils.sqlEscapeString(lines[0].trim())+"")
                        .where("idn = ?", mParam1)
                        .execute();
                ApplicationClass.syncDatatoWear();
            }
        });


        fabMenu = (FloatingActionsMenu) v.findViewById(R.id.fab_notefrag);
        fabSendWear = (FloatingActionButton) v.findViewById(R.id.fab_sentwear);
        fabReminder = (FloatingActionButton) v.findViewById(R.id.fab_reminder);
//        fabMenu.addButton(fabSendWear);
   //     fabMenu.addButton(fabReminder);
        if(!pref.getFabLearn()){
            fabMenu.expand();
            pref.setFabLearn(true);
        }
        fabReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateView();
               // fabMenu.collapse();
            }
        });
        fabSendWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
              //  fabMenu.collapse();
            }
        });


        return v;
    }

    public  void openDateView(){
        new SingleDateAndTimePickerDialog.Builder(context)
                .bottomSheet()
                .curved()
                .minutesStep(1)

                .title("Select date & time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                    Log.e("TAG","Date selected = "+date);
                        Config.setReminder(date,context,mParam1);
                         Snackbar.make(edtNote,"Reminder Added ",Snackbar.LENGTH_LONG).show();
                    }
                }).display();
    }

    public void  sendNotification(){
        Note noteGet = new Select()
                .from(Note.class)
                .where("idn = ?", mParam1)
                .executeSingle();
        new SendNotification(context,noteGet.body,noteGet.getTitle(),mParam1).sendNotificationWear();
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
        ApplicationClass.backupDbToFirebase();
        Log.e("TAG","onDetach backupDbToFirebase ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
     //   super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_fragment_menu, menu);
        Log.e("TAG","onCreateOptionsMenu NoteFragment");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e("TAG","onOptionsItemSelected NoteFragment");

        //noinspection SimplifiableIfStatement
        if (id == R.id.send_notification) {
           Log.e("TAG","send_notification select");
            Note noteGet = new Select()
                    .from(Note.class)
                    .where("idn = ?", mParam1)
                    .executeSingle();
             new SendNotification(context,noteGet.body,noteGet.getTitle(),mParam1).sendNotificationWear();


            return true;
        }else if(id == R.id.send_ontime){
            openDateView();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
