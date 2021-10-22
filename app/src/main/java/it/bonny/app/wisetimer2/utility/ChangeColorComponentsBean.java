package it.bonny.app.wisetimer2.utility;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.bonny.app.wisetimer2.bean.SettingBean;

public class ChangeColorComponentsBean {
    private Context context;
    private SettingBean settingBean;
    private TextView countTime;
    private TextView numSeries;
    private TextView numRounds;
    private TextView strSeriesTimerAct;
    private TextView strRoundsTimerAct;
    private TextView stringView;
    private ProgressBar progressBarCircle;
    private FloatingActionButton fabPreWorkStop;
    private FloatingActionButton fabPreWorkPausePlay;
    private FloatingActionButton lockDisplay;
    private RelativeLayout borderBtn;
    private String type;

    public ChangeColorComponentsBean(){}

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    SettingBean getSettingBean() {
        return settingBean;
    }
    public void setSettingBean(SettingBean settingBean) {
        this.settingBean = settingBean;
    }

    TextView getCountTime() {
        return countTime;
    }
    public void setCountTime(TextView countTime) {
        this.countTime = countTime;
    }

    public TextView getNumSeries() {
        return numSeries;
    }
    public void setNumSeries(TextView numSeries) {
        this.numSeries = numSeries;
    }

    TextView getNumRounds() {
        return numRounds;
    }
    public void setNumRounds(TextView numRounds) {
        this.numRounds = numRounds;
    }

    TextView getStringView() {
        return stringView;
    }
    public void setStringView(TextView stringView) {
        this.stringView = stringView;
    }

    ProgressBar getProgressBarCircle() {
        return progressBarCircle;
    }
    public void setProgressBarCircle(ProgressBar progressBarCircle) {
        this.progressBarCircle = progressBarCircle;
    }

    FloatingActionButton getFabPreWorkStop() {
        return fabPreWorkStop;
    }
    public void setFabPreWorkStop(FloatingActionButton fabPreWorkStop) {
        this.fabPreWorkStop = fabPreWorkStop;
    }

    FloatingActionButton getFabPreWorkPausePlay() {
        return fabPreWorkPausePlay;
    }
    public void setFabPreWorkPausePlay(FloatingActionButton fabPreWorkPausePlay) {
        this.fabPreWorkPausePlay = fabPreWorkPausePlay;
    }

    FloatingActionButton getLockDisplay() {
        return lockDisplay;
    }
    public void setLockDisplay(FloatingActionButton lockDisplay) {
        this.lockDisplay = lockDisplay;
    }

    RelativeLayout getBorderBtn() {
        return borderBtn;
    }
    public void setBorderBtn(RelativeLayout borderBtn) {
        this.borderBtn = borderBtn;
    }

    String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    TextView getStrSeriesTimerAct() {
        return strSeriesTimerAct;
    }
    public void setStrSeriesTimerAct(TextView strSeriesTimerAct) {
        this.strSeriesTimerAct = strSeriesTimerAct;
    }

    TextView getStrRoundsTimerAct() {
        return strRoundsTimerAct;
    }
    public void setStrRoundsTimerAct(TextView strRoundsTimerAct) {
        this.strRoundsTimerAct = strRoundsTimerAct;
    }

}
