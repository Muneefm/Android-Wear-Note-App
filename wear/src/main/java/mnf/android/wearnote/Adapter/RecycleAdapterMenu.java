package mnf.android.wearnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mnf.android.wearnote.Model.MenuModel;
import mnf.android.wearnote.R;

/**
 * Created by muneef on 29/01/17.
 */

public class RecycleAdapterMenu extends WearableRecyclerView.Adapter<RecycleAdapterMenu.ItemViewHolder> {
   // private String[] mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;
    List<MenuModel> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecycleAdapterMenu(Context context, List<MenuModel> models) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = models;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableRecyclerView.ViewHolder {
        private TextView textView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.name);
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
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item_menu, null));

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Log.e("TAG","onBindViewHolder position = "+position);
        MenuModel item = mDataset.get(position);
        holder.textView.setText(item.getTitle());

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
        Log.e("TAG","recycle view item count = "+mDataset.size());

        return mDataset.size();
    }
}
