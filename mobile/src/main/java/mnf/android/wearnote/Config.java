package mnf.android.wearnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;
import java.util.Random;

import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Model.ReminderModel;
import mnf.android.wearnote.tools.Reciever;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by muneef on 26/01/17.
 */

public class Config {
   static int min = 1000;
    static int max = 999999999;
    public static int generateRandomNumberGenerate(){
        Random r = new Random();
        int ran = r.nextInt(max - min + 1) + min;

        List<Note> notes =new Select()
                .all()
                .from(Note.class)
                .execute();
        if (notes!=null&&notes.size()!=0)
        for (Note note: notes) {
            if (note.idn==ran){
                generateRandomNumberGenerate();
            }
        }
        return ran;
    }

    public static int generateRandomNumberReminder(){
        Random r = new Random();
        int ran = r.nextInt(max - min + 1) + min;

        List<ReminderModel> reminderModelList =new Select()
                .all()
                .from(ReminderModel.class)
                .execute();
        if (reminderModelList!=null&&reminderModelList.size()!=0)
            for (ReminderModel reminderModel: reminderModelList) {
                if (reminderModel.idn==ran){
                    generateRandomNumberGenerate();
                }
            }
        return ran;
    }
    public static int generateRandomInt(){
        Random r = new Random();
        int ran = r.nextInt(999 - 0 + 1) + min;
        return ran;
    }


    public static Note getNoteItem(Integer id){
        return new Select()
                .from(Note.class)
                .where("idn = ?", id)
                .executeSingle();
    }

    public static List<Note> getNoteList(){

        return new Select()
                .all()
                .from(Note.class)
                .execute();

    }



    public static List<ReminderModel> getReminderList(){
        List<ReminderModel> listReminder = new Select()
                .all()
                .from(ReminderModel.class)
                .execute();
        Date now = new Date();
        for (ReminderModel model: listReminder) {
            if(model.getDate().before(now)){
                model.delete();
            }
        }
        return new Select()
                .all()
                .from(ReminderModel.class)
                .execute();

    }



    public static void setReminder(Date date, Context context,Integer noteid){
        int idReminder = generateRandomNumberReminder();
        Intent intent = new Intent(context, Reciever.class);
        intent.putExtra("noteid",noteid);
        intent.putExtra("reminderid",idReminder);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Config.generateRandomInt(), intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        // alarmManager.set(AlarmManager.RTC_WAKEUP,date.getTime() , pendingIntent); SystemClock.elapsedRealtime() + 5*1000
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime() , pendingIntent);
        ReminderModel reminderModel = new ReminderModel();
        reminderModel.setIdn(idReminder);
        reminderModel.setDate(date);
        reminderModel.setNoteid(noteid);
        reminderModel.setStatus(1);
        reminderModel.save();
    }





}
