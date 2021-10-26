package it.bonny.app.wisetimer2.manager.ui.stopwatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.StopwatchBean;
import it.bonny.app.wisetimer2.manager.MainActivity;
import it.bonny.app.wisetimer2.notification.NotificationReceiver;
import it.bonny.app.wisetimer2.utility.CustomAdapterLapStopwatch;
import it.bonny.app.wisetimer2.utility.MyBounceInterpolator;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.R;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;

public class StopwatchFragment extends Fragment {
    private View root;
    private SettingBean settingBean;
    private final Util util = new Util();
    private TextView countTimerStopwatch, stringCountTimeLap, countLap;
    private FloatingActionButton fabRestart, fabPlayPause, fabLap, fabShare, lockDisplay;
    private ImageView iconDetailsLap;
    private long millisecondTime, startTime = 0L, timeBuff, updateTime = 0L;
    private long millisecondTimeLap, startTimeLap, timeBuffLap, updateTimeLap = 0L;
    private Handler handler;
    private int seconds, minutes, milliSeconds, numLap = 0, secondsLap, minutesLap, milliSecondsLap;
    private final StopwatchBean stopwatchBest = new StopwatchBean();
    private final StopwatchBean stopwatchWorse = new StopwatchBean();
    private List<StopwatchBean> listElementsArrayList;
    private CustomAdapterLapStopwatch adapter;
    private Animation myAnim, rotate, rotate180, zoomIn, zoomIn2;
    private boolean isPaused = false, isClosedApp = true;
    private boolean isLocked = false;
    private LinearLayout layout_listLap, layoutLap;
    private FragmentActivity fragmentActivity;
    private NotificationManagerCompat notificationManagerCompat;
    private ProgressBar progressBar_stopwatch;
    private final WiseToast wiseToast = new WiseToast();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
            if(getActivity() != null)
                settingBean = util.getSettingBeanSaved(getActivity());
            if(settingBean != null){
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(root.getContext());
                if(settingBean != null)
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
            }
            fragmentActivity = this.getActivity();
            initElement();
            fabPlayPause.setOnClickListener(v -> {
                if(fragmentActivity.getActionBar() != null)
                    fragmentActivity.getActionBar().hide();
                playPauseFun();
            });

            fabRestart.setOnClickListener(v -> restartFun());

            fabLap.setOnClickListener(v -> lapFun());

            lockDisplay.setOnClickListener(v -> {
                if(!isLocked){
                    enableViews(root.getRootView(), false);
                    isLocked = true;
                    lockDisplay.setImageResource(R.drawable.lock_closed);
                }else {
                    enableViews(root.getRootView(), true);
                    isLocked = false;
                    lockDisplay.setImageResource(R.drawable.lock_open);
                }
            });
            fabShare.setOnClickListener(v -> {
                if(getActivity() != null){
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("it"));
                    String now = df.format(new Date());
                    String textToShare = getString(R.string.date_lap_text) + " " + now + "\n" + getString(R.string.hello_lap_text) + " ";
                    if(numLap == 1)
                        textToShare = textToShare + getString(R.string.numLap_lap_text) + "\n";
                    else
                        textToShare = textToShare + getString(R.string.numsLap_lap_text)+ "\n\n";
                    textToShare = textToShare + getString(R.string.lap_lap_text) + "    " + getString(R.string.time_lap_text) + "  \n";
                    int count = 1;
                    StringBuilder textToShareBuilder = new StringBuilder(textToShare);
                    for(StopwatchBean stopwatchBean: listElementsArrayList){
                        String resultLap = " ";
                        textToShareBuilder.append(count).append("  ").append(stopwatchBean.getTime());
                        if(StopwatchBean.best.equals(stopwatchBean.getResultLap())){
                            resultLap = getString(R.string.best) + " " + getString(R.string.lap_name);
                        }else if(StopwatchBean.worse.equals(stopwatchBean.getResultLap())){
                            resultLap = getString(R.string.worse) + " " + getString(R.string.lap_name);
                        }
                        textToShareBuilder.append(resultLap).append("\n");
                        count ++;
                    }
                    textToShare = textToShareBuilder.toString();
                    Intent shareIntent = new ShareCompat.IntentBuilder(getActivity())
                            .setType("text/plain")
                            .setText(textToShare)
                            .getIntent();

                    if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(shareIntent);
                    }
                }
            });
            lockDisplay.setOnLongClickListener(v -> {
                if(getContext() != null) {
                    wiseToast.info(getContext(), getString(R.string.disable_screen), Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            fabLap.setOnLongClickListener(v -> {
                if(getContext() != null)
                    wiseToast.info(getContext(), getString(R.string.lap_info), Toast.LENGTH_SHORT).show();
                return true;
            });
            fabShare.setOnLongClickListener(v -> {
                if(getContext() != null)
                    wiseToast.info(getContext(), getString(R.string.share_laps), Toast.LENGTH_SHORT).show();
                return true;
            });
            fabRestart.setOnLongClickListener(v -> {
                if(getContext() != null)
                    wiseToast.info(getContext(), getString(R.string.restart_info), Toast.LENGTH_SHORT).show();
                return true;
            });
            fabPlayPause.setOnLongClickListener(v -> {
                if(getContext() != null)
                    wiseToast.info(getContext(), getString(R.string.play_pause_info), Toast.LENGTH_SHORT).show();
                return true;
            });
            iconDetailsLap.setOnClickListener(v -> createDialogInfo());
            iconDetailsLap.setOnLongClickListener(v -> {
                if(getContext() != null)
                    wiseToast.info(getContext(), getString(R.string.show_details_lap), Toast.LENGTH_SHORT).show();
                return true;
            });
        }catch (Exception e) {
            util.errorReport(getActivity(), e.toString(), settingBean, getContext());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return root;
    }

    private final Runnable runnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            if (isClosedApp) {
                showNotificationTimer(getString(R.string.stopwatch_name) + " - " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }
            progressBar_stopwatch.setProgress(seconds);
            milliSeconds = (int) (updateTime % 1000);
            millisecondTimeLap = SystemClock.uptimeMillis() - startTimeLap;
            updateTimeLap = timeBuffLap + millisecondTimeLap;
            secondsLap = (int) (updateTimeLap / 1000);
            minutesLap = secondsLap / 60;
            secondsLap = secondsLap % 60;
            milliSecondsLap = (int) (updateTimeLap % 1000);
            String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "." + String.format("%03d", milliSeconds);
            String timeLap = String.format("%02d", minutesLap) + ":" + String.format("%02d", secondsLap) + "." + String.format("%03d", milliSecondsLap);
            countTimerStopwatch.setText(time);
            stringCountTimeLap.setText(timeLap);
            handler.postDelayed(this, 0);
        }
    };

    private void initElement(){
        notificationManagerCompat = NotificationManagerCompat.from(root.getContext());
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim = AnimationUtils.loadAnimation(root.getContext(), R.anim.bounce);
        rotate = AnimationUtils.loadAnimation(root.getContext(), R.anim.rotate);
        rotate180 = AnimationUtils.loadAnimation(root.getContext(), R.anim.rotate_180);
        zoomIn = AnimationUtils.loadAnimation(root.getContext(), R.anim.zoom_in);
        zoomIn2 = AnimationUtils.loadAnimation(root.getContext(), R.anim.zoom_in2);
        countTimerStopwatch = root.findViewById(R.id.countTimerStopwatch);
        layoutLap = root.findViewById(R.id.layoutLap);
        countLap = root.findViewById(R.id.countLap);
        iconDetailsLap = root.findViewById(R.id.iconDetailsLap);
        stringCountTimeLap = root.findViewById(R.id.stringCountTimeLap);
        fabRestart = root.findViewById(R.id.fabRestart);
        fabPlayPause = root.findViewById(R.id.fabPlayPause);
        fabShare = root.findViewById(R.id.fabShare);
        fabLap = root.findViewById(R.id.fabLap);
        progressBar_stopwatch = root.findViewById(R.id.progressBar_stopwatch);

        progressBar_stopwatch.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.drawable_circle_dark_blue));
        progressBar_stopwatch.setMax(60);
        progressBar_stopwatch.setProgress(0);

        fabLap.hide();
        fabShare.hide();
        ListView listLap = root.findViewById(R.id.listLap);
        handler = new Handler();
        listElementsArrayList = new ArrayList<>();
        adapter = new CustomAdapterLapStopwatch(root.getContext(), R.layout.row_list_lap_stopwatch, listElementsArrayList);
        listLap.setAdapter(adapter);
        layout_listLap = root.findViewById(R.id.layout_listLap);
        lockDisplay = root.findViewById(R.id.lockDisplay);
        myAnim.setInterpolator(interpolator);
    }

    private void showNotificationTimer(String stringNotifications) {
        try{
            Intent activityIntent = new Intent(root.getContext(), MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(root.getContext(), 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent stopIntent = new Intent(root.getContext(), NotificationReceiver.class);
            stopIntent.setAction(getString(R.string.descriptionStop));
            stopIntent.putExtra("", "");
            Intent pauseIntent = new Intent(root.getContext(), NotificationReceiver.class);
            pauseIntent.putExtra("stringNotifications", stringNotifications);
            pauseIntent.setAction(getString(R.string.descriptionPause));
            Notification notification = new NotificationCompat.Builder(root.getContext(), getString(R.string.notification_channel_id))
                    .setSmallIcon(R.drawable.ic_icon_wisetimer_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_wisetimer_notify))
                    .setContentTitle(getString(R.string.stopwatch_name))
                    .setContentText(stringNotifications)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(getResources().getColor(R.color.transparent, root.getContext().getTheme()))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setDefaults(0)
                    .setVibrate(new long[0])
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManagerCompat.notify(0, notification);
        }catch (Exception e){
            util.errorReport(getActivity(), e.toString(), settingBean, getContext());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        notificationManagerCompat.cancel(0);
        isClosedApp = false;
    }
    @Override
    public void onPause(){
        super.onPause();
        isClosedApp = true;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        notificationManagerCompat.cancel(0);
    }

    public void myOnKeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                playPauseFun();
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                lapFun();
                break;
        }
    }

    private void playPauseFun(){
        try{
            if(getActivity() != null){
                if(((MainActivity) getActivity()).getSupportActionBar() != null)
                    Objects.requireNonNull(((MainActivity) getActivity()).getSupportActionBar(), "getSupportActionBar must not be null").hide();
            }
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        MainActivity.isRunningStopwatch = true;
        lockDisplay.setVisibility(View.VISIBLE);

        if(!isPaused){
            fabPlayPause.startAnimation(rotate180);
            startTime = SystemClock.uptimeMillis();
            startTimeLap = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            isPaused = true;
            fabPlayPause.setImageResource(R.drawable.baseline_pause_24);
            isPaused = true;
            fabRestart.hide();
            fabLap.show();
            fabLap.startAnimation(myAnim);
        }else{
            fabPlayPause.startAnimation(rotate180);
            timeBuff += millisecondTime;
            timeBuffLap += millisecondTimeLap;
            handler.removeCallbacks(runnable);
            fabPlayPause.setImageResource(R.drawable.baseline_play_arrow_24);
            isPaused = false;
            fabLap.hide();
            fabRestart.show();
            fabRestart.startAnimation(myAnim);
        }
    }

    private void restartFun(){
        try{
            if(getActivity() != null){
                if(((MainActivity) getActivity()).getSupportActionBar() != null)
                    Objects.requireNonNull(((MainActivity) getActivity()).getSupportActionBar(), "getSupportActionBar must not be null").show();
            }
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        MainActivity.isRunningStopwatch = false;
        lockDisplay.setVisibility(View.GONE);
        millisecondTime = 0L;
        startTime = 0L;
        timeBuff = 0L;
        updateTime = 0L;
        seconds = 0;
        minutes = 0;
        milliSeconds = 0;
        millisecondTimeLap = 0L;
        startTimeLap = 0L;
        timeBuffLap = 0L;
        updateTimeLap = 0L;
        secondsLap = 0;
        minutesLap = 0;
        milliSecondsLap = 0;

        countTimerStopwatch.setText(R.string.start_time_stopwatch);
        stringCountTimeLap.setText(R.string.start_time_stopwatch);

        fabRestart.startAnimation(rotate);
        listElementsArrayList.clear();
        adapter.notifyDataSetChanged();
        progressBar_stopwatch.setProgress(0);
        layout_listLap.setVisibility(View.INVISIBLE);
        layoutLap.setVisibility(View.INVISIBLE);
        notificationManagerCompat.cancel(0);
        isClosedApp = false;
        fabShare.hide();
        numLap = 0;
        iconDetailsLap.setVisibility(View.GONE);
    }

    private void lapFun(){
        numLap = listElementsArrayList.size() + 1;
        StopwatchBean stopwatchBean = new StopwatchBean();
        stopwatchBean.setLap(getString(R.string.lap) + " " + numLap);
        stopwatchBean.setTime(countTimerStopwatch.getText().toString());
        stopwatchBean.setTimePassed(getString(R.string.time_passed_lap) + ": " + stringCountTimeLap.getText().toString());
        stopwatchBean.setNumLap(numLap);
        stopwatchBean.setMinutes(minutesLap);
        stopwatchBean.setSeconds(secondsLap);
        stopwatchBean.setMillisSeconds(milliSecondsLap);
        if(numLap == 1){
            stopwatchBest.setMinutes(minutesLap);
            stopwatchBest.setSeconds(secondsLap);
            stopwatchBest.setMillisSeconds(milliSecondsLap);
            stopwatchBest.setNumLap(numLap);
            stopwatchBean.setResultLap(StopwatchBean.best);
            listElementsArrayList.add(stopwatchBean);
        }else {
            listElementsArrayList.add(stopwatchBean);
            listElementsArrayList.sort(Collections.reverseOrder());
            getBestWorseLap();
        }
        adapter.notifyDataSetChanged();
        layoutLap.setVisibility(View.VISIBLE);
        countLap.startAnimation(zoomIn);
        String numLapString = "" + numLap;
        countLap.setText(numLapString);
        millisecondTimeLap = 0L;
        startTimeLap = 0L;
        timeBuffLap = 0L;
        updateTimeLap = 0L;
        secondsLap = 0;
        minutesLap = 0;
        milliSecondsLap = 0;
        startTimeLap = SystemClock.uptimeMillis();
        if(numLap == 1){
            layout_listLap.setVisibility(View.VISIBLE);
            fabShare.startAnimation(zoomIn2);
            fabShare.show();
            iconDetailsLap.setVisibility(View.VISIBLE);
        }
    }

    private void enableViews(View v, boolean enabled) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0;i<vg.getChildCount();i++) {
                if(vg.getChildAt(i).getId() != lockDisplay.getId()){
                    enableViews(vg.getChildAt(i), enabled);
                }
            }
        }
        v.setEnabled(enabled);
    }

    @SuppressLint("DefaultLocale")
    private String getMediaLap() {
        String mediaLap = "";
        int minMediaLap, secMediaLap, millisSecMediaLap;
        long timeApp = 0;
        if(listElementsArrayList != null && listElementsArrayList.size() > 0){
            for(StopwatchBean bean: listElementsArrayList){
                timeApp = timeApp + ((long) bean.getMinutes() * 60 * 1000) + (bean.getSeconds() * 1000L) + bean.getMillisSeconds();
            }
            timeApp = timeApp / listElementsArrayList.size();
            secMediaLap = (int) (timeApp / 1000);
            minMediaLap = secMediaLap / 60;
            secMediaLap = secMediaLap % 60;
            millisSecMediaLap = (int) (timeApp % 1000);
            mediaLap = String.format("%02d", minMediaLap) + ":" + String.format("%02d", secMediaLap) + "." + String.format("%03d", millisSecMediaLap);
        }
        return mediaLap;
    }

    private void getBestWorseLap(){
        int count = 0;
        for(StopwatchBean bean: listElementsArrayList){
            boolean foundBest = false;
            bean.setResultLap(null);
            if(bean.getMinutes() < stopwatchBest.getMinutes()){
                bean.setNumIndexInList(count);
                addBestLap(bean);
                foundBest = true;
            }else if(bean.getMinutes() == stopwatchBest.getMinutes()){
                if(bean.getSeconds() < stopwatchBest.getSeconds()){
                    bean.setNumIndexInList(count);
                    addBestLap(bean);
                    foundBest = true;
                }else if(bean.getSeconds() == stopwatchBest.getSeconds()){
                    if(bean.getMillisSeconds() < stopwatchBest.getMillisSeconds()){
                        bean.setNumIndexInList(count);
                        addBestLap(bean);
                        foundBest = true;
                    }else if(bean.getMillisSeconds() == stopwatchBest.getMillisSeconds()){
                        bean.setNumIndexInList(count);
                        addBestLap(bean);
                        foundBest = true;
                    }
                }
            }
            //WORSE
            if(!foundBest){
                if(bean.getMinutes() > stopwatchWorse.getMinutes()){
                    bean.setNumIndexInList(count);
                    addWorseLap(bean);
                }else if(bean.getMinutes() == stopwatchWorse.getMinutes()){
                    if(bean.getSeconds() > stopwatchWorse.getSeconds()){
                        bean.setNumIndexInList(count);
                        addWorseLap(bean);
                    }else if(bean.getSeconds() == stopwatchWorse.getSeconds()){
                        if(bean.getMillisSeconds() > stopwatchWorse.getMillisSeconds()){
                            bean.setNumIndexInList(count);
                            addWorseLap(bean);
                        }else if(bean.getMillisSeconds() == stopwatchWorse.getMillisSeconds()){
                            bean.setNumIndexInList(count);
                            addWorseLap(bean);
                        }
                    }
                }
            }
            count ++;
        }
        listElementsArrayList.get(stopwatchBest.getNumIndexInList()).setResultLap(StopwatchBean.best);
        listElementsArrayList.get(stopwatchWorse.getNumIndexInList()).setResultLap(StopwatchBean.worse);
    }

    private void addWorseLap(StopwatchBean bean){
        stopwatchWorse.copy(bean);
    }
    private void addBestLap(StopwatchBean bean){
        stopwatchBest.copy(bean);
    }

    @SuppressLint("DefaultLocale")
    private void createDialogInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyleDark);
        View viewInfoDialog = View.inflate(getContext(), R.layout.alert_show_details, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        final TextView numLapTime = viewInfoDialog.findViewById(R.id.numLap);
        final TextView bestLapTime = viewInfoDialog.findViewById(R.id.bestLapTime);
        final TextView worseLapTime = viewInfoDialog.findViewById(R.id.worseLapTime);
        final TextView mediaLapTime = viewInfoDialog.findViewById(R.id.mediaLapTime);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                if(getContext() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        String bestTime = String.format("%02d", stopwatchBest.getMinutes()) + ":" + String.format("%02d", stopwatchBest.getSeconds()) +
                "." + String.format("%03d", stopwatchBest.getMillisSeconds());
        String worseTime = String.format("%02d", stopwatchWorse.getMinutes()) + ":" + String.format("%02d", stopwatchWorse.getSeconds()) +
                "." + String.format("%03d", stopwatchWorse.getMillisSeconds());
        String str = "" + numLap;
        numLapTime.setText(str);
        bestLapTime.setText(bestTime);
        worseLapTime.setText(worseTime);
        mediaLapTime.setText(getMediaLap());
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        if(dialog != null)
            dialog.show();
    }

    //Methods used by other classes such as MainActivity
    public NotificationManagerCompat getNotificationManagerCompat() {
        return notificationManagerCompat;
    }
}
