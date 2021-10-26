package it.bonny.app.wisetimer2.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.bonny.app.wisetimer2.bean.SettingBean;
import it.bonny.app.wisetimer2.bean.StateType;
import it.bonny.app.wisetimer2.bean.TimerBean;
import it.bonny.app.wisetimer2.db.ManagerDB;
import it.bonny.app.wisetimer2.wisetoast.WiseToast;
import it.bonny.app.wisetimer2.R;

public class Util {
    public static  final String PREFS_NAME_FILE = "MyPrefsFile";
    public static final int RESULT_OK = -1;
    public Util(){}

    public String addSeriesRounds(String valueSeries){
        if(!isZeroEmpty(valueSeries)){
            int numSeriesInt = Integer.parseInt(valueSeries);
            numSeriesInt = numSeriesInt + 1;
            valueSeries = "" + numSeriesInt;
        }else {
            valueSeries = "1";
        }
        return valueSeries;
    }

    public String removeSeriesRounds(String valueSeries){
        if(!isZeroEmpty(valueSeries)){
            int numSeriesInt = Integer.parseInt(valueSeries);
            numSeriesInt = numSeriesInt - 1;
            if(numSeriesInt <= 0){
                valueSeries = "1";
            }else {
                valueSeries = "" + numSeriesInt;
            }
        }else{
            valueSeries = "1";
        }
        return valueSeries;
    }

    public HashMap<String, String> addSeconds(String valueSec, String valueMin){
        HashMap<String, String> hashMap = new HashMap<>();
        if(!isEmpty(valueSec) && !isEmpty(valueMin)){
            int valueSecInt = Integer.parseInt(valueSec);
            int valueMinInt = Integer.parseInt(valueMin);
            valueSecInt = valueSecInt + 1;
            if(valueSecInt >= 60){
                valueSec = "00";
                valueMinInt = valueMinInt + 1;
                if(valueMinInt < 10){
                    valueMin = "0" + valueMinInt;
                }else {
                    valueMin = "" + valueMinInt;
                }
            }else if(valueSecInt < 10){
                valueSec = "0" + valueSecInt;
            }else {
                valueSec = "" + valueSecInt;
            }
        }else {
            valueMin = "00";
            valueSec = "00";
        }
        hashMap.put("sec", valueSec);
        hashMap.put("min", valueMin);

        return hashMap;
    }

    public HashMap<String, String> removeSeconds(String valueSec, String valueMin){
        HashMap<String, String> hashMap = new HashMap<>();
        if(!isEmpty(valueSec) && !isEmpty(valueMin)){
            int valueSecInt = Integer.parseInt(valueSec);
            int valueMinInt = Integer.parseInt(valueMin);
            valueSecInt = valueSecInt - 1;
            if(valueSecInt < 0 && valueMinInt > 0){
                valueSec = "59";
                valueMinInt = valueMinInt - 1;
                if(valueMinInt <= 0){
                    valueMin = "00";
                }else if(valueMinInt <= 9){
                    valueMin = "0" + valueMinInt;
                }else{
                    valueMin = "" + valueMinInt;
                }
            }else if(valueSecInt < 10 && valueSecInt >= 0){
                valueSec = "0" + valueSecInt;
            }else if(valueSecInt >= 10){
                valueSec = "" + valueSecInt;
            }
        }else {
            valueMin = "00";
            valueSec = "00";
        }
        hashMap.put("sec", valueSec);
        hashMap.put("min", valueMin);

        return hashMap;
    }

    public TimerBean countSecond(String sec, String min){
        TimerBean timerBean = new TimerBean();
        if(isEmpty(sec))
            sec = "00";
        if(isEmpty(min))
            min = "00";
        if(isEmpty(sec) && isEmpty(min))
            sec = "00";
        timerBean.setNumSec(sec);
        timerBean.setNumMin(min);
        int number = Integer.parseInt(timerBean.getNumSec());
        if(number >= 60){
            number = number - 60;
            int numMin = Integer.parseInt(timerBean.getNumMin());
            numMin = numMin + 1;
            if(numMin >= 10){
                timerBean.setNumMin("" + numMin);
            }else {
                timerBean.setNumMin("0" + numMin);
            }
        }
        if(number < 60){
            if(number >= 10){
                timerBean.setNumSec("" + number);
            }else {
                timerBean.setNumSec("0" + number);
            }
        }
        return timerBean;
    }

    public TimerBean countSecondLongClickAdd(String sec, String min, String count){
        TimerBean bean = new TimerBean();
        if(isEmpty(sec) && isEmpty(min))
            sec = "00";
        bean.setNumSec(sec);
        if(isEmpty(min))
            min = "00";
        bean.setNumMin(min);
        int number = Integer.parseInt(count);
        while (number >= 60){
            number = number - 60;
            int numMin = Integer.parseInt(min);
            numMin = numMin + 1;
            if(numMin >= 10){
                bean.setNumMin("" + numMin);
            }else {
                bean.setNumMin("0" + numMin);
            }
        }
        if(number >= 10){
            bean.setNumSec("" + number);
        }else {
            bean.setNumSec("0" + number);
        }

        return bean;
    }

    public TimerBean countSecondLongClickRemove(String sec, String min, String count){
        TimerBean bean = new TimerBean();
        if(isEmpty(sec) && isEmpty(min))
            sec = "00";
        bean.setNumSec(sec);
        if(isEmpty(min))
            min = "00";
        bean.setNumMin(min);
        int number = Integer.parseInt(count);
        int secInt = Integer.parseInt(sec);
        if(number >= 60)
            number = number - 60;
        if((secInt - 10) < 0){
            int numMin = Integer.parseInt(min);
            numMin = numMin - 1;
            if(numMin >= 10){
                bean.setNumMin("" + numMin);
            }else {
                bean.setNumMin("0" + numMin);
            }
        }
        if(number >= 10){
            bean.setNumSec("" + number);
        }else {
            bean.setNumSec("0" + number);
        }

        return bean;
    }

    public String resultStringTime(int seconds){
        int min = 0, sec;
        String finalString, numMin, numSec;
        if(seconds >= 60){
            min = seconds / 60;
            sec = seconds - (min * 60);
        }else {
            sec = seconds;
        }
        if(min >= 10){
            numMin = "" + min;
        }else{
            numMin = "0" + min;
        }
        if(sec >= 10){
            numSec = "" + sec;
        }else {
            numSec = "0" + sec;
        }
        finalString = numMin + ":" + numSec;
        return finalString;
    }

    public void changeColorComponents(ChangeColorComponentsBean bean){
        int color = 0, colorBorder = 0, colorDrawable = 0;
        String textType = "";
        bean.getProgressBarCircle().setBackground(ContextCompat.getDrawable(bean.getContext(), R.drawable.drawable_circle_dark_blue));
        if(StateType.WORK.equals(bean.getType())){
            colorDrawable = R.drawable.drawable_circle_green;
            colorBorder = R.drawable.border_btn_work;
            color = R.color.newColorGreen;
            textType = bean.getContext().getString(R.string.work);
        }else if(StateType.PRE_WORK.equals(bean.getType())){
            colorDrawable = R.drawable.drawable_circle_yellow;
            colorBorder = R.drawable.border_btn_pre;
            color = R.color.newColorRed;
            textType = bean.getContext().getString(R.string.preWork);
        }else if(StateType.REST.equals(bean.getType()) || StateType.ROUND.equals(bean.getType())){
            colorDrawable = R.drawable.drawable_circle_blue;
            colorBorder = R.drawable.border_btn_rest;
            color = R.color.newColorBlue;
            if(StateType.ROUND.equals(bean.getType()))
                textType = bean.getContext().getString(R.string.restRounds);
            else
                textType = bean.getContext().getString(R.string.rest);
        }
        bean.getProgressBarCircle().setProgressDrawable(
                ContextCompat.getDrawable(bean.getContext(), colorDrawable));
        bean.getBorderBtn().setBackground(ContextCompat.getDrawable(bean.getContext(), colorBorder));
        bean.getFabPreWorkPausePlay().setBackgroundTintList(ContextCompat.getColorStateList(bean.getContext(), color));
        bean.getFabPreWorkStop().setBackgroundTintList(ContextCompat.getColorStateList(bean.getContext(), color));
        bean.getLockDisplay().setBackgroundTintList(ContextCompat.getColorStateList(bean.getContext(), color));
        bean.getCountTime().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getCountTime().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getNumSeries().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getNumRounds().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getStringView().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getStrSeriesTimerAct().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getStrRoundsTimerAct().setTextColor(ContextCompat.getColor(bean.getContext(), color));
        bean.getStringView().setText(textType);
    }

    public void getAlertDialogDeleteSettings(SettingBean settingBean, final Activity activity, final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyleDark);
        WiseToast wiseToast = new WiseToast();
        View viewInfoDialog = View.inflate(context, R.layout.alert_reset_settings, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        Button buttonSend = viewInfoDialog.findViewById(R.id.buttonSend);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        buttonSend.setOnClickListener(v -> {
            deleteSettingBean(activity);
            wiseToast.success(context, context.getString(R.string.alert_delete_message_yes), Toast.LENGTH_SHORT).show();
        });
        if(dialog != null)
            dialog.show();
    }

    public void saveTimerBean(TimerBean timerBean, Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(UtilityName.NAME, timerBean.getName());
        editor.putString(UtilityName.NUM_SERIES, checkNumber(timerBean.getNumSeries(), 1));
        if(!"".equals(timerBean.getNumMin())  && timerBean.getNumMin() != null)
            editor.putString(UtilityName.NUM_MIN, checkNumber(timerBean.getNumMin(), 0));
        if(!"".equals(timerBean.getNumSec())  && timerBean.getNumSec() != null)
            editor.putString(UtilityName.NUM_SEC, checkNumber(timerBean.getNumSec(), 0));
        editor.putString(UtilityName.WORK_SEC, checkNumber(timerBean.getWorkSec(), 0));
        editor.putString(UtilityName.WORK_MIN, checkNumber(timerBean.getWorkMin(), 0));
        editor.putString(UtilityName.REST_MIN, checkNumber(timerBean.getRestMin(), 0));
        editor.putString(UtilityName.REST_SEC, checkNumber(timerBean.getRestSec(), 0));
        editor.putBoolean(UtilityName.CHECKED, timerBean.isChecked());
        editor.putString(UtilityName.NUM_ROUNDS, checkNumber(timerBean.getNumRounds(), 1));
        editor.putString(UtilityName.REST_ROUNDS_MIN, checkNumber(timerBean.getRestRoundsMin(), 0));
        editor.putString(UtilityName.REST_ROUNDS_SEC, checkNumber(timerBean.getRestRoundsSec(), 0));
        editor.apply();
    }

    public void saveSettingBean(SettingBean settingBean, Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(activity.getString(R.string.id_voice), settingBean.isVoice());
        editor.putBoolean(activity.getString(R.string.id_sound), settingBean.isSound());
        editor.putBoolean(activity.getString(R.string.id_vibration), settingBean.isVibration());
        editor.putString(activity.getString(R.string.id_languages), settingBean.getLanguages());
        editor.putBoolean(activity.getString(R.string.id_infoPreWork), settingBean.isInfoPreWork());
        editor.putBoolean(activity.getString(R.string.id_display_on), settingBean.isDisplayOn());
        editor.putBoolean(activity.getString(R.string.id_side_buttons), settingBean.isSideButtons());
        editor.putBoolean(activity.getString(R.string.id_showAlertSadeButtons), settingBean.isShowAlertSadeButtons());
        editor.putBoolean(activity.getString(R.string.id_active_crash), settingBean.isActiveCrash());
        editor.putBoolean(activity.getString(R.string.id_active_analytics), settingBean.isActiveAnalytics());
        editor.putBoolean(activity.getString(R.string.id_active_messaging), settingBean.isActiveMessaging());
        editor.apply();
    }

    private void deleteSettingBean(Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(activity.getString(R.string.id_voice));
        editor.remove(activity.getString(R.string.id_sound));
        editor.remove(activity.getString(R.string.id_vibration));
        editor.remove(activity.getString(R.string.id_languages));
        editor.remove(activity.getString(R.string.id_infoPreWork));
        editor.remove(activity.getString(R.string.id_display_on));
        editor.remove(activity.getString(R.string.id_side_buttons));
        editor.remove(activity.getString(R.string.id_showAlertSadeButtons));
        editor.remove(activity.getString(R.string.id_active_crash));
        editor.remove(activity.getString(R.string.id_active_analytics));
        editor.remove(activity.getString(R.string.id_active_messaging));
        editor.apply();
        activity.recreate();
    }

    public void deleteTimerBean(Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(UtilityName.NAME);
        editor.remove(UtilityName.NUM_SERIES);
        editor.remove(UtilityName.NUM_MIN);
        editor.remove(UtilityName.NUM_SEC);
        editor.remove(UtilityName.WORK_SEC);
        editor.remove(UtilityName.WORK_MIN);
        editor.remove(UtilityName.REST_MIN);
        editor.remove(UtilityName.REST_SEC);
        editor.remove(UtilityName.CHECKED);
        editor.remove(UtilityName.NUM_ROUNDS);
        editor.remove(UtilityName.REST_ROUNDS_MIN);
        editor.remove(UtilityName.REST_ROUNDS_SEC);
        editor.apply();
        activity.recreate();
    }

    public TimerBean getTimerBeanSaved(Activity activity){
        TimerBean timerBean = new TimerBean();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE);
        timerBean.setName(sharedPreferences.getString(UtilityName.NAME, activity.getString(R.string.timerUsedNameCustomized)));
        timerBean.setNumSeries(sharedPreferences.getString(UtilityName.NUM_SERIES, "1"));
        timerBean.setNumSec(sharedPreferences.getString(UtilityName.NUM_SEC, "00"));
        timerBean.setNumMin(sharedPreferences.getString(UtilityName.NUM_MIN, "00"));
        timerBean.setWorkSec(sharedPreferences.getString(UtilityName.WORK_SEC, "00"));
        timerBean.setWorkMin(sharedPreferences.getString(UtilityName.WORK_MIN, "00"));
        timerBean.setRestSec(sharedPreferences.getString(UtilityName.REST_SEC, "00"));
        timerBean.setRestMin(sharedPreferences.getString(UtilityName.REST_MIN, "00"));
        timerBean.setChecked(sharedPreferences.getBoolean(UtilityName.CHECKED, false));
        timerBean.setNumRounds(sharedPreferences.getString(UtilityName.NUM_ROUNDS, "1"));
        timerBean.setRestRoundsMin(sharedPreferences.getString(UtilityName.REST_ROUNDS_MIN, "00"));
        timerBean.setRestRoundsSec(sharedPreferences.getString(UtilityName.REST_ROUNDS_SEC, "00"));
        return timerBean;
    }

    public SettingBean getSettingBeanSaved(Activity activity){
        SettingBean settingBean = new SettingBean();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE);
        settingBean.setVoice(sharedPreferences.getBoolean(activity.getString(R.string.id_voice), false));
        settingBean.setSound(sharedPreferences.getBoolean(activity.getString(R.string.id_sound), false));
        settingBean.setVibration(sharedPreferences.getBoolean(activity.getString(R.string.id_vibration), false));
        settingBean.setLanguages(sharedPreferences.getString(activity.getString(R.string.id_languages), findLanguageDevice(activity.getApplicationContext())));
        settingBean.setInfoPreWork(sharedPreferences.getBoolean(activity.getString(R.string.id_infoPreWork), true));
        settingBean.setDisplayOn(sharedPreferences.getBoolean(activity.getString(R.string.id_display_on), false));
        settingBean.setSideButtons(sharedPreferences.getBoolean(activity.getString(R.string.id_side_buttons), false));
        settingBean.setShowAlertSadeButtons(sharedPreferences.getBoolean(activity.getString(R.string.id_showAlertSadeButtons), false));
        settingBean.setActiveCrash(sharedPreferences.getBoolean(activity.getString(R.string.id_active_crash), true));
        settingBean.setActiveAnalytics(sharedPreferences.getBoolean(activity.getString(R.string.id_active_analytics), true));
        settingBean.setActiveMessaging(sharedPreferences.getBoolean(activity.getString(R.string.id_active_messaging), true));
        return settingBean;
    }

    public void setAppLocale(String localeCode, Context context){
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(new Locale(localeCode.toLowerCase()));
        context.createConfigurationContext(config);
    }

    public void vibrateOn(Vibrator vibrator, long time){
        vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public List<TimerBean> getTimerBeansFromCursor (Cursor cursor){
        List<TimerBean> timerBeans = new ArrayList<>();
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    TimerBean timerBean = getTimerBeanFromCursor(cursor);
                    if(timerBean != null)
                        timerBeans.add(timerBean);
                }while (cursor.moveToNext());
            }
        }
        return  timerBeans;
    }

    private TimerBean getTimerBeanFromCursor(Cursor cursor){
        TimerBean timerBean = null;
        try {
            if(cursor != null){
                timerBean = new TimerBean();
                timerBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_ID))));
                timerBean.setName(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_NOME)));
                timerBean.setNumSeries(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_NUM_SERIES)));
                timerBean.setWorkMin(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_WORK_MIN)));
                timerBean.setWorkSec(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_WORK_SEC)));
                timerBean.setRestMin(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_REST_MIN)));
                timerBean.setRestSec(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_REST_SEC)));
                timerBean.setNumRounds(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_NUM_ROUNDS)));
                timerBean.setRestRoundsSec(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_REST_ROUNDS_SEC)));
                timerBean.setRestRoundsMin(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_REST_ROUNDS_MIN)));
                long checked = cursor.getLong(cursor.getColumnIndex(ManagerDB.KEY_CHECKED));
                if(checked == 0)
                    timerBean.setChecked(false);
                else if(checked == 1)
                    timerBean.setChecked(true);
            }
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return timerBean;
    }

    public boolean isZeroEmpty(String value){
        boolean result = true;
        if(value != null && !"".equals(value) && !"0".equals(value) && !"00".equals(value))
            result = false;
        return  result;
    }

    private boolean isEmpty(String value){
        boolean result = true;
        if(value != null && !"".equals(value))
            result = false;
        return  result;
    }

    public void exitAppByOnBackPressed(Context context){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(a);
    }

    public void errorReport(final Activity activity, final String errorText, SettingBean settingBean, final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyleDark);;
        View viewInfoDialog = View.inflate(context, R.layout.alert_error_report, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        Button buttonSend = viewInfoDialog.findViewById(R.id.buttonSend);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        buttonSend.setOnClickListener(v -> sendEmail(errorText, "WiseTimer Error", activity, context));
        if(dialog != null)
            dialog.show();
    }

    public void sendEmail(String textEmail, String objectEmail, final Activity activity, final Context context) {
        byte[] data = Base64.decode("Ym9ubnkuc3ZpbHVwcG9AZ21haWwuY29t", Base64.DEFAULT);
        String[] TO = {new String(data, StandardCharsets.UTF_8)};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        WiseToast wiseToast = new WiseToast();

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, objectEmail);
        emailIntent.putExtra(Intent.EXTRA_TEXT, textEmail);

        try {
            activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_email)));
            activity.finish();
        } catch (android.content.ActivityNotFoundException ex) {
            wiseToast.error(context, activity.getString(R.string.send_email_error), Toast.LENGTH_SHORT).show();
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }

    public int convertStringInteger(String string){
        int integer = 0;
        try{
            if(string != null && !"".equals(string))
                integer = Integer.parseInt(string);
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return integer;
        }
        return integer;
    }

    public HashMap<String, Integer> getTotIntervals(TimerBean timerBean, SettingBean settingBean){
        HashMap<String, Integer> hashMapNumIntervals = new HashMap<>();
        int numIntervalsWork = 0, numIntervalsPreWork = 0, numIntervalsRest = 0, numIntervalsRestRounds = 0;
        int numRounds = convertStringInteger(timerBean.getNumRounds());
        int numSeries = convertStringInteger(timerBean.getNumSeries());

        if(settingBean.isInfoPreWork())
            numIntervalsPreWork = 1;
        if(timerBean.workNotNull() && timerBean.restNotNull()){
            numIntervalsWork = numSeries;
            numIntervalsRest = numSeries - 1;
        }else if(timerBean.workNotNull() && !timerBean.restNotNull())
            numIntervalsWork = numSeries;
        else if(!timerBean.workNotNull() && timerBean.restNotNull())
            numIntervalsRest = numSeries;

        if(numRounds > 1){
            numIntervalsWork = numIntervalsWork * numRounds;
            numIntervalsRest = numIntervalsRest * numRounds;
            if(timerBean.restNotNull())
                numIntervalsRestRounds = numRounds - 1;
        }
        int numTotIntervals = numIntervalsPreWork + numIntervalsWork + numIntervalsRest + numIntervalsRestRounds;

        hashMapNumIntervals.put(UtilityName.INTERVALS_PRE_WORK, numIntervalsPreWork);
        hashMapNumIntervals.put(UtilityName.INTERVALS_WORK, numIntervalsWork);
        hashMapNumIntervals.put(UtilityName.INTERVALS_REST, numIntervalsRest);
        hashMapNumIntervals.put(UtilityName.INTERVALS_TOT, numTotIntervals);
        hashMapNumIntervals.put(UtilityName.INTERVALS_REST_ROUNDS, numIntervalsRestRounds);

        return hashMapNumIntervals;
    }

    public HashMap<String, String> getTotTime(TimerBean timerBean, SettingBean settingBean){
        HashMap<String, String> hashMapTimeIntervals = new HashMap<>();
        int secPreWork = 0, secWork = 0, secRest = 0, secRestRounds = 0;
        int minWork = 0, minRest = 0, minRestRounds = 0;
        int numSeries = convertStringInteger(timerBean.getNumSeries());
        int numRounds = convertStringInteger(timerBean.getNumRounds());

        if(settingBean.isInfoPreWork())
            secPreWork += 5;

        if(timerBean.workNotNull() && timerBean.restNotNull()){
            secWork = convertStringInteger(timerBean.getWorkSec()) * numSeries;
            minWork = convertStringInteger(timerBean.getWorkMin()) * numSeries;
            secRest = convertStringInteger(timerBean.getRestSec()) * (numSeries - 1);
            minRest = convertStringInteger(timerBean.getRestMin()) * (numSeries - 1);
        }else if(timerBean.workNotNull() && !timerBean.restNotNull()){
            secWork = convertStringInteger(timerBean.getWorkSec()) * numSeries;
            minWork = convertStringInteger(timerBean.getWorkMin()) * numSeries;
        }else if(!timerBean.workNotNull() && timerBean.restNotNull()){
            secRest = convertStringInteger(timerBean.getRestSec()) * numSeries;
            minRest = convertStringInteger(timerBean.getRestMin()) * numSeries;
        }
        if(numRounds > 1){
            secWork = secWork * numRounds;
            minWork = minWork * numRounds;
            secRest = secRest * numRounds;
            minRest = minRest * numRounds;
            secRestRounds = convertStringInteger(timerBean.getRestRoundsSec()) * (numRounds - 1);
            minRestRounds = convertStringInteger(timerBean.getRestRoundsMin()) * (numRounds - 1);
        }
        hashMapTimeIntervals.put(UtilityName.INTERVALS_PRE_WORK, "00:0" + secPreWork);
        int totSec = secPreWork + secWork + secRest + secRestRounds;
        int totMin = minWork + minRest + minRestRounds;

        TimerBean supportBean = countSecond("" + secWork, "" + minWork);
        correctFormatSecMin(supportBean);
        hashMapTimeIntervals.put(UtilityName.INTERVALS_WORK, supportBean.getNumMin() + ":" + supportBean.getNumSec());

        supportBean = countSecond("" + secRest, "" + minRest);
        correctFormatSecMin(supportBean);
        hashMapTimeIntervals.put(UtilityName.INTERVALS_REST, supportBean.getNumMin() + ":" + supportBean.getNumSec());

        supportBean = countSecond("" + secRestRounds, "" + minRestRounds);
        correctFormatSecMin(supportBean);
        hashMapTimeIntervals.put(UtilityName.INTERVALS_REST_ROUNDS, supportBean.getNumMin() + ":" + supportBean.getNumSec());

        supportBean = countSecond("" + totSec, "" + totMin);
        correctFormatSecMin(supportBean);
        hashMapTimeIntervals.put(UtilityName.INTERVALS_TOT, supportBean.getNumMin() + ":" + supportBean.getNumSec());

        return hashMapTimeIntervals;
    }

    private void correctFormatSecMin(TimerBean timerBean){
        if(timerBean.getNumSec() == null || timerBean.getNumSec().length() != 2)
            timerBean.setNumSec("00");
        if(timerBean.getNumMin() == null || timerBean.getNumMin().length() != 2)
            timerBean.setNumMin("00");
    }

    private String findLanguageDevice(Context context){
        String language;
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        if("italiano".equals(Locale.getDefault().getDisplayLanguage()) || locale.getLanguage().equals(new Locale("it").getLanguage()))
            language = "it";
        else
            language = "en";
        return language;
    }

    private String checkNumber(String str, int value) {
        try {
            int intApp = Integer.parseInt(str);
            if(intApp > 9){
                str = "" + intApp;
            }else {
                if(value == 1)
                    str = "" + intApp;
                else
                    str = "0" + intApp;
            }
        } catch (Exception e) {
            if(value == 1)
                str = "1";
            else
                str = "00";
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return str;
    }

    public String setCountText(EditText editText) {
        return editText.getText().toString().length() + "/20";
    }

    public void createDialogInfoTimer(TimerBean timerBeanCurrent, SettingBean settingBean, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyleDark);;
        View viewInfoDialog = View.inflate(context, R.layout.alert_info_timer, null);
        builder.setCancelable(false);
        builder.setView(viewInfoDialog);
        HashMap<String, Integer> intervals = getTotIntervals(timerBeanCurrent, settingBean);
        HashMap<String, String> times = getTotTime(timerBeanCurrent, settingBean);

        TextView titleInfoTimerAlert = viewInfoDialog.findViewById(R.id.titleInfoTimerAlert);
        TextView numRounds = viewInfoDialog.findViewById(R.id.numRounds);
        TextView totalInfo = viewInfoDialog.findViewById(R.id.totalInfo);
        TextView preWorkInfo = viewInfoDialog.findViewById(R.id.preWorkInfo);
        TextView workInfo = viewInfoDialog.findViewById(R.id.workInfo);
        TextView restInfo = viewInfoDialog.findViewById(R.id.restInfo);
        TextView restRoundsInfo = viewInfoDialog.findViewById(R.id.restRoundsInfo);

        titleInfoTimerAlert.setText(title);

        String supportString = "" + timerBeanCurrent.getNumRounds();
        numRounds.setText(supportString);
        supportString = times.get(UtilityName.INTERVALS_TOT) + " - " + intervals.get(UtilityName.INTERVALS_TOT) +
                " " + context.getString(R.string.intervals_info_alert_timer);
        totalInfo.setText(supportString);

        supportString = times.get(UtilityName.INTERVALS_PRE_WORK) + " - " + intervals.get(UtilityName.INTERVALS_PRE_WORK) +
                supportIntervals(intervals.get(UtilityName.INTERVALS_PRE_WORK), context);
        preWorkInfo.setText(supportString);

        supportString = times.get(UtilityName.INTERVALS_WORK) + " - " + intervals.get(UtilityName.INTERVALS_WORK) +
                supportIntervals(intervals.get(UtilityName.INTERVALS_WORK), context);
        workInfo.setText(supportString);

        supportString = times.get(UtilityName.INTERVALS_REST) + " - " + intervals.get(UtilityName.INTERVALS_REST) +
                supportIntervals(intervals.get(UtilityName.INTERVALS_REST), context);
        restInfo.setText(supportString);

        supportString = times.get(UtilityName.INTERVALS_REST_ROUNDS) + " - " + intervals.get(UtilityName.INTERVALS_REST_ROUNDS) +
                supportIntervals(intervals.get(UtilityName.INTERVALS_REST_ROUNDS), context);
        restRoundsInfo.setText(supportString);

        Button buttonCancel = viewInfoDialog.findViewById(R.id.buttonCancel);
        final AlertDialog dialog = builder.create();
        if(dialog != null){
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.transparent)));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
        buttonCancel.setOnClickListener(v -> {
            if(dialog != null)
                dialog.dismiss();
        });
        if(dialog != null)
            dialog.show();
    }

    private String supportIntervals(Integer integer, Context context){
        String s = " ";
        if(integer > 1)
            s += context.getString(R.string.intervals_info_alert_timer);
        else
            s += context.getString(R.string.interval_info_alert_timer);
        return s;
    }

}
