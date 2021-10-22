package it.bonny.app.wisetimer2.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it.bonny.app.wisetimer2.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder ( this , getString(R.string.notification_channel_id))
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_icon_wisetimer_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_wisetimer_notify))
                    .setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
            if(notificationManager != null)
                notificationManager.notify( 0, notificationBuilder.build());
        }
        /*if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
        }*/

    }

    @Override
    public void onNewToken (@NonNull String token) {
        // Se si desidera inviare messaggi a questa istanza dell'applicazione o
        // gestisci gli abbonamenti di questa app sul lato server, invia il file
        // Token ID istanza sul server delle app.

    }

}
