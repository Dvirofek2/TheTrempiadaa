package com.example.thetrempiada.driverActivities;

import java.util.ArrayList;

public class DriverTremp {
    protected String uid;
    protected ArrayList<Tremp> tremps;

    public DriverTremp(String uid, ArrayList<Tremp> tremps) {
        this.uid = uid;
        this.tremps = tremps;
    }

    public DriverTremp() {
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTremps(ArrayList<Tremp> tremps) {
        this.tremps = tremps;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<Tremp> getTremps() {
        return tremps;
    }
}
