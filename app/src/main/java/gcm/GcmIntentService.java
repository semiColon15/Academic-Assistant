package gcm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hooper.kenneth.academicassistant.ChooseConversationLecturerActivity;
import com.hooper.kenneth.academicassistant.ChooseConversationStudentActivity;
import com.hooper.kenneth.academicassistant.LogInActivity;
import com.hooper.kenneth.academicassistant.R;

import java.util.List;

public class GcmIntentService extends IntentService {
    public static final String TAG = "GCM Demo";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public GcmIntentService() {
        super(GcmIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        Log.i(TAG, "Received extras: " + extras.toString());
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            createNotification("Send error occured!", "GCM");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            createNotification("Messages deleted on the server!", "GCM");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            String message = extras.getString("message");
            String senderName = extras.getString("senderName");
            createNotification(message, senderName);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);//Signals "work completed", must be called to release wakelock
    }

    private void createNotification(String message, String senderName) {
        //boolean isActivityRunning;
        Intent notificationIntent = new Intent(this, LogInActivity.class);
        /*if(LogInActivity.loggedInUserType) {
            isActivityRunning = ChooseConversationLecturerActivity.isActivityRunning;
        }else {
            isActivityRunning = ChooseConversationStudentActivity.isActivityRunning;
        }*/
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setContentTitle(message)
                            .setSubText(senderName)
                            .setSmallIcon(R.drawable.chatify)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setContentIntent(pendingIntent);

            //Vibration
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

            //LED
            builder.setLights(Color.RED, 3000, 3000);

            //Ton
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
