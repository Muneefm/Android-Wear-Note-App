package mnf.android.wearnote.tools;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import mnf.android.wearnote.Config;
import mnf.android.wearnote.MainActivity;
import mnf.android.wearnote.R;

/**
 * Created by muneef on 05/02/17.
 */

public class SendNotification {
    static Context context;
    static String notificationContent="";
    static String notificationTitle="";
    static String uid = "";
    public SendNotification(Context c,String content,String title,String uid){
        this.context = c;
        this.notificationContent = content;
        this.notificationTitle = title;
        this.uid = uid;

    }

    public static void sendNotificationWear(){
        Log.e("TAG","sendNotificationWear method");

        int notificationId = Config.generateRandomInt();
// Build intent for notification content
        Intent viewIntent = new Intent(context, MainActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);


        Intent actionIntent = new Intent(context, MainActivity.class);
        actionIntent.putExtra("uid",uid);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(context, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

// Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_note_white,
                        "Open note ", actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_wear_black)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationContent)
                        .setSound(null)
                        .setContentIntent(viewPendingIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))

                        .setStyle(new NotificationCompat.BigTextStyle());

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
