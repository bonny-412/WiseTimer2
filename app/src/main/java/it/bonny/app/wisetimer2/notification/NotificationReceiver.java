package it.bonny.app.wisetimer2.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*String message = "";
        if(intent.getAction() != null){
            if(intent.getAction().equals(context.getString(R.string.descriptionStop))){
                Intent returnMainActivity = new Intent(context, MainActivity.class);
                returnMainActivity.putExtra("timerBean", intent.getSerializableExtra("timerBean"));
                context.startActivity(returnMainActivity);
            }else if(intent.getAction().equals(context.getString(R.string.descriptionPause))){
                message = context.getString(R.string.descriptionPause);
            }
        }*/
    }
}
