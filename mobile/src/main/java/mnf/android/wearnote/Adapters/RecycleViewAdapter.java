package mnf.android.wearnote.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Delete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.R;


/**
 * Created by muneef on 25/01/17.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;

    List<Note> noteModel;
    Context c;
    boolean undoOn =false;
    List<Note> itemsPendingRemoval;


    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Note, Runnable> pendingRunnables = new HashMap<>();

    public RecycleViewAdapter(Context mContext, List<Note> models) {
        this.c = mContext;
        this.noteModel = models;
        itemsPendingRemoval = new ArrayList<>();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body;
        Button undoButton;
      //  public ImageView thumbnail, overflow;
        View v;
        public ViewHolder(View view) {
            super(view);
            v =view;
            title = (TextView) view.findViewById(R.id.title_note);
             body = (TextView) view.findViewById(R.id.body_note);
            undoButton = (Button) view.findViewById(R.id.undo_button);

            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //  overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        return new ViewHolder(itemView);       }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Typeface face=Typeface.createFromAsset(c.getAssets(), "fonts/Quicksand-Regular.ttf");
        holder.body.setTypeface(face);
        Typeface faceTitle=Typeface.createFromAsset(c.getAssets(), "fonts/Poppins-Regular.ttf");
      //  Typeface faceTitle=Typeface.createFromAsset(c.getAssets(), "fonts/Farsan-Regular.ttf");
        holder.title.setTypeface(faceTitle);

       final Note not = noteModel.get(position);
        if (itemsPendingRemoval.contains(not)) {
            // we need to show the "undo" state of the row
            holder.v.setBackgroundColor(Color.RED);
            holder.title.setVisibility(View.GONE);
            holder.body.setVisibility(View.GONE);

            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(not);
                    pendingRunnables.remove(not);
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(not);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(noteModel.indexOf(not));
                }
            });

        }else {



            // List<Note> notes = Select.from(Note.class).fetch();

            Log.e("TAG", "onBindViewHolder pos = " + position + " note id   = " + noteModel.get(position).getIdn());

            holder.v.setBackgroundColor(Color.WHITE);
            holder.title.setVisibility(View.VISIBLE);
            holder.body.setVisibility(View.VISIBLE);

            holder.title.setText(not.getTitle());
            holder.body.setText(not.getDate());

            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        Log.e("TAG","size of list = "+noteModel.size());
        return noteModel.size();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }


    public void pendingRemoval(int position) {
        final Note item = noteModel.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(noteModel.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Note item = noteModel.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (noteModel.contains(item)) {
            new Delete().from(Note.class).where("idn = ?",noteModel.get(position).getIdn()).execute();
            Log.e("TAG","Deleting item with id ="+noteModel.get(position).getIdn());
            noteModel.remove(position);

            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Note item = noteModel.get(position);
        return itemsPendingRemoval.contains(item);
    }



}
