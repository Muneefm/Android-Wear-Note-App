package mnf.android.wearnote.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Model.ReminderModel;

/**
 * Created by muneef on 05/02/17.
 */

public class Reciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG","Reciever onReceive");
        if((!intent.getExtras().getString("reminderid").equals(""))&&(!intent.getExtras().getString("noteid").equals(""))) {
            String idReminder = intent.getExtras().getString("reminderid");

            ReminderModel reminderModel = new Select()
                    .from(ReminderModel.class)
                    .where("idn = ?", idReminder)
                    .executeSingle();
            Log.e("TAG","Reciever status = "+reminderModel.getStatus());

            if (reminderModel.getStatus() == 1) {
                String id = intent.getExtras().getString("noteid");
                Log.e("TAG", "Reciever Extras id = " + id);

                Note note = new Select()
                        .from(Note.class)
                        .where("idn = ?", id)
                        .executeSingle();
                new SendNotification(context, note.getTitle(), note.getBody(),id).sendNotificationWear();
                new Delete().from(ReminderModel.class).where("idn = ?",idReminder).execute();
                Log.e("TAG", "Reciever delete id = " + idReminder);

            }
        }
    }
}
