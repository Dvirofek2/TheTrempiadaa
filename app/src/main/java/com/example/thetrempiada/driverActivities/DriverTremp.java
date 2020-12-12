package com.example.thetrempiada.driverActivities;

import java.io.Serializable;
import java.util.ArrayList;

public class DriverTremp implements Serializable {
    protected String id;
    protected ArrayList<Tremp> tremps;

    public DriverTremp(String uid, ArrayList<Tremp> tremps) {
        this.id = uid;
        this.tremps = tremps;
    }

    public DriverTremp() {
        tremps = new ArrayList<>();
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public void setTremps(ArrayList<Tremp> tremps) {
        this.tremps = tremps;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Tremp> getTremps() {
        return tremps;
    }
}
