package it.bonny.app.wisetimer2.bean;

import androidx.annotation.NonNull;

public class StopwatchBean implements Comparable<StopwatchBean>{
    public final static String best = "BEST";
    public final static String worse = "WORSE";
    private String lap;
    private String timePassed;
    private String time;
    private String resultLap;
    private Integer numLap;
    private int minutes;
    private int seconds;
    private int millisSeconds;
    private Integer numIndexInList;


    public StopwatchBean(){}

    public String getLap() {
        return lap;
    }

    public void setLap(String lap) {
        this.lap = lap;
    }

    public String getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(String timePassed) {
        this.timePassed = timePassed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResultLap() {
        return resultLap;
    }

    public void setResultLap(String resultLap) {
        this.resultLap = resultLap;
    }

    private Integer getNumLap() {
        return numLap;
    }
    public void setNumLap(Integer numLap) {
        this.numLap = numLap;
    }

    public int getMinutes() {
        return minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMillisSeconds() {
        return millisSeconds;
    }
    public void setMillisSeconds(int millisSeconds) {
        this.millisSeconds = millisSeconds;
    }

    public Integer getNumIndexInList() {
        return numIndexInList;
    }
    public void setNumIndexInList(Integer numIndexInList) {
        this.numIndexInList = numIndexInList;
    }

    @Override
    public int compareTo(StopwatchBean o) {
        return this.numLap.compareTo(o.numLap);
    }

    @NonNull
    public String toString(){
        String buffer = "";
        buffer = buffer + "index=[" + numIndexInList + "]";
        buffer = buffer + "numLap=[" + numLap + "]";
        buffer = buffer + "minutes=[" + minutes + "]";
        buffer = buffer + "seconds=[" + seconds + "]";
        buffer = buffer + "millisSeconds=[" + millisSeconds + "]";
        return buffer;
    }

    public void copy(StopwatchBean bean){
        this.minutes = bean.getMinutes();
        this.seconds = bean.getSeconds();
        this.millisSeconds = bean.getMillisSeconds();
        this.numLap = bean.getNumLap();
        this.numIndexInList = bean.getNumIndexInList();
    }
}
