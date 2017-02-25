package mnf.android.wearnote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Cache;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import mnf.android.wearnote.Adapters.RecycleViewAdapter;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.callbacks.AdapterItemUpdate;
import mnf.android.wearnote.tools.DividerItemDecoration;
import mnf.android.wearnote.tools.RecyclerTouchListener;
import mnf.android.wearnote.tools.SimpleDividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListNote.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListNote extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   // RecyclerView rcMain;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.rc_main)
    RecyclerView recyclerView;
    private static RecycleViewAdapter adapter;
    static TextView emptyPlaceholder;
    RecyclerView.OnItemTouchListener listener;
    Context c;
    public ListNote() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListNote.
     */
    // TODO: Rename and change types and number of parameters
    public static ListNote newInstance(String param1, String param2) {
        ListNote fragment = new ListNote();
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
    public void onResume() {
        super.onResume();
        final List<Note> list=  new Select()
                .all()
                .from(Note.class)
                .execute();
        for (Note note:list) {
            Log.e("TAG","empty deleting body = "+note.getBody());
            if(note.getBody().equals("")){
                new Delete().from(Note.class).where("idn = ?",note.getIdn()).execute();
                Log.e("TAG","deleting record with id  ="+note.getIdn());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_note, container, false);
       // rcMain = (RecyclerView) v.findViewById(R.id.rc_main);

        c = getActivity();
        ButterKnife.bind(getActivity());
        recyclerView = (RecyclerView) v.findViewById(R.id.rc_main);
        emptyPlaceholder = (TextView) v.findViewById(R.id.empty_placeholder);
        Typeface face=Typeface.createFromAsset(c.getAssets(), "fonts/Cabin-Regular.ttf");
        emptyPlaceholder.setTypeface(face);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New note opened", Snackbar.LENGTH_LONG)
                     .show();
                String newIdn = Config.generateRandomNumberGenerate();
                Log.e("TAG","new rand = "+newIdn);
                Note note = new Note();
                note.idn = newIdn;
                note.title = "";
                note.body = "";
                note.date=  DateFormat.getDateTimeInstance().format(new Date());
                note.save();
                Log.e("TAG","new record with id  = "+newIdn);

                //  FragmentManager fragmentManager = getFragmentManager()
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(newIdn+"","")).addToBackStack("note").commit();

            }
        });


        final List<Note> listn=  new Select()
                .all()
                .from(Note.class)
                .execute();
        for (Note note:listn) {
            Log.e("TAG","empty deleting body = "+note.getBody());
            if(note.getBody().equals("")){
                new Delete().from(Note.class).where("idn = ?",note.getIdn()).execute();
                Log.e("TAG","deleting record with id  ="+note.getIdn());
            }
        }
        final List<Note> list=  new Select()
                .all()
                .from(Note.class)
                .execute();
        if(list.size()<=0){
            emptyPlaceholder.setVisibility(View.VISIBLE);
        }
        adapter = new RecycleViewAdapter(c,list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
      //  recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, Config.dpToPx(1,getApplicationContext()), true));
        //recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(c));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
        new ApplicationClass().setAdapterItemListener(new AdapterItemUpdate() {
            @Override
            public void itemUpdated() {
                Cache.clear();
                final List<Note> items = Config.getNoteList();
                Log.e("ListNote","item Updated  list size =  "+items.size());

                if(items.size()>0){
                    emptyPlaceholder.setVisibility(View.INVISIBLE);
                }else{
                    emptyPlaceholder.setVisibility(View.VISIBLE);
                }

                adapter.addItems(items);
              //  addRecycleTouchListener(items,recyclerView);
                if(listener!=null){
                    recyclerView.removeOnItemTouchListener(listener);
                }
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // Movie movie = movieList.get(position);
                        // Toast.makeText(c, position + " is selected!", Toast.LENGTH_SHORT).show();
                        Log.e("ListNote"," new lister position = "+position+" list size =  "+items.size());
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(items.get(position).getIdn().toString(),"")).addToBackStack("note").commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

            }
        });

        addRecycleTouchListener(list,recyclerView);

        return v;
    }

    public void addRecycleTouchListener(  final List<Note> items, RecyclerView recyclerView){
        Log.e("ListNote","items size = "+items.size());
         listener = new RecyclerTouchListener(c, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Movie movie = movieList.get(position);
                // Toast.makeText(c, position + " is selected!", Toast.LENGTH_SHORT).show();
                Log.e("ListNote","position = "+position+" list size =  "+items.size());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new NoteFragment().newInstance(items.get(position).getIdn().toString(),"")).addToBackStack("note").commit();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        recyclerView.addOnItemTouchListener(listener);


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
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(c, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) c.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                RecycleViewAdapter testAdapter = (RecycleViewAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                RecycleViewAdapter adapter = (RecycleViewAdapter)recyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }




}
