package it.bonny.app.wisetimer2.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class SettingBean implements Serializable {
    private boolean sound;
    private boolean voice;
    private boolean vibration;
    private String languages;
    private String timerStopwatch;
    private boolean infoPreWork;
    private boolean displayOn;
    private boolean sideButtons;
    private boolean showAlertSadeButtons;
    private boolean activeCrash;
    private boolean activeAnalytics;
    private boolean activeMessaging;

    public SettingBean(){}

    public boolean isSound() {
        return sound;
    }
    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isVoice() {
        return voice;
    }
    public void setVoice(boolean voice) {
        this.voice = voice;
    }

    public String getLanguages() {
        return languages;
    }
    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public boolean isVibration(){ return vibration; }
    public void setVibration(boolean vibration){ this.vibration = vibration; }

    public  boolean isInfoPreWork(){ return  infoPreWork; }
    public void setInfoPreWork(boolean infoPreWork) { this.infoPreWork = infoPreWork; }

    public boolean isDisplayOn() {
        return displayOn;
    }
    public void setDisplayOn(boolean displayOn) {
        this.displayOn = displayOn;
    }

    public boolean isSideButtons() {
        return sideButtons;
    }
    public void setSideButtons(boolean sideButtons) {
        this.sideButtons = sideButtons;
    }

    public boolean isShowAlertSadeButtons() {
        return showAlertSadeButtons;
    }
    public void setShowAlertSadeButtons(boolean showAlertSadeButtons) {
        this.showAlertSadeButtons = showAlertSadeButtons;
    }

    public boolean isActiveCrash() {
        return activeCrash;
    }
    public void setActiveCrash(boolean activeCrash) {
        this.activeCrash = activeCrash;
    }

    public boolean isActiveAnalytics() {
        return activeAnalytics;
    }
    public void setActiveAnalytics(boolean activeAnalytics) {
        this.activeAnalytics = activeAnalytics;
    }

    public boolean isActiveMessaging() {
        return activeMessaging;
    }
    public void setActiveMessaging(boolean activeMessaging) {
        this.activeMessaging = activeMessaging;
    }

    public boolean isItalian(){
        boolean result = false;
        if("it".equalsIgnoreCase(languages))
            result = true;
        return result;
    }

    public boolean isEngland(){
        boolean result = false;
        if("en".equalsIgnoreCase(languages))
            result = true;
        return  result;
    }

    public String getTimerStopwatch() {
        return timerStopwatch;
    }
    public void setTimerStopwatch(String timerStopwatch) {
        this.timerStopwatch = timerStopwatch;
    }

    @NonNull
    public String toString(){
        return " voice: " + voice + " sound: " + sound + " languages: " + languages + " vibration: " + vibration +
                " activeCrash: " + activeCrash + " activeAnalytics: " + activeAnalytics +
                " activeMessaging: " + activeMessaging + " timerStopwatch: " + timerStopwatch;
    }
}
