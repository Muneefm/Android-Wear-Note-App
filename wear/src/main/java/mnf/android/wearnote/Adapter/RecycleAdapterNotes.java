package mnf.android.wearnote.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mnf.android.wearnote.Model.MenuModel;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.R;

/**
 * Created by muneef on 31/01/17.
 */

public class RecycleAdapterNotes  extends WearableRecyclerView.Adapter<RecycleAdapterNotes.ItemViewHolder> {
    // private String[] mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;
    List<Note> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecycleAdapterNotes(Context context, List<Note> models) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = models;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableRecyclerView.ViewHolder {
        private TextView textView,dateTv;
        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.w_note_title);
            dateTv = (TextView) itemView.findViewById(R.id.w_note_date);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
  /*  @Override
    public  onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
    }
*/

    @Override
    public RecycleAdapterNotes.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecycleAdapterNotes.ItemViewHolder(mInflater.inflate(R.layout.note_list_item, null));

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Typeface face=Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.ttf");
        holder.textView.setTypeface(face);
        Note noteModel = mDataset.get(position);
        holder.dateTv.setText(noteModel.getDate());
        holder.textView.setText(noteModel.getTitle());

    }




    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
   /* @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView tview = itemHolder.textView;
        // replace text contents
        tview.setText(mDataset.get(position).getTitle());
        // replace list item's metadata
        holder.itemView.setTag(position);
    }
*/
    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        Log.e("TAG","recycle view Notes item count = "+mDataset.size());

        return mDataset.size();
    }
}
