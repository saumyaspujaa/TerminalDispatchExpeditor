package com.digipodium.tde.user;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.digipodium.tde.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.digipodium.tde.Constants.PRIMARY_CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {


    private static final int NOTIFICATION_ID = 0;
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    String mobile, body;
    private NotificationManager mNotifyManager;

    private NotificationCompat.Builder getNotificationBuilder(Context c, CharSequence title, CharSequence text) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(c, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_stat_name);
        return notifyBuilder;
    }

    ;

    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        createNotificationChannel(context);
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if (message.toLowerCase().contains("delivery")) {
                        mobile = senderNum.replaceAll("\\s", "");
                        body = message.replaceAll("\\s", "+");
                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + body);

                        // Show Notification
                        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(context, "Delivery Notification", body);
                        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, "senderNum: " + mobile + ", message: " + message, duration);
                        toast.show();
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    public void createNotificationChannel(Context c) {
        mNotifyManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    c.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("delivery notification from" + c.getString(R.string.app_name));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

}