package it.bonny.app.wisetimer2.utility;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ContinuousLongClickListener implements View.OnTouchListener, View.OnLongClickListener{
    private static final int WHAT = 264776257;
    private final View.OnLongClickListener onLongClickListener;
    private final Handler handler;
    private final int delay;

    public ContinuousLongClickListener(View view, View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        delay = 750;
        handler = new Handler();
        view.setOnLongClickListener(this);
        view.setOnTouchListener(this);
    }
    @Override
    public boolean onLongClick(final View view) {
        try{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    onLongClickListener.onLongClick(view);

                    Message message = Message.obtain(handler, this);
                    message.what = WHAT;
                    handler.sendMessageDelayed(message, delay);
                }
            });
            return true;
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
            if (event.getAction() == MotionEvent.ACTION_UP)
                handler.removeMessages(WHAT);
        }catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return false;
    }
}
