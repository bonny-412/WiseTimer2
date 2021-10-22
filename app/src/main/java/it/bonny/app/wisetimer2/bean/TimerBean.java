package it.bonny.app.wisetimer2.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TimerBean implements Serializable {
    private long id;
    private String name;
    private String numSeries;
    private String numMin;
    private String numSec;
    private String workSec;
    private String workMin;
    private String restSec;
    private String restMin;
    private String timerState;
    private String numRounds;
    private String restRoundsSec;
    private String restRoundsMin;

    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public TimerBean(){
        super();
    }

    public TimerBean(String numSeries, String workSec, String workMin, String restSec, String restMin, String numRounds,
                     String restRoundsSec, String restRoundsMin){
        super();
        this.numSeries = numSeries;
        this.workSec = workSec;
        this.workMin = workMin;
        this.restSec = restSec;
        this.restMin = restMin;
        this.numRounds = numRounds;
        this.restRoundsSec = restRoundsSec;
        this.restRoundsMin = restRoundsMin;
    }

    public String getNumMin(){
        return this.numMin;
    }
    public void setNumMin(String numMin){
        this.numMin = numMin;
    }

    public String getNumSec(){
        return this.numSec;
    }
    public void setNumSec(String numSec){
        this.numSec = numSec;
    }

    public String getWorkSec() {
        return workSec;
    }
    public void setWorkSec(String workSec) {
        this.workSec = workSec;
    }

    public String getWorkMin() {
        return workMin;
    }
    public void setWorkMin(String workMin) {
        this.workMin = workMin;
    }

    public String getRestSec() {
        return restSec;
    }
    public void setRestSec(String restSec) {
        this.restSec = restSec;
    }

    public String getRestMin() {
        return restMin;
    }
    public void setRestMin(String restMin) {
        this.restMin = restMin;
    }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public long getId(){return  id;}
    public void setId(long id){this.id = id;}

    public String getNumSeries(){
        return this.numSeries;
    }
    public void setNumSeries(String numSeries){
        this.numSeries = numSeries;
    }

    public String getTimerState(){ return this.timerState; }
    public void setTimerState(String timerState) { this.timerState = timerState; }

    public String getNumRounds(){ return this.numRounds; }
    public void setNumRounds(String numRounds) { this.numRounds = numRounds; }

    public String getRestRoundsSec(){
        return this.restRoundsSec;
    }
    public void setRestRoundsSec(String restRoundsSec){
        this.restRoundsSec = restRoundsSec;
    }

    public String getRestRoundsMin(){ return this.restRoundsMin; }
    public void setRestRoundsMin(String restRoundsMin){
        this.restRoundsMin = restRoundsMin;
    }

    @NonNull
    public String toString(){
        String str = "name: " + name + " numSeries: " + numSeries + " numMin: " + numMin + " numSec: " + numSec;
        str = str + " workSec: " + workSec + " workMin: " + workMin + " restSec: " + restSec + " restMin: " + restMin + " checked: " + checked;
        str = str + " timerState: " + timerState;
        str += " numRounds: " + numRounds + " restRoundsMin: " + restRoundsMin + " restRoundsSec: " + restRoundsSec;
        return str;
    }

    public boolean workNotNull(){
        boolean result = true;
        if("00".equalsIgnoreCase(this.workSec) && "00".equalsIgnoreCase(this.workMin))
            result = false;
        return  result;
    }

    public boolean restNotNull(){
        boolean result = true;
        if("00".equalsIgnoreCase(this.restSec) && "00".equalsIgnoreCase(this.restMin))
            result = false;
        return  result;
    }

    public boolean isEqual(TimerBean bean){
        boolean result = true;
        if(bean != null){
            if(bean.id != this.getId())
                result = false;
            else if(!bean.getName().equals(this.getName()))
                result = false;
            else if(!bean.getNumSeries().equals(this.getNumSeries()))
                result = false;
            else if(!bean.getRestMin().equals(this.getRestMin()))
                result = false;
            else if(!bean.getRestSec().equals(this.getRestSec()))
                result = false;
            else if(!bean.getWorkMin().equals(this.getWorkMin()))
                result = false;
            else if(!bean.getWorkSec().equals(this.getWorkSec()))
                result = false;
            else if(!bean.getNumRounds().equals(this.getNumRounds()))
                result = false;
            else if(!bean.getRestRoundsMin().equals(this.getRestRoundsMin()))
                result = false;
            else if(!bean.getRestRoundsSec().equals(this.getRestRoundsSec()))
                result = false;
        }else {
            result = false;
        }
        return result;
    }

    public void copy(TimerBean bean){
        if(bean != null){
            this.setId(bean.getId());
            this.setName(bean.getName());
            this.setNumSeries(bean.getNumSeries());
            this.setNumMin(bean.getNumMin());
            this.setNumSec(bean.getNumSec());
            this.setWorkSec(bean.getWorkSec());
            this.setWorkMin(bean.getWorkMin());
            this.setRestSec(bean.getRestSec());
            this.setRestMin(bean.getRestMin());
            this.setTimerState(bean.getTimerState());
            this.setNumRounds(bean.getNumRounds());
            this.setRestRoundsMin(bean.getRestRoundsMin());
            this.setRestRoundsSec(bean.getRestRoundsSec());
        }
    }
}