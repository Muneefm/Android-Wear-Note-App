package mnf.android.wearnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mnf.android.wearnote.Model.Note;
import mnf.android.wearnote.Model.ReminderModel;
import mnf.android.wearnote.tools.Reciever;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by muneef on 26/01/17.
 */

public class Config {

    public static String base64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoKWzEUVcKfYhX3wwDzR17+CRLwCy8Dpwxnjezr3OQEZmscrp5K3w5WC5KiqpROqrQxbTe1ON7TZij6guJogws5KRAOOxVa6sPKalTEs0zy4tnU9UDjAAFxCRSqH9X23D0Vb/Iu01Rc4tpEibOVsAYDJzIf/1yCx+Tx85lhVqbMVc7et9HBmtR8VyUt8bBtZp5Z5mvvBSbQnMifMbxtNYqnTDGJgZJWOfs4yIS9oa6XZhnNyO+AvO7Frh0zXlc8AVu3vEOlazdqFgv9Wa/paguHs24pZyM8CmXJGNpQ7TRp4LgPBTxS/BAde1j2vTQz/NBI27qCkuM1AwHqxdKwKp6wIDAQAB";
    public static String productIdAds ="removeads";
    public static int adclickLimit =1;
    public static int noteClickLimitToPremium =3;


    static int min = 1000;
    static int max = 999999999;
    static int i =0;
    public static String generateRandomNumberGenerate(){
       /* Random r = new Random();
        int ran = r.nextInt(max - min + 1) + min;
*/
        String uniqueID = getUUID();
        List<Note> notes =new Select()
                .all()
                .from(Note.class)
                .execute();
        if (notes!=null&&notes.size()!=0)
        for (Note note: notes) {
            if (note.idn.equals(uniqueID)){
                generateRandomNumberGenerate();
            }
        }
        return uniqueID;
    }

    public static String getUUID(){
        return   UUID.randomUUID().toString();

    }

    public static String generateRandomNumberReminder(){
        String uniqueIDRem = getUUID();

        List<ReminderModel> reminderModelList =new Select()
                .all()
                .from(ReminderModel.class)
                .execute();
        if (reminderModelList!=null&&reminderModelList.size()!=0)
            for (ReminderModel reminderModel: reminderModelList) {
                if (reminderModel.idn.equals(uniqueIDRem)){
                    generateRandomNumberGenerate();
                }
            }
        return uniqueIDRem;
    }
    public static int generateRandomInt(){
        Random r = new Random();
        int ran = r.nextInt(999 - 0 + 1) + min;
        return ran;
    }


    public static Note getNoteItem(String id){
        return new Select()
                .from(Note.class)
                .where("idn = ?", id)
                .executeSingle();
    }

    public static  List<Note> getNoteList(){
        Log.e("TAG","Config  function call" );
        List<Note> ret = new Select()
                .all()
                .from(Note.class)
                .execute();

        for (Note itm : ret) {
            Log.e("TAG","Config  class getNoteList   idn = "+itm.getIdn()+" title = "+itm.getTitle());

        }
        return ret;

    }

    public static int getExample(){
        i++;
        Log.e("tag","return value example = "+i);
        return i;
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
        Cache.clear();
        return new Select()
                .all()
                .from(ReminderModel.class)
                .execute();

    }



    public static void setReminder(Date date, Context context,String noteid){
        date.setSeconds(0);

        Log.e("Config","Config.setReminder note id = "+noteid+" date = "+date);
        String idReminder = generateRandomNumberReminder();
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
       // reminderModel.setTitle();
        reminderModel.save();
        Log.e("Config","reminder saved id = "+idReminder);

    }


    public static void loadImage(String url, ImageView imageView){
        Picasso.with(imageView.getContext())
                .load(url)
                .into( imageView);
    }




}
