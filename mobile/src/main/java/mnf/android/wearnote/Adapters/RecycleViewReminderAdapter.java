package mnf.android.wearnote.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.activeandroid.query.Update;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mnf.android.wearnote.Config;
import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Model.ReminderModel;
import mnf.android.wearnote.R;

/**
 * Created by muneef on 05/02/17.
 */

public class RecycleViewReminderAdapter extends RecyclerView.Adapter<RecycleViewReminderAdapter.ViewHolder>{


    List<ReminderModel> mDataset;
    Context c;


    public RecycleViewReminderAdapter(Context mContext, List<ReminderModel> models) {
        this.c = mContext;
        this.mDataset = models;
       // itemsPendingRemoval = new ArrayList<>();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date, title;
        SwitchCompat switchRm;
        //  public ImageView thumbnail, overflow;
        View v;
        public ViewHolder(View view) {
            super(view);
            v =view;
            title = (TextView) view.findViewById(R.id.rm_title);
            date = (TextView) view.findViewById(R.id.rm_time);
            switchRm = (SwitchCompat) view.findViewById(R.id.rm_switch);

            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //  overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);

        return new RecycleViewReminderAdapter.ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ReminderModel model = mDataset.get(position);
        final String id = model.getIdn();
        Date date = model.getDate();

        holder.date.setText(DateFormat.getTimeInstance().format(date));
        Note notItem = Config.getNoteItem(model.getNoteid());
        List<Note> notes = Config.getNoteList();
        Log.e("adapter", "size = " + notes.size());
        if(notItem!=null) {
            holder.title.setText(notItem.getTitle());
        }
        if(model.getStatus()==1){
            holder.switchRm.setChecked(true);
        }else{
            holder.switchRm.setChecked(false);
        }

        holder.switchRm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("TAG","onCheckedChanged = "+isChecked+ " id = "+id);
                if (isChecked) {
                    new Update(ReminderModel.class)
                            .set("status = " + 1)
                            .where("idn = ?", id)
                            .execute();
                }else{
                    new Update(ReminderModel.class)
                            .set("status = " + 0)
                            .where("idn = ?", id)
                            .execute();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
