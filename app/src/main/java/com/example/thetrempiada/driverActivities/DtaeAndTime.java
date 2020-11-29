package com.example.thetrempiada.driverActivities;

import com.google.gson.internal.bind.DateTypeAdapter;

import java.io.Serializable;

public class DtaeAndTime implements Serializable {
    protected int hour,min,day,year,month;

    public DtaeAndTime(int hour, int min, int day, int year, int month) {
        this.hour = hour;
        this.min = min;
        this.day = day;
        this.year = year;
        this.month = month;
    }

    public DtaeAndTime(){}

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String toString(){
        String s = "";
        s+=hour+":"+min+"\n"+day+"/"+month+"/"+year;
        return s;
    }
}
