package it.bonny.app.wisetimer2.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Locale;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.StateType;
import it.bonny.app.wisetimer2.bean.TimerBean;
import it.bonny.app.wisetimer2.notification.NotificationReceiver;
import it.bonny.app.wisetimer2.utility.ChangeColorComponentsBean;
import it.bonny.app.wisetimer2.utility.MyBounceInterpolator;
import it.bonny.app.wisetimer2.utility.Util;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class TimerActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private final Context context = this;
    private final Activity activity = this;
    private TextView countTime, numSeries, stringView, numRounds, strRoundsTimerAct, strSeriesTimerAct;
    private ProgressBar progressBarCircle;
    private RelativeLayout borderBtn;
    private FloatingActionButton fabPreWorkPausePlay, fabPreWorkStop;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;
    private Animation myAnim, fadeIn, rotate180;
    private TextToSpeech tts;
    private MediaPlayer mediaPlayer, mediaPlayerStart;
    private TimerBean timerBean;
    private SettingBean settingBean;
    private final Util util = new Util();
    private boolean isPaused  = false, isClosedApp = true, isLocked = false, isFinished = false;
    private long timeLeft = 0, backPressedTime;
    private int tempoTotInSec = 0, numSeriesLeft, numRoundsLeft;
    private NotificationManagerCompat notificationManagerCompat;
    private FloatingActionButton lockDisplay;
    private final WiseToast wiseToast = new WiseToast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            timerBean = (TimerBean) getIntent().getSerializableExtra("timerBean");
            settingBean = (SettingBean) getIntent().getSerializableExtra("settingBean");
            if(settingBean != null){
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                if(settingBean != null) {
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingBean.isActiveAnalytics());
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, savedInstanceState);
            }
            setTheme(R.style.AppTheme);
            try {
                if (this.getSupportActionBar() != null)
                    this.getSupportActionBar().hide();
            } catch (NullPointerException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

            util.setAppLocale(settingBean.getLanguages(), context);
            setContentView(R.layout.activity_timer);
            if (settingBean.isDisplayOn()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            initElement();
            setProgressAndStartTimer(progressBarCircle, tempoTotInSec);
            fabPreWorkPausePlay.setOnClickListener(v -> playPauseFun());
            fabPreWorkStop.setOnClickListener(v -> stopCounter());

            lockDisplay.setOnClickListener(v -> {
                if (!isLocked) {
                    isLocked = true;
                    lockDisplay.setImageResource(R.drawable.lock_closed);
                    enableViews(findViewById(android.R.id.content).getRootView(), false);
                } else {
                    isLocked = false;
                    lockDisplay.setImageResource(R.drawable.lock_open);
                    enableViews(findViewById(android.R.id.content).getRootView(), true);
                }
            });

            lockDisplay.setOnLongClickListener(v -> {
                wiseToast.info(context, getString(R.string.disable_screen), Toast.LENGTH_SHORT).show();
                return true;
            });

            fabPreWorkPausePlay.setOnLongClickListener(v -> {
                wiseToast.info(context, getString(R.string.play_pause_timer_info), Toast.LENGTH_SHORT).show();
                return true;
            });
            fabPreWorkStop.setOnLongClickListener(v -> {
                wiseToast.info(context, getString(R.string.restart_timer_info), Toast.LENGTH_SHORT).show();
                return true;
            });
        } catch (Exception e) {
            util.errorReport(activity, e.toString(), settingBean, activity.getApplicationContext());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void setWorkRest(){
        int seconds, minutes;
        if(timerBean.workNotNull()){
            seconds = Integer.parseInt(timerBean.getWorkSec());
            minutes = Integer.parseInt(timerBean.getWorkMin());
            tempoTotInSec = (seconds + (minutes* 60)) * 1000;
            timerBean.setTimerState(StateType.WORK);
        }else if(timerBean.restNotNull()){
            seconds = Integer.parseInt(timerBean.getRestSec());
            minutes = Integer.parseInt(timerBean.getRestMin());
            tempoTotInSec = (seconds + (minutes* 60)) * 1000;
            timerBean.setTimerState(StateType.REST);
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

    private void playPauseFun(){
        if(!isPaused){
            fabPreWorkPausePlay.startAnimation(rotate180);
            if(countDownTimer != null)
                countDownTimer.cancel();
            fabPreWorkPausePlay.setImageResource(R.drawable.baseline_play_arrow_24);
            isPaused = true;
            fabPreWorkStop.show();
            fabPreWorkStop.startAnimation(myAnim);
        }else {
            fabPreWorkPausePlay.startAnimation(rotate180);
            fabPreWorkPausePlay.setImageResource(R.drawable.baseline_pause_24);
            isPaused = false;
            fabPreWorkStop.hide();
            startTimer(timeLeft);
        }
    }

    private void speakOut(String textToSpeech, final boolean isFinishedTimer) {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                runOnUiThread(() -> {
                });
            }
            @Override
            public void onDone(String s) {
                runOnUiThread(() -> {
                    if(isFinishedTimer)
                        stopCounter();
                });
            }
            @Override
            public void onError(String s) {
                runOnUiThread(() -> wiseToast.error(context, getString(R.string.error_textToSpeech), Toast.LENGTH_SHORT).show());
            }
        });
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
    }

    public void initElement(){
        notificationManagerCompat = NotificationManagerCompat.from(this);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        rotate180 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_180);
        fabPreWorkStop = findViewById(R.id.fabStop);
        fabPreWorkPausePlay = findViewById(R.id.fabPlayPause);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        borderBtn = findViewById(R.id.borderBtn);
        stringView = findViewById(R.id.stringaView);
        countTime = findViewById(R.id.counttime);
        numSeries = findViewById(R.id.numSerie);
        numRounds = findViewById(R.id.numRounds);
        strRoundsTimerAct = findViewById(R.id.strRoundsTimerAct);
        strSeriesTimerAct = findViewById(R.id.strSeriesTimerAct);
        lockDisplay = findViewById(R.id.lockDisplay);
        numSeriesLeft = util.convertStringInteger(timerBean.getNumSeries());
        numRoundsLeft = util.convertStringInteger(timerBean.getNumRounds());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        mediaPlayerStart = MediaPlayer.create(getApplicationContext(), R.raw.beep_start);
        tts = new TextToSpeech(context, this);
        myAnim.setInterpolator(interpolator);
        fabPreWorkStop.hide();
        numSeries.setText(timerBean.getNumSeries());
        numRounds.setText(timerBean.getNumRounds());
        isPaused = false;
        if(settingBean.isInfoPreWork()){
            tempoTotInSec = 6;
            tempoTotInSec = tempoTotInSec * 1000;
        }else {
            setWorkRest();
        }
        setBeanToChangeColorComponents();
    }

    public void startTimer(long tempo){
        if(settingBean.isVoice()){
            switch (timerBean.getTimerState()){
                case StateType.WORK:
                    speakOut(getString(R.string.start_work), false);
                    break;
                case StateType.REST:
                    speakOut(getString(R.string.start_rest), false);
                    break;
                case StateType.ROUND:
                    speakOut(getString(R.string.start_rest_round), false);
                    break;
            }
        }
        countDownTimer = new CountDownTimer(tempo,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                onRunningTimer(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                onFinishedTimer();
            }
        }.start();
    }

    public void onRunningTimer(long millisUntilFinished){
        String finalValue;
        boolean addAnim = false;
        int secLeft = (int) millisUntilFinished / 1000;
        if((secLeft <= 5 && secLeft > 1)){
            if(settingBean.isVibration())
                util.vibrateOn(vibrator, 150);
            if(settingBean.isSound())
                mediaPlayer.start();
        }else if(secLeft == 0){
            if(settingBean.isVibration())
                util.vibrateOn(vibrator, 500);
        }else if(secLeft == 1){
            if(settingBean.isVibration())
                util.vibrateOn(vibrator, 500);
            if(settingBean.isSound()){
                mediaPlayerStart.seekTo(725);
                mediaPlayerStart.start();
            }
        }
        if(secLeft <= 3) addAnim = true;
        finalValue = util.resultStringTime(secLeft);
        if(isClosedApp){
            String stringToNotify = getString(R.string.rounds) + ": " + numRoundsLeft + " " + getString(R.string.series) + ": " + numSeriesLeft + " ";
            switch (timerBean.getTimerState()){
                case StateType.PRE_WORK:
                    stringToNotify += getString(R.string.preWork) + ": ";
                    break;
                case StateType.WORK:
                    stringToNotify += getString(R.string.work) + ": ";
                    break;
                case StateType.REST:
                    stringToNotify += getString(R.string.rest) + ": ";
                    break;
                case StateType.ROUND:
                    stringToNotify += getString(R.string.restRounds) + ": ";
                    break;
            }
            showNotificationRunning(stringToNotify + finalValue);
        }
        timeLeft = millisUntilFinished;
        countTime.setText(finalValue);
        if(addAnim) countTime.startAnimation(fadeIn);
        progressBarCircle.setProgress(secLeft);
    }

    @SuppressLint("SetTextI18n")
    public void onFinishedTimer(){
        int seconds, minutes;
        if(StateType.PRE_WORK.equals(timerBean.getTimerState()) || StateType.ROUND.equals(timerBean.getTimerState())){
            if(StateType.ROUND.equals(timerBean.getTimerState()))
                if(settingBean.isVoice())
                    speakOut(getString(R.string.fine_rest_round), false);
            setWorkRest();
            setBeanToChangeColorComponents();
            setProgressAndStartTimer(progressBarCircle, tempoTotInSec);
        }else if(StateType.WORK.equals(timerBean.getTimerState())){
            numSeriesLeft--;
            if(numSeriesLeft <= 0){
                numRoundsLeft--;
                if(numRoundsLeft <= 0){
                    if(settingBean.isVoice())
                        speakOut(getString(R.string.fine_work), true);
                    else
                        stopCounter();
                }else {
                    numRounds.startAnimation(myAnim);
                    numRounds.setText("" + numRoundsLeft);
                    configureRestRound();
                }
            }else {
                if(timerBean.restNotNull()){
                    seconds = Integer.parseInt(timerBean.getRestSec());
                    minutes = Integer.parseInt(timerBean.getRestMin());
                    tempoTotInSec = (seconds + (minutes * 60)) * 1000;
                    timerBean.setTimerState(StateType.REST);
                }else {
                    seconds = Integer.parseInt(timerBean.getWorkSec());
                    minutes = Integer.parseInt(timerBean.getWorkMin());
                    tempoTotInSec = (seconds + (minutes * 60)) * 1000;
                    timerBean.setTimerState(StateType.WORK);
                }
                setBeanToChangeColorComponents();
                String stringApp = "" + numSeriesLeft;
                numSeries.startAnimation(myAnim);
                numSeries.setText(stringApp);
                if(settingBean.isVoice())
                    speakOut(getString(R.string.fine_work), false);
                setProgressAndStartTimer(progressBarCircle, tempoTotInSec);
            }
        }else if(StateType.REST.equals(timerBean.getTimerState())){
            if(util.isZeroEmpty(timerBean.getWorkSec()) && util.isZeroEmpty(timerBean.getWorkMin()))
                numSeriesLeft --;
            if(numSeriesLeft <= 0) {
                numRoundsLeft--;
                if(numRoundsLeft <= 0){
                    if (settingBean.isVoice())
                        speakOut(getString(R.string.fine_rest), true);
                    else
                        stopCounter();
                }else {
                    numRounds.startAnimation(myAnim);
                    numRounds.setText("" + numRoundsLeft);
                    configureRestRound();
                }
            }else{
                if(timerBean.workNotNull()){
                    seconds = Integer.parseInt(timerBean.getWorkSec());
                    minutes = Integer.parseInt(timerBean.getWorkMin());
                    tempoTotInSec = (seconds + (minutes * 60)) * 1000;
                    timerBean.setTimerState(StateType.WORK);
                }else if(timerBean.restNotNull()){
                    seconds = Integer.parseInt(timerBean.getRestSec());
                    minutes = Integer.parseInt(timerBean.getRestMin());
                    tempoTotInSec = (seconds + (minutes * 60)) * 1000;
                    timerBean.setTimerState(StateType.REST);
                }
                setBeanToChangeColorComponents();
                if(util.isZeroEmpty(timerBean.getWorkSec()) && util.isZeroEmpty(timerBean.getWorkMin())){
                    String stringApp = "" + numSeriesLeft;
                    numSeries.startAnimation(myAnim);
                    numSeries.setText(stringApp);
                }
                if(settingBean.isVoice())
                    speakOut(getString(R.string.fine_rest), false);
                setProgressAndStartTimer(progressBarCircle, tempoTotInSec);
            }
        }
    }

    public void setProgressAndStartTimer(ProgressBar progressBar, int tempoTotInSec){
        progressBar.setMax(tempoTotInSec / 1000);
        progressBar.setProgress( tempoTotInSec / 1000);
        startTimer(tempoTotInSec);
    }

    private void configureRestRound(){
        timerBean.setTimerState(StateType.ROUND);
        setBeanToChangeColorComponents();
        int seconds = util.convertStringInteger(timerBean.getRestRoundsSec());
        int minutes = util.convertStringInteger(timerBean.getRestRoundsMin());
        tempoTotInSec = (seconds + (minutes * 60)) * 1000;
        numSeriesLeft = util.convertStringInteger(timerBean.getNumSeries());
        String stringApp = "" + numSeriesLeft;
        numSeries.startAnimation(myAnim);
        numSeries.setText(stringApp);
        setProgressAndStartTimer(progressBarCircle, tempoTotInSec);
    }

    private void setBeanToChangeColorComponents(){
        ChangeColorComponentsBean bean = new ChangeColorComponentsBean();
        bean.setContext(context);
        bean.setBorderBtn(borderBtn);
        bean.setCountTime(countTime);
        bean.setSettingBean(settingBean);
        bean.setNumSeries(numSeries);
        bean.setNumRounds(numRounds);
        bean.setStringView(stringView);
        bean.setProgressBarCircle(progressBarCircle);
        bean.setFabPreWorkStop(fabPreWorkStop);
        bean.setFabPreWorkPausePlay(fabPreWorkPausePlay);
        bean.setLockDisplay(lockDisplay);
        bean.setType(timerBean.getTimerState());
        bean.setStrRoundsTimerAct(strRoundsTimerAct);
        bean.setStrSeriesTimerAct(strSeriesTimerAct);
        util.changeColorComponents(bean);
    }

    public void stopCounter(){
        if(countDownTimer != null)
            countDownTimer.cancel();
        if(isClosedApp)
            showNotificationFinished(getString(R.string.end_timer));
        isFinished = true;
        //Permette di inviare una risposta all'activty padre (TimerFragment)
        setResult(Util.RESULT_OK, new Intent());
        if(!isClosedApp && isFinished)
            finish();
    }

    //Notifications
    private void showNotificationRunning(String stringNotifications) {
        Intent activityIntent = new Intent(this, TimerActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        showNotificationTimer(stringNotifications, activityIntent);
    }

    private void showNotificationFinished(String stringNotifications) {
        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        showNotificationTimer(stringNotifications, activityIntent);
    }

    private void showNotificationTimer(String stringNotifications, Intent activityIntent) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent stopIntent = new Intent(this, NotificationReceiver.class);
        stopIntent.setAction(getString(R.string.descriptionStop));
        stopIntent.putExtra("timerBean", timerBean);
        Intent pauseIntent = new Intent(this, NotificationReceiver.class);
        pauseIntent.putExtra("stringNotifications", stringNotifications);
        pauseIntent.setAction(getString(R.string.descriptionPause));
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_icon_wisetimer_notify)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_wisetimer_notify))
                .setContentTitle(getString(R.string.timer_name))
                .setContentText(stringNotifications)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(getResources().getColor(R.color.transparent, getTheme()))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setDefaults(0)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .build();

        notificationManagerCompat.notify(0, notification);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(settingBean.isSideButtons()){
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    playPauseFun();
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    stopCounter();
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = -1;
            if(settingBean.isItalian()){
                result = tts.setLanguage(Locale.ITALY);
            }else if(settingBean.isEngland()){
                result = tts.setLanguage(Locale.UK);
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                wiseToast.error(context, getString(R.string.error_textToSpeech), Toast.LENGTH_SHORT).show();
            }
            tts.setPitch(1.2f);
            tts.setSpeechRate(1.6f);
        } else
            wiseToast.error(context, getString(R.string.error_textToSpeech), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            if(countDownTimer != null)
                countDownTimer.cancel();
            notificationManagerCompat.cancel(0);
            util.exitAppByOnBackPressed(this);
        } else {
            if(context != null)
                wiseToast.warning(context, getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManagerCompat.cancel(0);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        notificationManagerCompat.cancel(0);
        isClosedApp = false;
        if(isFinished)
            finish();
    }

    @Override
    public void onPause(){
        super.onPause();
        isClosedApp = true;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

}
